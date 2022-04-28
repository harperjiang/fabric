package client.loader;

import client.ClientAppConfig;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.gateway.*;
import commercialpaper.papernet.CommercialPaper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class LoaderRunner implements AutoCloseable {

    public static void main(String[] args) throws Exception {

        int NUM_ROUND = Integer.valueOf(args[0]);
        int length = String.valueOf(NUM_ROUND).length() + 1;
        Random rand = new Random(System.currentTimeMillis());

        LoaderRunner runner = new LoaderRunner();

        for (int i = 0; i < NUM_ROUND; ++i) {
            String paperNumber = StringUtils.leftPad(String.valueOf(i), length, '0');
            int next = rand.nextInt(3);
            switch (next) {
                case 0:
                    runner.runRoute1(paperNumber);
                    break;
                case 1:
                    runner.runRoute2(paperNumber);
                    break;
                case 2:
                    runner.runRoute3(paperNumber);
                    break;
            }
        }
        runner.close();
    }

    public static String NETWORK_NAME = "mychannel";

    public static final String CHAINCODE_NAME = "papercontract";
    public static final String CONTRACT_NAME = "org.papernet.commercialpaper";

    Connection digibankConnection;
    Connection magnetocorpConnection;

    Contract digibank;
    Contract magnetocorp;

    public LoaderRunner() {
        Path con1path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
        Path con2path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org2.example.com", "connection-org2.yaml");

        digibankConnection = Connection.connectAs(con1path, NETWORK_NAME, new Role.Digibank());
        magnetocorpConnection = Connection.connectAs(con2path, NETWORK_NAME, new Role.MagnetoCorp());

        digibank = digibankConnection.getContract(CHAINCODE_NAME, CONTRACT_NAME);
        magnetocorp = magnetocorpConnection.getContract(CHAINCODE_NAME, CONTRACT_NAME);
    }

    void runRoute1(String paperNum) {
        // Each loop finish a round of issue-buy-redeem cycle. Random pause between the operations
        CommercialPaper paper = execute(magnetocorp, "issue", "MagnetoCorp", paperNum, "2020-05-31", "2020-11-30", "5000000");
        paper = execute(digibank, "buy", "MagnetoCorp", paperNum, "MagnetoCorp", "DigiBank", "4900000", "2020-05-31");
        paper = execute(digibank, "redeem", "MagnetoCorp", paperNum, "DigiBank", "2020-11-30");
    }

    void runRoute2(String paperNum) {
        CommercialPaper paper = execute(magnetocorp, "issue", "MagnetoCorp", paperNum, "2020-05-31", "2020-11-30", "5000000");
        paper = execute(digibank, "buyrequest", "MagnetoCorp", paperNum, "MagnetoCorp", "DigiBank", "520000", "2022-05-01");
        paper = execute(magnetocorp, "transfer", "MagnetoCorp", paperNum, "DigiBank", "2022-05-01");
        paper = execute(digibank, "redeem", "MagnetoCorp", paperNum, "DigiBank", "2022-05-01");
    }

    void runRoute3(String paperNum) {
        CommercialPaper paper = execute(magnetocorp, "issue", "MagnetoCorp", paperNum, "2020-05-31", "2020-11-30", "5000000");
        paper = execute(digibank, "buyrequest", "MagnetoCorp", paperNum, "MagnetoCorp", "DigiBank", "520000", "2022-05-01");
        paper = execute(magnetocorp, "reject", "MagnetoCorp", paperNum);
    }

    CommercialPaper execute(Contract contract, String method, String... parameters) {
        try {
            byte[] response = contract.submitTransaction(method, parameters);
            CommercialPaper paper = CommercialPaper.deserialize(response);
            return paper;
        } catch (GatewayException | TimeoutException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        digibankConnection.close();
        magnetocorpConnection.close();
    }
}

