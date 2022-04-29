package client.load;

import client.ClientAppConfig;
import client.load.step.RouteOne;
import client.load.step.RouteThree;
import client.load.step.RouteTwo;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.gateway.*;

import java.nio.file.Path;
import java.util.Random;

public class LoadRunner implements AutoCloseable {

    public static void main(String[] args) throws Exception {

        int NUM_ROUND = Integer.valueOf(args[0]);
        int length = String.valueOf(NUM_ROUND).length() + 1;
        Random rand = new Random(System.currentTimeMillis());

        LoadRunner runner = new LoadRunner();

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
    Contract magneto;

    public LoadRunner() {
        Path con1path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
        Path con2path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org2.example.com", "connection-org2.yaml");

        digibankConnection = Connection.connectAs(con1path, NETWORK_NAME, new Role.Digibank());
        magnetocorpConnection = Connection.connectAs(con2path, NETWORK_NAME, new Role.MagnetoCorp());

        digibank = digibankConnection.getContract(CHAINCODE_NAME, CONTRACT_NAME);
        magneto = magnetocorpConnection.getContract(CHAINCODE_NAME, CONTRACT_NAME);
    }

    void runRoute1(String paperNum) throws Exception {
        new RouteOne().execute(magneto, digibank, paperNum);
    }

    void runRoute2(String paperNum) throws Exception {
        new RouteTwo().execute(magneto, digibank, paperNum);
    }

    void runRoute3(String paperNum) throws Exception {
        new RouteThree().execute(magneto, digibank, paperNum);
    }

    @Override
    public void close() throws Exception {
        digibankConnection.close();
        magnetocorpConnection.close();
    }
}

