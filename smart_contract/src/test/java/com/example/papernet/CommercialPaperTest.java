package com.example.papernet;

import com.example.papernet.ledgerapi.State;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CommercialPaperTest {

    @Test
    void testSerialize() {
        CommercialPaper paper = new CommercialPaper();
        paper.setRequester("");
        paper.setOwner("MagnetoCorp");
        paper.setIssuerMSP("Org2MSP");
        paper.setRequestValue(0);
        paper.setTradeValue(0);
        paper.setState("ISSUED");
        paper.setIssuer("MagnetoCorp");
        paper.setPaperNumber("00001");
        paper.setKey();
        paper.setRequesterMSP("");
        paper.setMaturityDateTime("2020-11-30");
        paper.setOwnerMSP("Org2MSP");
        paper.setFaceValue(5000000);
        paper.setIssueDateTime("2020-05-31");

        String expected = "{\"faceValue\":5000000,\"issueDateTime\":\"2020-05-31\",\"issuer\":\"MagnetoCorp\",\"issuerMSP\":\"Org2MSP\",\"maturityDateTime\":\"2020-11-30\",\"owner\":\"MagnetoCorp\",\"ownerMSP\":\"Org2MSP\",\"paperNumber\":\"00001\",\"requestValue\":0,\"requester\":\"\",\"requesterMSP\":\"\",\"state\":\"ISSUED\",\"tradeValue\":0}";
        assertEquals(expected, new String(State.serialize(paper), StandardCharsets.UTF_8));
    }
}