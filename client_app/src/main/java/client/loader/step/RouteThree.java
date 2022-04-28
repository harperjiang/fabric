package client.loader.step;

import client.ClientAppConfig;
import client.loader.Connection;
import client.loader.LoaderRunner;
import client.loader.Role;
import client.loader.Utils;
import commercialpaper.papernet.CommercialPaper;
import org.hyperledger.fabric.gateway.Contract;

import java.nio.file.Path;

public class RouteThree {

    public static void main(String[] args) throws Exception {
        Path con1path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
        Path con2path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org2.example.com", "connection-org2.yaml");
        try (Connection magcon = Connection.connectAs(con2path, "mychannel", new Role.MagnetoCorp());
             Connection digibankcon = Connection.connectAs(con1path, "mychannel", new Role.Digibank())) {
            new RouteThree().execute(magcon, digibankcon, args[0]);
        }
    }

    public void execute(Connection magcon, Connection digibankcon, String paperNumber) throws Exception {
        Contract magcontract = magcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);
        Contract digicontract = digibankcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);

        byte[] response = magcontract.submitTransaction("issue", "MagnetoCorp", paperNumber,
                Utils.randomDate(), Utils.randomDate(), Utils.randomPrice());
        // Process response
        System.out.println("Process issue transaction response.");
        CommercialPaper paper = CommercialPaper.deserialize(response);
        System.out.println(paper);

        response = digicontract.submitTransaction("buy", "MagnetoCorp", paperNumber, "MagnetoCorp", "DigiBank",
                Utils.randomPrice(), Utils.randomDate());
        // Process response
        System.out.println("Process buy transaction response.");
        paper = CommercialPaper.deserialize(response);
        System.out.println(paper);

        response = digicontract.submitTransaction("redeem", "MagnetoCorp", paperNumber, "DigiBank", Utils.randomDate());
        // Process response
        System.out.println("Process redeem transaction response.");
        paper = CommercialPaper.deserialize(response);
        System.out.println(paper);
    }
}
