package example;

import org.conscrypt.OpenSSLProvider
import org.kin.Kin
import java.security.Security

fun main() {

    Security.addProvider(OpenSSLProvider());

    var kin = Kin(false, 0, "Example App", "credentialUser", "credentialPass",
            { balance -> println("[Balance Change] $balance") },
            { payments -> println("[Payments] $payments") },
            { accountContext -> println("[Account Context] $accountContext : address: ${accountContext.address()}") }
    );

    print(kin);

}


main()