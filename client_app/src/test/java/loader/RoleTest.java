package loader;

import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.junit.jupiter.api.Test;
import client.loader.Role;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    public void testCreate() throws IOException {
        Role role = new Role.Digibank();
        assertNotNull(role.getWallet());
        Wallet wallet = role.getWallet();

        Identity id = wallet.get("User1@org1.example.com");
        assertEquals("Org1MSP", id.getMspId());
    }
}