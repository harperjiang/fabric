package client.loader;

import client.load.Utils;
import org.junit.jupiter.api.Test;

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