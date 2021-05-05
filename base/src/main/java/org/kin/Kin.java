package org.kin;

import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.kin.sdk.base.KinAccountContext;
import org.kin.sdk.base.KinAccountContextImpl;
import org.kin.sdk.base.KinEnvironment;
import org.kin.sdk.base.ObservationMode;
import org.kin.sdk.base.models.*;
import org.kin.sdk.base.network.services.AppInfoProvider;
import org.kin.sdk.base.stellar.models.NetworkEnvironment;
import org.kin.sdk.base.storage.KinFileStorage;
import org.kin.sdk.base.tools.Base58;
import org.kin.sdk.base.tools.DisposeBag;
import org.kin.sdk.base.tools.Observer;
import org.kin.sdk.base.tools.Optional;

import java.util.List;

public final class Kin {

    private final boolean production;
    private final int appIndex;
    private final String appAddress;
    private final String credentialsUser;
    private final String credentialsPass;
    private final Function1<KinBalance, Unit> balanceChanged;
    private final Function1<List<? extends KinPayment>, Unit> paymentHappened;

    private final DisposeBag lifecycle;

    private final KinEnvironment.Agora environment;
    private KinAccountContext context;
    private Observer<List<? extends KinPayment>> observerPayments;
    private Observer<KinBalance> observerBalance;

    /**
     * Performs operations for a [KinAccount].
     *
     * @param production      Boolean indicating if [NetworkEnvironment] is in production or test
     * @param appIndex        App Index assigned by the Kin Foundation
     * @param appAddress      Blockchain address for the app in stellarBase32Encoded format
     * @param credentialsUser User id of [AppUserCreds] sent to your webhook for authentication
     * @param credentialsPass Password of [AppUserCreds] sent to your webhook for authentication
     * @param onBalanceChange Callback [balanceChanged] to notify the app of balance changes
     * @param onPayment       Callback [paymentHappened] to notify the app of balance changes
     */

    public Kin(
            boolean production,
            int appIndex,
            String appAddress,
            String credentialsUser,
            String credentialsPass,
            final Function1<KinBalance, Unit> onBalanceChange,
            final Function1<List<? extends KinPayment>, Unit> onPayment,
            final Function1<Kin, Unit> onAccountContext
    ) {
        this.production = production;
        this.appIndex = appIndex;
        this.appAddress = appAddress;
        this.credentialsUser = credentialsUser;
        this.credentialsPass = credentialsPass;
        this.balanceChanged = onBalanceChange;
        this.paymentHappened = onPayment;
        this.lifecycle = new DisposeBag();

        //fetch the account and set the context
        this.environment = this.getEnvironment();
        this.environment.allAccountIds().then(it -> {
            //First get (or create) an account id for this device
            String accountId = it.size() == 0 ? createAccount() : ((KinAccount.Id) it.get(0)).stellarBase32Encode();

            //Then set the context with that single account
            this.context = this.getKinContext(accountId);

            if (onAccountContext != null) {
                onAccountContext.invoke(this);
            }

            return null;
        });

        //handle listeners
        if (this.balanceChanged != null) {
            this.watchBalance(); //watch for changes in balance
        }

        if (this.paymentHappened != null) {
            this.watchPayments(); //watch for changes in payments
        }
    }


    /**
     * Return the device's blockchain address
     */
    public String address() {
        return this.context.getAccountId().base58Encode();
    }

    /**
     * Force the balance and payment listeners to refresh, to get transactions not initiated by this device
     */
    public final void checkTransactions() {
        if (this.observerBalance != null) {
            this.observerBalance.requestInvalidation();
        }

        if (this.observerPayments != null) {
            this.observerPayments.requestInvalidation();
        }
    }

    /**
     * Sends Kin to the designated address
     *
     * @param paymentItems     List of items and costs in a single transaction.
     * @param address          Destination address
     * @param paymentType      [KinBinaryMemo.TransferType] of Earn, Spend or P2P (for record keeping)
     * @param paymentSucceeded callback to indicate completion or failure of a payment
     */
    public final void sendKin(
            List<Pair<String, Double>> paymentItems,
            String address,
            KinBinaryMemo.TransferType paymentType,
            final Function2<KinPayment, Throwable, Unit> paymentSucceeded) {

        KinAccount.Id kinAccount = this.kinAccount(address);
        Invoice invoice = this.buildInvoice(paymentItems);
        double amount = this.invoiceTotal(paymentItems);

        this.context.sendKinPayment(
                new KinAmount(amount),
                kinAccount,
                this.buildMemo(invoice, paymentType),
                Optional.Companion.of(invoice)
        ).then(((Function1<KinPayment, Unit>) payment -> {
            if (paymentSucceeded != null) {
                paymentSucceeded.invoke(payment, null);
            }
            return null;
        }), ((Function1<Throwable, Unit>) error -> {
            if (paymentSucceeded != null) {
                paymentSucceeded.invoke(null, error);
            }
            return null;
        }));
    }


    private double invoiceTotal(List<Pair<String, Double>> paymentItems) {
        double total = 0.0D;

        for (Pair<String, Double> it : paymentItems) {
            total += it.getSecond();
        }

        return total;
    }

    private Invoice buildInvoice(List<Pair<String, Double>> paymentItems) {

        Invoice.Builder invoiceBuilder = new Invoice.Builder();

        for (Pair<String, Double> it : paymentItems) {
            LineItem item = new LineItem.Builder(it.getFirst(), new KinAmount(it.getSecond())).build();
            invoiceBuilder.addLineItems(CollectionsKt.listOf(item));
        }

        return invoiceBuilder.build();
    }


    private KinMemo buildMemo(
            Invoice invoice,
            KinBinaryMemo.TransferType transferType
    ) {
        KinBinaryMemo.Builder memo = new KinBinaryMemo.Builder(this.appIndex).setTranferType(transferType);
        InvoiceList invoiceList = (new InvoiceList.Builder()).addInvoice(invoice).build();

        memo.setForeignKey(invoiceList.getId().getInvoiceHash().decode());

        return memo.build().toKinMemo();
    }

    private KinAccount.Id kinAccount(String accountId) {
        //resolve between Solana and Stellar format addresses
        try {
            return new KinAccount.Id(Base58.INSTANCE.decode(accountId)); //Solana format
        } catch (Exception var4) {
            return new KinAccount.Id(accountId); //Stellar format
        }
    }

    private void watchPayments() {
        observerPayments = context.observePayments(ObservationMode.Passive.INSTANCE)
                .add((Function1<? super List<? extends KinPayment>, Unit>) payments -> {
                    if (paymentHappened != null) {
                        paymentHappened.invoke(payments);
                    }
                    return null;
                }).disposedBy(this.lifecycle);
    }

    private void watchBalance() {
        //watch for changes to this account
        observerBalance = context.observeBalance(ObservationMode.Passive.INSTANCE)
                .add((Function1<KinBalance, Unit>) kinBalance -> {
                    if (balanceChanged != null) {
                        balanceChanged.invoke(kinBalance);
                    }
                    return null;
                })
                .disposedBy(lifecycle);
    }


    private KinAccountContext getKinContext(String accountId) {
        return new KinAccountContext.Builder(this.environment)
                .useExistingAccount(new KinAccount.Id(accountId))
                .build();
    }

    private String createAccount() {
        KinAccountContextImpl kinContext = new KinAccountContext.Builder(environment)
                .createNewAccount()
                .build();
        return kinContext.getAccountId().stellarBase32Encode();
    }

    private KinEnvironment.Agora getEnvironment() {
        String storageLoc = "/tmp/kin";

        NetworkEnvironment networkEnv = this.production ? NetworkEnvironment.KinStellarMainNetKin3.INSTANCE :
                NetworkEnvironment.KinStellarTestNetKin3.INSTANCE;

        return (new KinEnvironment.Agora.Builder(networkEnv))
                .setAppInfoProvider(new AppInfoProvider() {
                    @NotNull
                    private final AppInfo appInfo;

                    @NotNull
                    public AppInfo getAppInfo() {
                        return this.appInfo;
                    }

                    @NotNull
                    public AppUserCreds getPassthroughAppUserCredentials() {
                        return new AppUserCreds(
                                credentialsUser,
                                credentialsPass
                        );
                    }

                    {
                        this.appInfo = new AppInfo(
                                new AppIdx(appIndex),
                                new KinAccount.Id(appAddress),
                                "Example",
                                0
                        );
                    }
                })
                .setMinApiVersion(4) //make sure we're on the Agora chain (not the former stellar)
                .setStorage(new KinFileStorage.Builder(storageLoc))
                .build();
    }

    @Override
    public String toString() {
        return "Kin{" +
                "production=" + production +
                ", appAddress='" + appAddress + '\'' +
                ", credentialsUser='" + credentialsUser + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
