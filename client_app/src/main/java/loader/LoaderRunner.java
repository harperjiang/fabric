package loader;

import org.apache.commons.lang3.StringUtils;
import org.papernet.CommercialPaper;

import java.util.Random;

public class LoaderRunner {

    public static void main(String[] args) {

        int NUM_ROUND = 0;
        int length = String.valueOf(NUM_ROUND).length() + 1;
        Random rand = new Random(System.currentTimeMillis());

        LoaderRunner runner = new LoaderRunner();

        for (int i = 0; i < NUM_ROUND; ++i) {
            String paperNumber = StringUtils.leftPad(String.valueOf(i), length, '0');
            int next = rand.nextInt(3);
            switch (next) {
                case 0:
                    runner.runRoute1(paperNumber);
                    break;
                case 1:
                    runner.runRoute2(paperNumber);
                    break;
                case 2:
                    runner.runRoute3(paperNumber);
                    break;
            }
        }
    }

    Role digibank;
    Role magnetocorp;

    void runRoute1(String paperNum) {
        // Each loop finish a round of issue-buy-redeem cycle. Random pause between the operations
        CommercialPaper paper = executeAs(magnetocorp, "issue", "MagnetoCorp", paperNum, "2020-05-31", "2020-11-30", "5000000");
        paper = executeAs(digibank, "buy", "MagnetoCorp", paperNum, "MagnetoCorp", "DigiBank", "4900000", "2020-05-31");
        paper = executeAs(digibank, "redeem", "MagnetoCorp", paperNum, "DigiBank", "2020-11-30");
    }

    void runRoute2(String paperNum) {
        CommercialPaper paper = executeAs(magnetocorp, "issue", "MagnetoCorp", paperNum, "2020-05-31", "2020-11-30", "5000000");
        paper = executeAs(digibank, "buyrequest", "MagnetoCorp", paperNum, "MagnetoCorp", "DigiBank", "520000", "2022-05-01");
        paper = executeAs(magnetocorp, "transfer", "MagnetoCorp", paperNum, "MagnetoCorp", "2022-05-01");
    }

    void runRoute3(String paperNum) {
        CommercialPaper paper = executeAs(magnetocorp, "issue", "MagnetoCorp", paperNum, "2020-05-31", "2020-11-30", "5000000");
        paper = executeAs(digibank, "buyrequest", "MagnetoCorp", paperNum, "MagnetoCorp", "DigiBank", "520000", "2022-05-01");
        paper = executeAs(magnetocorp, "reject", "MagnetoCorp", paperNum);
    }

    CommercialPaper executeAs(Role role, String... parameters) {
        return null;
    }
}

