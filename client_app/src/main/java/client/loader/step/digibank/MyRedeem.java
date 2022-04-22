package client.loader.step.digibank;

import client.loader.Connection;
import client.loader.LoaderRunner;
import client.loader.Role;
import org.hyperledger.fabric.gateway.Contract;
import commercialpaper.papernet.CommercialPaper;

public class MyRedeem {

    public void main(String[] args) throws Exception {
//        NetworkHelper.trustAllCerts();
        Connection digibankcon = Connection.connectAs("connection-org1.yaml", "mychannel", new Role.Digibank());
        Contract contract = digibankcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);
        byte[] response = contract.submitTransaction("redeem", "MagnetoCorp", args[0], "DigiBank", args[1]);
        // Process response
        System.out.println("Process redeem transaction response.");
        CommercialPaper paper = CommercialPaper.deserialize(response);
        System.out.println(paper);
    }
}
