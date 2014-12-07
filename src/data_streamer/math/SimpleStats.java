package data_streamer.math;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import data_streamer.Market;

public final class SimpleStats
{
    public static final int TRADE_LIST_LENGTH = 3, MIN_LIST_INDEX = 0, MAX_LIST_INDEX = 1,
        AVG_LIST_INDEX = 2;

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
    private static List<List<Double>> gatherTrades(Date start, Date end, Market market)
    {
        List<List<Double>> ret = new ArrayList<List<Double>>();

        return ret;
    }

    public static List<Double> getAvgList(Date start, Date end, Market market)
    {
        return getAvgList(gatherTrades(start, end, market));
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
            if (avg <= 0)
                avg = (minList.get(i) + maxList.get(i)) / 2.0;
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
