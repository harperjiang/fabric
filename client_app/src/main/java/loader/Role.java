package loader;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import java.io.IOException;
import java.nio.file.Path;

public abstract class Role {

    private String username;

    private String walletPath;

    private Wallet wallet;

    public Role(String username, String walletPath) {
        this.username = username;
        this.walletPath = walletPath;
        try {
            this.wallet = Wallets.newFileSystemWallet(Path.of(walletPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Wallet getWallet() {
        return this.wallet;
    }

    public String getUsername() {
        return this.username;
    }

    public static class Digibank extends Role {
        public Digibank() {
            super("", "");
        }
    }

    public static class MagnetoCorp extends Role {
        public MagnetoCorp() {
            super("", "");
        }
    }

}
