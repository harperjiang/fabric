/*
SPDX-License-Identifier: Apache-2.0
*/
package org.papernet;

import org.papernet.ledgerapi.State;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.logging.Logger;

/**
 * A custom context provides easy access to list of all commercial papers
 */

/**
 * Define commercial paper smart contract by extending Fabric Contract class
 */
@Contract(name = "org.papernet.commercialpaper", info = @Info(title = "MyAsset contract", description = "", version = "0.0.1", license = @License(name = "SPDX-License-Identifier: Apache-2.0", url = ""), contact = @Contact(email = "java-contract@example.com", name = "java-contract", url = "http://java-contract.me")))
@Default
public class CommercialPaperContract implements ContractInterface {

    // use the classname for the logger, this way you can refactor
    private final static Logger LOG = Logger.getLogger(CommercialPaperContract.class.getName());

    @Override
    public Context createContext(ChaincodeStub stub) {
        return new CommercialPaperContext(stub);
    }

    public CommercialPaperContract() {

    }

    /**
     * Define a custom context for commercial paper
     */

    /**
     * Instantiate to perform any setup of the ledger that might be required.
     *
     * @param {Context} ctx the transaction context
     */
    @Transaction
    public void instantiate(CommercialPaperContext ctx) {
        // No implementation required with this example
        // It could be where data migration is performed, if necessary
        LOG.info("No data migration to perform");
    }

    /**
     * Issue commercial paper
     *
     * @param {Context} ctx the transaction context
     * @param {String}  issuer commercial paper issuer
     * @param {Integer} paperNumber paper number for this issuer
     * @param {String}  issueDateTime paper issue date
     * @param {String}  maturityDateTime paper maturity date
     * @param {Integer} faceValue face value of paper
     */
    @Transaction
    public CommercialPaper issue(CommercialPaperContext ctx, String issuer, String paperNumber, String issueDateTime,
                                 String maturityDateTime, int faceValue) {

        System.out.println(ctx);

        // create an instance of the paper
        CommercialPaper paper = CommercialPaper.createInstance(issuer, paperNumber, issueDateTime, maturityDateTime,
                faceValue, issuer, "");

        // Smart contract, rather than paper, moves paper into ISSUED state
        paper.setIssued();
        paper.setIssuerMSP(ctx.getClientIdentity().getMSPID());
        paper.setOwnerMSP(ctx.getClientIdentity().getMSPID());

        // Newly issued paper is owned by the issuer
        System.out.println(paper);
        // Add the paper to the list of all similar commercial papers in the ledger
        // world state
        ctx.getPaperList().addPaper(paper);

        // Must return a serialized paper to caller of smart contract
        return paper;
    }

    /**
     * Buy commercial paper
     *
     * @param {Context} ctx the transaction context
     * @param {String}  issuer commercial paper issuer
     * @param {Integer} paperNumber paper number for this issuer
     * @param {String}  currentOwner current owner of paper
     * @param {String}  newOwner new owner of paper
     * @param {Integer} price price paid for this paper
     * @param {String}  purchaseDateTime time paper was purchased (i.e. traded)
     */
    @Transaction
    public CommercialPaper buy(CommercialPaperContext ctx, String issuer, String paperNumber, String currentOwner,
                               String newOwner, int price, String purchaseDateTime) {

        // Retrieve the current paper using key fields provided
        String paperKey = State.makeKey(new String[]{paperNumber});
        CommercialPaper paper = ctx.getPaperList().getPaper(paperKey);

        // Validate current owner
        if (!paper.getOwner().equals(currentOwner)) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " is not owned by " + currentOwner);
        }

        // First buy moves state from ISSUED to TRADING
        if (paper.isIssued()) {
            paper.setTrading();
            paper.setOwner(newOwner);
            paper.setOwnerMSP(ctx.getClientIdentity().getMSPID());
            paper.setTradeValue(price);
        } else {
            throw new RuntimeException(
                    "Paper " + issuer + paperNumber + " is not trading. Current state = " + paper.getState());
        }

        // Update the paper
        ctx.getPaperList().updatePaper(paper);
        return paper;
    }

    /**
     * Redeem commercial paper
     *
     * @param {Context} ctx the transaction context
     * @param {String}  issuer commercial paper issuer
     * @param {Integer} paperNumber paper number for this issuer
     * @param {String}  redeemingOwner redeeming owner of paper
     * @param {String}  redeemDateTime time paper was redeemed
     */
    @Transaction
    public CommercialPaper redeem(CommercialPaperContext ctx, String issuer, String paperNumber, String redeemingOwner,
                                  String redeemDateTime) {

        String paperKey = CommercialPaper.makeKey(new String[]{paperNumber});

        CommercialPaper paper = ctx.getPaperList().getPaper(paperKey);

        // Check paper is not REDEEMED
        if (paper.isRedeemed()) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " already redeemed");
        }

        // Verify that the redeemer owns the commercial paper before redeeming it
        if (paper.getOwner().equals(redeemingOwner) && paper.getOwnerMSP().equals(ctx.getClientIdentity().getMSPID())) {
            paper.setOwner(paper.getIssuer());
            paper.setOwnerMSP(paper.getIssuerMSP());
            paper.setRedeemed();
        } else {
            throw new RuntimeException("Redeeming owner does not own paper" + issuer + paperNumber);
        }

        ctx.getPaperList().updatePaper(paper);
        return paper;
    }

    @Transaction
    public CommercialPaper buyrequest(CommercialPaperContext ctx, String issuer, String paperNumber, String currentOwner, String newOwner, int proposedPrice, String purchaseDateTime) {
        // Retrieve the current paper using key fields provided
        String paperKey = CommercialPaper.makeKey(new String[]{paperNumber});
        CommercialPaper paper = ctx.getPaperList().getPaper(paperKey);

        // Validate current owner - this is really information for the user trying the sample, rather than any 'authorisation' check per se FYI
        if (!paper.getOwner().equals(currentOwner)) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " is not owned by " + currentOwner);
        }
        // paper set to 'PENDING' - can only be transferred (confirmed) by identity from owning org (MSP check).
        if (paper.isIssued() || paper.isTrading()) {
            paper.setPending();
            paper.setRequester(newOwner);
            paper.setRequesterMSP(ctx.getClientIdentity().getMSPID());
            paper.setRequestValue(proposedPrice);
        }

        // Update the paper
        ctx.getPaperList().updatePaper(paper);
        return paper;
    }

    @Transaction
    public CommercialPaper transfer(CommercialPaperContext ctx, String issuer, String paperNumber, String newOwner, String confirmDateTime) {
        // Retrieve the current paper using key fields provided
        String paperKey = CommercialPaper.makeKey(new String[]{paperNumber});
        CommercialPaper paper = ctx.getPaperList().getPaper(paperKey);

        // Validate current owner's MSP in the paper === invoking transferor's MSP id - can only transfer if you are the owning org.

        if (!paper.getOwnerMSP().equals(ctx.getClientIdentity().getMSPID())) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " is not owned by " + ctx.getClientIdentity().getMSPID());
        }

        if (!paper.getRequester().equals(newOwner)) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " is not requested by the newOwner " + newOwner);
        }

        // Paper needs to be 'pending' - which means you need to have run 'buy_pending' transaction first.
        if (!paper.isPending()) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " is not currently in state: PENDING for transfer to occur: must run buy_request transaction first");
        }
        // else all good

        paper.setOwner(newOwner);
        paper.setTradeValue(paper.getRequestValue());
        paper.setOwnerMSP(paper.getRequesterMSP());
        paper.setRequester("");
        paper.setRequesterMSP("");
        paper.setRequestValue(0);
        // set the MSP of the transferee (so that, that org may also pass MSP check, if subsequently transferred/sold on)
        paper.setTrading();

        // Update the paper
        ctx.getPaperList().updatePaper(paper);
        return paper;
    }

    @Transaction
    public CommercialPaper reject(CommercialPaperContext ctx, String issuer, String paperNumber) {
        String paperKey = CommercialPaper.makeKey(new String[]{paperNumber});
        CommercialPaper paper = ctx.getPaperList().getPaper(paperKey);

        // Validate current owner's MSP in the paper === invoking transferor's MSP id - can only transfer if you are the owning org.

        if (!paper.getOwnerMSP().equals(ctx.getClientIdentity().getMSPID())) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " is not owned by the current invoking Organisation, and not authorised to transfer");
        }

        // Paper needs to be 'pending' - which means you need to have run 'buy_pending' transaction first.
        if (!paper.isPending()) {
            throw new RuntimeException("Paper " + issuer + paperNumber + " is not currently in state: PENDING for transfer to occur: must run buy_request transaction first");
        }
        // else all good
        paper.setRequester("");
        paper.setRequestValue(0);
        paper.setRequesterMSP("");
        paper.setTrading();

        // Update the paper
        ctx.getPaperList().updatePaper(paper);
        return paper;
    }
}
