package client.loader;

import client.ClientAppConfig;
import client.loader.step.RouteOne;
import client.loader.step.RouteThree;
import client.loader.step.RouteTwo;
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

    }

    void runRoute1(String paperNum) throws Exception {
        new RouteOne().execute(magnetocorpConnection, digibankConnection, paperNum);
    }

    void runRoute2(String paperNum) throws Exception {
        new RouteTwo().execute(magnetocorpConnection, digibankConnection, paperNum);
    }

    void runRoute3(String paperNum) throws Exception {
        new RouteThree().execute(magnetocorpConnection, digibankConnection, paperNum);
    }

    @Override
    public void close() throws Exception {
        digibankConnection.close();
        magnetocorpConnection.close();
    }
}

