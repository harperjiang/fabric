package loader;

import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.gateway.*;
import org.papernet.CommercialPaper;

import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class LoaderRunner {

    public static void main(String[] args) {

        int NUM_ROUND = 0;
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
    }

    static Path connectionProfile = null;
    static String networkName = null;

    static final String CHAINCODE_NAME = "";
    static final String CONTRACT_NAME = "";

    Connection digibankConnection;
    Connection magnetocorpConnection;

    Contract digibank;
    Contract magnetocorp;

    public LoaderRunner() {
        digibankConnection = Connection.connectAs(connectionProfile, networkName, new Role.Digibank());
        magnetocorpConnection = Connection.connectAs(connectionProfile, networkName, new Role.MagnetoCorp());

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
        paper = execute(magnetocorp, "transfer", "MagnetoCorp", paperNum, "MagnetoCorp", "2022-05-01");
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
}

