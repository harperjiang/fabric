package org.example.ledgerapi;

import org.example.CommercialPaper;
import org.example.CommercialPaperContext;
import org.example.CommercialPaperContract;
import org.example.PaperList;
import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CommercialPaperContractTest {

    CommercialPaperContext context;
    String MSP_ID;
    Map<String, byte[]> buffer = new HashMap<>();

    @BeforeEach
    public void prepare() {
        ClientIdentity clientIdentity = mock(ClientIdentity.class);
        when(clientIdentity.getMSPID()).thenAnswer(invoke -> MSP_ID);

        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(stub.getState(anyString())).thenAnswer(key -> buffer.get(key));

        PaperList paperList = mock(PaperList.class);
        when(paperList.addPaper(any(CommercialPaper.class))).thenAnswer(invoke -> {
            CommercialPaper paper = invoke.getArgument(0, CommercialPaper.class);
            buffer.put(paper.key, CommercialPaper.serialize(paper));
            return paperList;
        });
        when(paperList.getPaper(any(String.class))).thenAnswer(invoke -> {
            String key = invoke.getArgument(0, String.class);
            byte[] data = buffer.get(key);
            return CommercialPaper.deserialize(data);
        });
        when(paperList.updatePaper(any(CommercialPaper.class))).thenAnswer(invoke -> {
            CommercialPaper paper = invoke.getArgument(0, CommercialPaper.class);
            buffer.put(paper.key, CommercialPaper.serialize(paper));
            return paperList;
        });

        context = mock(CommercialPaperContext.class);
        when(context.getClientIdentity()).thenReturn(clientIdentity);
        when(context.getStub()).thenReturn(stub);
        when(context.getPaperList()).thenReturn(paperList);

        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            buffer.put((String) arg0, (byte[]) arg1);
            return null;
        }).when(stub).putState(anyString(), any(byte[].class));
    }

    @Test
    public void testIssue() {
        CommercialPaperContract contract = new CommercialPaperContract();
        MSP_ID = "bank1_mspid";
        CommercialPaper paper = contract.issue(context, "bank1", "000001", "2022-01-01", "2022-05-01", 10000);
        assertEquals("000001", paper.getPaperNumber());
        assertEquals(10000, paper.getFaceValue());
        assertEquals("bank1", paper.getOwner());
        assertEquals("bank1_mspid", paper.getOwnerMSP());
        assertEquals("bank1", paper.getIssuer());
        assertEquals("bank1_mspid", paper.getIssuerMSP());
        assertTrue(paper.isIssued());
    }

    @Test
    public void testIssueAndBuy() {
        CommercialPaperContract contract = new CommercialPaperContract();
        MSP_ID = "bank1_mspid";
        CommercialPaper paper = contract.issue(context, "bank1", "000001", "2022-01-01", "2022-05-01", 10000);
        MSP_ID = "bank2_mspid";
        paper = contract.buy(context, "bank1", "000001", "bank1", "bank2", 8000, "2022-04-01");

        assertEquals("000001", paper.getPaperNumber());
        assertEquals(10000, paper.getFaceValue());
        assertEquals(8000, paper.getTradeValue());
        assertEquals("bank1", paper.getIssuer());
        assertEquals("bank2", paper.getOwner());
        assertEquals("bank2_mspid", paper.getOwnerMSP());
        assertTrue(paper.isTrading());
    }

    @Test
    public void testIssueAndBuyAndRedeem() {
        CommercialPaperContract contract = new CommercialPaperContract();
        MSP_ID = "bank1_mspid";
        CommercialPaper paper = contract.issue(context, "bank1", "000001", "2022-01-01", "2022-05-01", 10000);
        MSP_ID = "bank2_mspid";
        paper = contract.buy(context, "bank1", "000001", "bank1", "bank2", 8000, "2022-04-01");
        MSP_ID = "bank2_mspid";
        paper = contract.redeem(context, "bank1", "000001", "bank2", "2022-04-05");

        assertEquals("000001", paper.getPaperNumber());
        assertEquals(10000, paper.getFaceValue());
        assertEquals(8000, paper.getTradeValue());
        assertEquals("bank1", paper.getIssuer());
        assertEquals("bank1", paper.getOwner());
        assertEquals("bank1_mspid", paper.getOwnerMSP());
        assertTrue(paper.isRedeemed());
    }

    @Test
    public void testIssueAndRequest() {
        CommercialPaperContract contract = new CommercialPaperContract();
        MSP_ID = "bank1_mspid";
        CommercialPaper paper = contract.issue(context, "bank1", "000001", "2022-01-01", "2022-05-01", 10000);
        MSP_ID = "bank2_mspid";
        paper = contract.buyrequest(context, "bank1", "000001", "bank1", "bank2", 8000, "2022-04-01");

        assertEquals("000001", paper.getPaperNumber());
        assertEquals(10000, paper.getFaceValue());
        assertEquals(0, paper.getTradeValue());
        assertEquals(8000, paper.getRequestValue());
        assertEquals("bank1", paper.getIssuer());
        assertEquals("bank1", paper.getOwner());
        assertEquals("bank1_mspid", paper.getOwnerMSP());
        assertEquals("bank2_mspid", paper.getRequesterMSP());
        assertTrue(paper.isPending());
    }

    @Test
    public void testIssueAndRequestAndTransfer() {
        CommercialPaperContract contract = new CommercialPaperContract();
        MSP_ID = "bank1_mspid";
        CommercialPaper paper = contract.issue(context, "bank1", "000001", "2022-01-01", "2022-05-01", 10000);
        MSP_ID = "bank2_mspid";
        paper = contract.buyrequest(context, "bank1", "000001", "bank1", "bank2", 8000, "2022-04-01");
        MSP_ID = "bank1_mspid";
        paper = contract.transfer(context, "bank1", "000001", "bank2", "2022-05-01");

        assertEquals("000001", paper.getPaperNumber());
        assertEquals(10000, paper.getFaceValue());
        assertEquals(8000, paper.getTradeValue());
        assertEquals(0, paper.getRequestValue());
        assertEquals("bank1", paper.getIssuer());
        assertEquals("bank2", paper.getOwner());
        assertEquals("bank2_mspid", paper.getOwnerMSP());
        assertEquals("", paper.getRequester());
        assertEquals("", paper.getRequesterMSP());
        assertEquals(0, paper.getRequestValue());
        assertTrue(paper.isTrading());
    }

    @Test
    public void testIssueAndRequestAndReject() {
        CommercialPaperContract contract = new CommercialPaperContract();
        MSP_ID = "bank1_mspid";
        CommercialPaper paper = contract.issue(context, "bank1", "000001", "2022-01-01", "2022-05-01", 10000);
        MSP_ID = "bank2_mspid";
        paper = contract.buyrequest(context, "bank1", "000001", "bank1", "bank2", 8000, "2022-04-01");
        MSP_ID = "bank1_mspid";
        paper = contract.reject(context, "bank1", "000001");

        assertEquals("000001", paper.getPaperNumber());
        assertEquals(10000, paper.getFaceValue());
        assertEquals(0, paper.getTradeValue());
        assertEquals(0, paper.getRequestValue());
        assertEquals("bank1", paper.getIssuer());
        assertEquals("bank1", paper.getOwner());
        assertEquals("bank1_mspid", paper.getOwnerMSP());
        assertEquals("", paper.getRequester());
        assertEquals("", paper.getRequesterMSP());
        assertEquals(0, paper.getRequestValue());
        assertTrue(paper.isTrading());
    }
}
