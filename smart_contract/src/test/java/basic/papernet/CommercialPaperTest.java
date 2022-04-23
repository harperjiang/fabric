package basic.papernet;

import basic.papernet.ledgerapi.State;
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

        String expected = "{\"state\":\"ISSUED\",\"paperNumber\":\"00001\",\"issuer\":\"MagnetoCorp\",\"issuerMSP\":\"Org2MSP\",\"issueDateTime\":\"2020-05-31\",\"faceValue\":5000000,\"maturityDateTime\":\"2020-11-30\",\"owner\":\"MagnetoCorp\",\"ownerMSP\":\"Org2MSP\",\"tradeValue\":0,\"requester\":\"\",\"requesterMSP\":\"\",\"requestValue\":0,\"key\":\"00001\"}";

        assertEquals(expected, new String(State.serialize(paper), StandardCharsets.UTF_8));
    }
}