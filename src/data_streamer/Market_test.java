package data_streamer;

import java.io.IOException;

public class Market_test
{

    public static void main(String[] args)
    {
        Market mkt = new Market("src/config/market.config", "src/config/data_config.txt", "./");

        try {
            mkt.loadConfigs();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            mkt.setupTimer();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.endLog();
        System.out.println(mkt.toString());

    }

}
