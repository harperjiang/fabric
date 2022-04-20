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
        byte[] response = contract.submitTransaction("issue", "MagnetoCorp", args[0], args[1], args[2], args[3]);
        // Process response
        System.out.println("Process issue transaction response.");
        CommercialPaper paper = CommercialPaper.deserialize(response);
        System.out.println(paper);
    }
}
