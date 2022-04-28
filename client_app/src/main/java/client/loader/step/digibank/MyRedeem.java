package client.loader.step.digibank;

import client.ClientAppConfig;
import client.loader.Connection;
import client.loader.LoaderRunner;
import client.loader.Role;
import org.hyperledger.fabric.gateway.Contract;
import commercialpaper.papernet.CommercialPaper;

import java.nio.file.Path;

public class MyRedeem {

    public void main(String[] args) throws Exception {
//        NetworkHelper.trustAllCerts();
        Path con1path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
        try (Connection digibankcon = Connection.connectAs(con1path, "mychannel", new Role.Digibank())) {
            Contract contract = digibankcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);
            byte[] response = contract.submitTransaction("redeem", "MagnetoCorp", args[0], "DigiBank", args[1]);
            // Process response
            System.out.println("Process redeem transaction response.");
            CommercialPaper paper = CommercialPaper.deserialize(response);
            System.out.println(paper);
        }
    }
}
