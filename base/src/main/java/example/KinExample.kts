package example;

import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.AppUserCreds
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.network.services.AppInfoProvider
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import java.security.Security
import org.conscrypt.OpenSSLProvider

fun main() {

    Security.addProvider( OpenSSLProvider() );

    var kin = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setAppInfoProvider(object : AppInfoProvider {
                override val appInfo: AppInfo =
                        AppInfo(
                                DemoAppConfig.DEMO_APP_IDX,
                                DemoAppConfig.DEMO_APP_ACCOUNT_ID,
                                "Kin Demo App",
                                0
                        )

                override fun getPassthroughAppUserCredentials(): AppUserCreds {
                    return AppUserCreds("demo_app_uid", "demo_app_user_passkey")
                }
            })
            .testMigration()
            .setEnableLogging()
            .setStorage(KinFileStorage.Builder("/tmp/kin"))
            .build()

    print(kin);

    setupTestAccounts(kin);

}

fun setupTestAccounts(testKinEnvironment: KinEnvironment) {
    KinAccountContext.Builder(testKinEnvironment)
            .importExistingPrivateKey(Key.PrivateKey(DemoAppConfig.DEMO_APP_SECRET_SEED))
            .build()
            .getAccount()
            .then {
                println("Setup KinAccount: $it")
            }
}

main()