package data_streamer.analytics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public final class TradeRecommender
{
    private static final int STATIC_RANGE = 10;

    public static BuyDecision makeBuyDecision(List<Double> currentRange, List<Double> allTrades)
    {
        if (allTrades.size() < STATIC_RANGE)
            return BuyDecision.HOLD;
        double diff = scoreDifference(currentRange);
        // Chunk the data
        double numRanges = Math.ceil(allTrades.size() / 10);
        List<Double> diffScoreList = new ArrayList<Double>();
        // Add the first bucket
        for (int i = 0; i * 10 < numRanges && i < allTrades.size(); i += STATIC_RANGE)
            diffScoreList.add(scoreDifference(allTrades.subList(i, Math.min(allTrades.size(), i
                + STATIC_RANGE))));
        System.out.println("Diff: " + diff);
        return null;
    }

    /**
     * Scores the difference in prices among this range. May apply some regressions to the range to
     * determine some normalized score.
     *
     * @param range list of doubles, sorted from newest (0) to oldest (range.size() - 1).
     * @return
     */
    public static double scoreDifference(List<Double> range)
    {
        // Simple corner case
        if (range.isEmpty())
            return 0;
        else if (range.size() == 1)
            return range.get(0);

        // Compute the difference between the oldest value and the newest value
        double diff = range.get(range.size() - 1) - range.get(0);
        // Create a regression model
        SimpleRegression sr = new SimpleRegression();
        double[][] data = new double[range.size()][range.size()];
        for (int i = 0; i < range.size(); i++) {
            data[i][0] = i + 1;
            data[i][1] = range.get(i);
        }
        sr.addData(data);
        System.out.println("sr intercept: " + sr.getIntercept());
        diff *= sr.getRSquare();
        return diff;
    }

    public static enum BuyDecision
    {
        BUY, SELL, HOLD;
    }

    private TradeRecommender() throws AssertionError
    {
        throw new AssertionError();
    }
}
