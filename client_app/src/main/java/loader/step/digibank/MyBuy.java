package loader.step.digibank;

import loader.Connection;
import loader.LoaderRunner;
import loader.Role;
import org.hyperledger.fabric.gateway.Contract;
import org.papernet.CommercialPaper;

public class MyBuy {

    public static void main(String[] args) throws Exception {
        Connection digibankcon = Connection.connectAs("connection-org1.yaml", "mychannel", new Role.Digibank());
        Contract contract = digibankcon.getContract(LoaderRunner.CHAINCODE_NAME, LoaderRunner.CONTRACT_NAME);
        byte[] response = contract.submitTransaction("buy", "MagnetoCorp", "00001", "MagnetoCorp", "DigiBank", "4900000", "2020-05-31");
        // Process response
        System.out.println("Process buy transaction response.");
        CommercialPaper paper = CommercialPaper.deserialize(response);
        System.out.println(paper);
    }
}
