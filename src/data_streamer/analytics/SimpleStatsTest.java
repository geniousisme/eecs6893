package data_streamer.analytics;

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
        System.out.println("Average: " + SimpleStats.avgTrade(new Date(0), today, mkt));
        System.out.println("Variance: " + SimpleStats.varTrade(new Date(0), today, mkt));
    }
}
