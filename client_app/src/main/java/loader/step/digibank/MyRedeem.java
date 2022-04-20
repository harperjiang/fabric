package loader.step.digibank;

import loader.Connection;
import loader.LoaderRunner;
import loader.Role;
import org.hyperledger.fabric.gateway.Contract;
import org.papernet.CommercialPaper;

public class MyRedeem {

    public void main(String[] args) throws Exception {
        Connection digibankcon = Connection.connectAs("connection-org1.yaml", "mychannel", new Role.Digibank());
        Contract contract = digibankcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);
        byte[] response = contract.submitTransaction("redeem", "MagnetoCorp", "00001", "DigiBank", "2020-11-30");
        // Process response
        System.out.println("Process redeem transaction response.");
        CommercialPaper paper = CommercialPaper.deserialize(response);
        System.out.println(paper);
    }
}
