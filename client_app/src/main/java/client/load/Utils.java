package client.load;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Random;

public class Utils {

    static Random random = new Random(System.currentTimeMillis());

    public static String randomPrice() {
        return String.valueOf(random.nextInt(10000));
    }

    public static String randomDate() {
        return MessageFormat.format("{0}-{1}-{2}", String.valueOf(2000 + random.nextInt(25)),
                StringUtils.leftPad(String.valueOf(random.nextInt(12)), 2, '0'),
                StringUtils.leftPad(String.valueOf(random.nextInt(30)), 2, '0'));
    }
}
