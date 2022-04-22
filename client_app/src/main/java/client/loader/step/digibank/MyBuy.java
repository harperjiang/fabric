package client.loader.step.digibank;

import client.ClientAppConfig;
import client.loader.Connection;
import client.loader.Role;
import client.loader.LoaderRunner;
import org.hyperledger.fabric.gateway.Contract;
import commercialpaper.papernet.CommercialPaper;

import java.nio.file.Path;

public class MyBuy {

    public static void main(String[] args) throws Exception {
//        NetworkHelper.trustAllCerts();
        Path con1path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
        Connection digibankcon = Connection.connectAs(con1path, "mychannel", new Role.Digibank());
        Contract contract = digibankcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);
        byte[] response = contract.submitTransaction("buy", "MagnetoCorp", args[0], "MagnetoCorp", "DigiBank", args[1], args[2]);
        // Process response
        System.out.println("Process buy transaction response.");
        CommercialPaper paper = CommercialPaper.deserialize(response);
        System.out.println(paper);
    }
}
