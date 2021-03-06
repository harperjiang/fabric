package client.load;

import org.hyperledger.fabric.gateway.*;

import java.io.IOException;
import java.nio.file.Path;

public class Connection implements AutoCloseable {

    private Gateway gateway;
    private Network network;

    private Connection() {
    }

    public static Connection connectAs(Path profile, String networkName, Role role) {
        Gateway.Builder builder = Gateway.createBuilder();
        try {
            // Set connection options on the gateway builder
            builder.identity(role.getWallet(), role.getUsername()).networkConfig(profile).discovery(false);
            // Connect to gateway using application specified parameters
            Gateway gateway = builder.connect();
            Network network = gateway.getNetwork(networkName);

            Connection connection = new Connection();
            connection.gateway = gateway;
            connection.network = network;

            return connection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Contract getContract(String chaincodeName, String contractName) {
        Contract contract = network.getContract(chaincodeName, contractName);
        return contract;
    }

    @Override
    public void close() throws Exception {
        if (null != gateway) {
            gateway.close();
        }
    }
}
