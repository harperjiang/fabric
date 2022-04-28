package client.loader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    public void testRandomPrice() {
        System.out.println(Utils.randomPrice());
        System.out.println(Utils.randomPrice());
        System.out.println(Utils.randomPrice());
        System.out.println(Utils.randomPrice());
        System.out.println(Utils.randomPrice());
    }

    @Test
    public void testRandomDate() {
        System.out.println(Utils.randomDate());
        System.out.println(Utils.randomDate());
        System.out.println(Utils.randomDate());
        System.out.println(Utils.randomDate());
        System.out.println(Utils.randomDate());
    }

}