package client.load.step;

import client.ClientAppConfig;
import client.load.Connection;
import client.load.Role;
import client.load.Utils;
import client.load.LoadRunner;
import commercialpaper.papernet.CommercialPaper;
import org.hyperledger.fabric.gateway.Contract;

import java.nio.file.Path;

public class RouteOne {

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
