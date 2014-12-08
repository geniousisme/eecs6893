package data_streamer.math;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import data_streamer.Market;

public class SimpleStatsTest
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
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();
        System.out.println(SimpleStats.avgTrade(yesterday, today, mkt));
    }
}
