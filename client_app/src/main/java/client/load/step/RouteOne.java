package client.load.step;

import client.ClientAppConfig;
import client.load.*;
import commercialpaper.papernet.CommercialPaper;
import org.hyperledger.fabric.gateway.Contract;
import org.slf4j.*;

import java.nio.file.Path;

public class RouteOne {

    Logger logger = LoggerFactory.getLogger(getClass());

    StopWatch stopWatch = new StopWatch();

    public static void main(String[] args) throws Exception {
        Path con1path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
        Path con2path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org2.example.com", "connection-org2.yaml");
        try (Connection magcon = Connection.connectAs(con2path, "mychannel", new Role.MagnetoCorp());
             Connection digibankcon = Connection.connectAs(con1path, "mychannel", new Role.Digibank())) {
            Contract magcontract = magcon.getContract(LoadRunner.CHAINCODE_NAME, LoadRunner.CONTRACT_NAME);
            Contract digicontract = digibankcon.getContract(LoadRunner.CHAINCODE_NAME, LoadRunner.CONTRACT_NAME);
            new RouteOne().execute(magcontract, digicontract, args[0]);
        }
    }

    public void execute(Contract magcontract, Contract digicontract, String paperNumber) throws Exception {
        CommercialPaper paper = null;

        stopWatch.start("issue");
        byte[] response = magcontract.submitTransaction("issue", "MagnetoCorp", paperNumber,
                Utils.randomDate(), Utils.randomDate(), Utils.randomPrice());
        stopWatch.record();

        // Process response
        if (logger.isDebugEnabled()) {
            System.out.println("Process issue transaction response.");
            paper = CommercialPaper.deserialize(response);
            System.out.println(paper);
        }

        stopWatch.start("buy");
        response = digicontract.submitTransaction("buy", "MagnetoCorp", paperNumber, "MagnetoCorp", "DigiBank",
                Utils.randomPrice(), Utils.randomDate());
        stopWatch.record();

        if (logger.isDebugEnabled()) {
            // Process response
            System.out.println("Process buy transaction response.");
            paper = CommercialPaper.deserialize(response);
            System.out.println(paper);
        }

        stopWatch.start("redeem");
        response = digicontract.submitTransaction("redeem", "MagnetoCorp", paperNumber, "DigiBank", Utils.randomDate());
        stopWatch.record();

        if (logger.isDebugEnabled()) {
            // Process response
            System.out.println("Process redeem transaction response.");
            paper = CommercialPaper.deserialize(response);
            System.out.println(paper);
        }

        stopWatch.output(logger);
    }
}
