package org.harper.loader.step.magnetocorp;

import org.harper.loader.Connection;
import org.harper.loader.LoaderRunner;
import org.harper.loader.Role;
import org.hyperledger.fabric.gateway.Contract;
import org.papernet.CommercialPaper;

public class MyIssue {

    public static void main(String[] args) throws Exception {
//        NetworkHelper.trustAllCerts();
        Connection magcon = Connection.connectAs("connection-org2.yaml", "mychannel", new Role.MagnetoCorp());
        Contract contract = magcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);
        byte[] response = contract.submitTransaction("issue", "MagnetoCorp", args[0], args[1], args[2], args[3]);
        // Process response
        System.out.println("Process issue transaction response.");
        CommercialPaper paper = CommercialPaper.deserialize(response);
        System.out.println(paper);
    }
}
