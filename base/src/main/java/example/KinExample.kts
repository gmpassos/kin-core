package example;

import org.conscrypt.OpenSSLProvider
import org.kin.Kin
import java.security.Security

fun main() {

    Security.addProvider(OpenSSLProvider());

    var kin = Kin(false, 0, "GDV4TKOCDBHB3XGCKAXWYETQRIN4RTJKSD6FQV43E2AUHORR56B4YDC4", "credentialUser", "credentialPass",
            { balance -> println("[Balance Change] $balance") },
            { payments -> println("[Payments] $payments") },
            { accountContext -> println("[Account Context] $accountContext : address: ${accountContext.address()}") }
    );

    print(kin);

}


main()