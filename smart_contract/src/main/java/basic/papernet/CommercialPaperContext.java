package basic.papernet;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

public class CommercialPaperContext extends Context {

    public CommercialPaperContext(ChaincodeStub stub) {
        super(stub);
        this.paperList = new PaperList(this);
    }

    private PaperList paperList;

    public PaperList getPaperList() {
        return paperList;
    }
}