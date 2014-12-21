package data_streamer.analytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import data_streamer.Market;
import data_streamer.feed.Feed;

public final class SimpleStats
{
    public static final int              TRADE_LIST_LENGTH = 3, MIN_LIST_INDEX = 0,
        MAX_LIST_INDEX = 1, AVG_LIST_INDEX = 2;

    public static final String           PRICE             = "PRICE", TIME = "TIME", MIN = "MIN",
        MAX = "MAX", OTHER = "OTHER";

    public static final SimpleDateFormat TIME_FORMAT       = new SimpleDateFormat(
                                                               "yyyyMMdd hhmmssSSS");

    /**
     * Returns a list
     *
     * @param start
     * @param end
     * @return a list whose first element is a list of the minimum prices in that interval, whose
     *         second element is a list of the maximum prices in that interval, and whose third
     *         elements is a list of average prices in that interval. If the length of the list is
     *         zero, then something went wrong
     */
    public static List<List<Double>> gatherTrades(Date start, Date end, Market market)
    {
        return gatherTrades(start, end, market, null);
    }

    /**
     * Returns a list
     *
     * @param start
     * @param end
     * @return a list whose first element is a list of the minimum prices in that interval, whose
     *         second element is a list of the maximum prices in that interval, and whose third
     *         elements is a list of average prices in that interval. If the length of the list is
     *         zero, then something went wrong
     */
    public static List<List<Double>> gatherTrades(Date start, Date end, Market market, String ex)
    {
        List<List<Double>> ret = new ArrayList<List<Double>>();
        List<Double> minList = new ArrayList<Double>(), maxList = new ArrayList<Double>(), avgList =
            new ArrayList<Double>();

        // Add them to the list in the correct order you say? Niet, I saw, I want to be completely
        // agnostic to what the values of the indexes are
        while (ret.size() < TRADE_LIST_LENGTH)
            ret.add(new ArrayList<Double>());
        ret.set(MIN_LIST_INDEX, minList);
        ret.set(MAX_LIST_INDEX, maxList);
        ret.set(AVG_LIST_INDEX, avgList);

        // Get the price feed
        Dictionary<String, Feed> priceFeeds = market.getTimer().getFeeds().get(PRICE);
        // Get the feed corresponding to the exchange
        Feed feed = null;
        if (ex == null || ex.trim().isEmpty()) {
            // Use the first feed we find
            Enumeration<String> keys = priceFeeds.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                feed = priceFeeds.get(key);
                break;
            }
        } else
            feed = priceFeeds.get(ex);
        // Make sure we have a feed to operate on
        if (feed == null)
            return ret;

        // Iterate over all exchanges
        ArrayList<Dictionary<String, String>> q = feed.getQ();
        for (Dictionary<String, String> exchange : q) {
            // Get the time
            String timeStr = exchange.get(TIME);
            if (timeStr == null)
                continue;

            Date time;
            try {
                time = TIME_FORMAT.parse(timeStr);
            } catch (ParseException e) {
                continue;
            }
            // Check to make sure it's within bounds (it's not if it's before the start or after the
            // end)
            if (time.getTime() < start.getTime() || time.getTime() > end.getTime())
                continue;

            // Add the prices to the list
            String numStr = exchange.get(MIN);
            if (numStr != null)
                try {
                    minList.add(Double.parseDouble(numStr));
                } catch (NumberFormatException e) {
                    minList.add(-1.0);
                }
            numStr = exchange.get(MAX);
            if (numStr != null)
                try {
                    maxList.add(Double.parseDouble(numStr));
                } catch (NumberFormatException e) {
                    maxList.add(-1.0);
                }
            numStr = exchange.get(OTHER);
            if (numStr != null)
                try {
                    avgList.add(Double.parseDouble(numStr));
                } catch (NumberFormatException e) {
                    avgList.add(-1.0);
                }
        }
        return ret;
    }

    public static List<Double> getAvgList(Date start, Date end, Market market, String ex)
    {
        return getAvgList(gatherTrades(start, end, market, ex));
    }

    public static List<Double> getAvgList(List<List<Double>> trades)
    {
        if (trades.size() != TRADE_LIST_LENGTH)
            return null;

        // Separate the lists
        List<Double> avgList = trades.get(AVG_LIST_INDEX), minList = trades.get(MIN_LIST_INDEX), maxList =
            trades.get(MAX_LIST_INDEX);
        // SOmething else wen't wrong
        if (avgList.size() != minList.size() && avgList.size() != minList.size())
            return null;

        List<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < avgList.size(); i++) {
            double avg = avgList.get(i);
            // Calculate the average
            if (avg <= 0) {
                double min = minList.get(i), max = maxList.get(i);
                if (min < 0 || max < 0)
                    continue;
                avg = (min + max) / 2.0;
            }
            ret.add(avg);
        }
        return ret;
    }

    public static double avgTrade(Date start, Date end, Market market)
    {
        return avgTrade(gatherTrades(start, end, market));
    }

    public static double avgTrade(List<List<Double>> trades)
    {
        List<Double> avgList = getAvgList(trades);
        if (avgList == null)
            return -1;

        double total_avg = 0;
        for (Double avg : avgList)
            total_avg += avg;
        return total_avg / avgList.size();
    }

    public static double varTrade(Date start, Date end, Market market)
    {
        return varTrade(gatherTrades(start, end, market));
    }

    public static double varTrade(List<List<Double>> trades)
    {
        double total_avg = avgTrade(trades);
        if (total_avg < 0)
            return -1;
        List<Double> avgList = getAvgList(trades);

        // Compute sum of square of differences
        double sum_sq_diff = 0;
        for (double avg : avgList) {
            // Faster than Math.pow(x, 2.0)?
            double temp = avg - total_avg;
            sum_sq_diff += temp * temp;
        }
        return sum_sq_diff / avgList.size();
    }

    private SimpleStats() throws AssertionError
    {
        throw new AssertionError();
    }
}
