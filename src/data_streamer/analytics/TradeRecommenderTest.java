package data_streamer.analytics;

import java.io.IOException;

import data_streamer.Market;

public class TradeRecommenderTest
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
    }
}
