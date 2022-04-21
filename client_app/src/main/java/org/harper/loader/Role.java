package org.harper.loader;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.impl.identity.InMemoryWalletStore;
import org.hyperledger.fabric.gateway.spi.WalletStore;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Role {

    private String username;

    private Wallet wallet;

    public Role(String username) {
        this.username = username;
        try {
            this.wallet = Wallets.newFileSystemWallet(Paths.get(".","wallet"));
        } catch (Exception e) {
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
            super("User1@org1.example.com");
        }
    }

    public static class MagnetoCorp extends Role {
        public MagnetoCorp() {
            super("User1@org2.example.com");
        }
    }

}
