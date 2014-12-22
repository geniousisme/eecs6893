package data_streamer.analytics;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import data_streamer.Market;
import data_streamer.analytics.TradeRecommender.BuyDecision;

public class TradeRecommenderTest
{
    public static void main(String[] args)
    {
        Market mkt =
            new Market("src/config/market.config",
                "src/config/data_config.txt", "./");

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

        List<List<Double>> gatherTrades =
            SimpleStats.gatherTrades(new Date(0), new Date(), mkt);
        List<Double> maxList = gatherTrades.get(SimpleStats.MAX_LIST_INDEX);
        BuyDecision buyDecision =
            TradeRecommender.makeTradeDecision(maxList.subList(0,
                TradeRecommender.STATIC_RANGE), maxList.subList(
                TradeRecommender.STATIC_RANGE, maxList.size()));
        System.out.println("Buy decision: " + buyDecision);
        long decisionTicks =
            TradeRecommender.decisionTicks(maxList.subList(0,
                TradeRecommender.STATIC_RANGE), maxList.subList(
                TradeRecommender.STATIC_RANGE, maxList.size()));
        System.out.println("Trade duration: " + decisionTicks);
    }
}
