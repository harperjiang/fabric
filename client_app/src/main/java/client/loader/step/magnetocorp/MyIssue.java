package client.loader.step.magnetocorp;

import client.ClientAppConfig;
import client.loader.Connection;
import client.loader.Role;
import client.loader.LoaderRunner;
import org.hyperledger.fabric.gateway.Contract;
import commercialpaper.papernet.CommercialPaper;

import java.nio.file.Path;

public class MyIssue {

    public static void main(String[] args) throws Exception {
//        NetworkHelper.trustAllCerts();
        Path con2path = Path.of(ClientAppConfig.FABRIC_SAMPLE_PATH, "test-network", "organizations", "peerOrganizations", "org2.example.com", "connection-org2.yaml");
        Connection magcon = Connection.connectAs(con2path, "mychannel", new Role.MagnetoCorp());
        Contract contract = magcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);
        byte[] response = contract.submitTransaction("issue", "MagnetoCorp", args[0], args[1], args[2], args[3]);
        // Process response
        System.out.println("Process issue transaction response.");
        CommercialPaper paper = CommercialPaper.deserialize(response);
        System.out.println(paper);
    }
}
