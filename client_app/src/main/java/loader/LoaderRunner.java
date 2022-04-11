package loader;

import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.gateway.*;
import org.papernet.CommercialPaper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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

    Role digibank;
    Role magnetocorp;

    void runRoute1(String paperNum) {
        // Each loop finish a round of issue-buy-redeem cycle. Random pause between the operations
        CommercialPaper paper = executeAs(magnetocorp, "issue", "MagnetoCorp", paperNum, "2020-05-31", "2020-11-30", "5000000");
        paper = executeAs(digibank, "buy", "MagnetoCorp", paperNum, "MagnetoCorp", "DigiBank", "4900000", "2020-05-31");
        paper = executeAs(digibank, "redeem", "MagnetoCorp", paperNum, "DigiBank", "2020-11-30");
    }

    void runRoute2(String paperNum) {
        CommercialPaper paper = executeAs(magnetocorp, "issue", "MagnetoCorp", paperNum, "2020-05-31", "2020-11-30", "5000000");
        paper = executeAs(digibank, "buyrequest", "MagnetoCorp", paperNum, "MagnetoCorp", "DigiBank", "520000", "2022-05-01");
        paper = executeAs(magnetocorp, "transfer", "MagnetoCorp", paperNum, "MagnetoCorp", "2022-05-01");
    }

    void runRoute3(String paperNum) {
        CommercialPaper paper = executeAs(magnetocorp, "issue", "MagnetoCorp", paperNum, "2020-05-31", "2020-11-30", "5000000");
        paper = executeAs(digibank, "buyrequest", "MagnetoCorp", paperNum, "MagnetoCorp", "DigiBank", "520000", "2022-05-01");
        paper = executeAs(magnetocorp, "reject", "MagnetoCorp", paperNum);
    }

    CommercialPaper executeAs(Role role, String... parameters) {
        Gateway.Builder builder = Gateway.createBuilder();

        String contractName="papercontract";
        // get the name of the contract, in case it is overridden
        Map<String,String> envvar = System.getenv();
        if (envvar.containsKey(ENVKEY)){
            contractName=envvar.get(ENVKEY);
        }

        try {
            // A wallet stores a collection of identities
            Path walletPath = Paths.get(".", "wallet");
            Wallet wallet = Wallets.newFileSystemWallet(walletPath);
            System.out.println("Read wallet info from: " + walletPath);

            String userName = "User1@org1.example.com";

            Path connectionProfile = Paths.get("..", "gateway", "connection-org1.yaml");

            // Set connection options on the gateway builder
            builder.identity(wallet, userName).networkConfig(connectionProfile).discovery(false);

            // Connect to gateway using application specified parameters
            try(Gateway gateway = builder.connect()) {

                // Access PaperNet network
                System.out.println("Use network channel: mychannel.");
                Network network = gateway.getNetwork("mychannel");

                // Get addressability to commercial paper contract
                System.out.println("Use org.papernet.commercialpaper smart contract.");
                Contract contract = network.getContract(contractName, "org.papernet.commercialpaper");

                // Buy commercial paper
                System.out.println("Submit commercial paper buy transaction.");
                byte[] response = contract.submitTransaction("buy", "MagnetoCorp", "00001", "MagnetoCorp", "DigiBank", "4900000", "2020-05-31");

                // Process response
                System.out.println("Process buy transaction response.");
                CommercialPaper paper = CommercialPaper.deserialize(response);
                System.out.println(paper);
            }
        } catch (GatewayException | IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
}

