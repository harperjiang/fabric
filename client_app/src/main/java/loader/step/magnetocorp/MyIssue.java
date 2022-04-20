package loader.step.magnetocorp;

import loader.Connection;
import loader.LoaderRunner;
import loader.Role;
import org.hyperledger.fabric.gateway.Contract;
import org.papernet.CommercialPaper;

public class MyIssue {

    public static void main(String[] args) throws Exception {
        Connection magcon = Connection.connectAs("connection-org2.yaml", "mychannel", new Role.MagnetoCorp());
        Contract contract = magcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);
        byte[] response = contract.submitTransaction("issue", "MagnetoCorp", "00001", "2020-05-31", "2020-11-30", "5000000");
        // Process response
        System.out.println("Process issue transaction response.");
        CommercialPaper paper = CommercialPaper.deserialize(response);
        System.out.println(paper);
    }
}
