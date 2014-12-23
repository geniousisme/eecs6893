package data_streamer.analytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.file.FileItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public final class TradeRecommender
{
    public static final int    STATIC_RANGE   = 40;
    public static final int    MAX_RANGES     = 10;
    public static final double MIN_SIMILARITY = 0.25;
    public static final double ABS_HOLD_SCORE = 1e-5;
    public static final double DECISION_RATIO = 0.5;

    public static PriorityQueue<SimilarityPair> retrieveSimilarity(
        List<Double> currentRange, List<Double> allTrades)
    {
        if (allTrades.size() < STATIC_RANGE)
            return null;
        double diff = scoreDifference(currentRange);
        // Chunk the data
        List<Double> diffScoreList = new ArrayList<Double>();
        for (int i = 0; i < allTrades.size(); i += STATIC_RANGE) {
            double score =
                scoreDifference(allTrades.subList(i, Math.min(allTrades.size(),
                    i + STATIC_RANGE)));
            // if (i / STATIC_RANGE == 332)
            // System.out.println("334 score: " + score);
            diffScoreList.add(score);
        }
        List<Double> normalizedList =
            new ArrayList<Double>(diffScoreList.size() + 1);
        normalizedList.add(diff);
        normalizedList.addAll(diffScoreList);
        // Write it out into a temporary file
        File rangeFile = writeFileItemNormalizedDiffs(normalizedList);
        DataModel dm;
        try {
            dm = new FileDataModel(rangeFile);
        } catch (IOException e) {
            System.err.println("failed to make DataModel of "
                + rangeFile.getAbsolutePath() + ": " + e.getMessage());
            rangeFile.delete();
            return null;
        }
        // Get the item similarity
        ItemSimilarity is = new FileItemSimilarity(rangeFile);
        int numUsers;
        try {
            numUsers = dm.getNumUsers();
            if (numUsers < 2)
                return null;
        } catch (TasteException e2) {
            rangeFile.delete();
            return null;
        }
        // Order the similarities
        long[] itemIDs = new long[numUsers - 1];
        for (int i = 2; i <= numUsers; i++)
            itemIDs[i - 2] = i;
        double[] itemSimilarities;
        try {
            itemSimilarities = is.itemSimilarities(1, itemIDs);
        } catch (TasteException e) {
            e.printStackTrace();
            rangeFile.delete();
            return null;
        }
        PriorityQueue<SimilarityPair> pq = new PriorityQueue<>();
        for (int i = 0; i < itemIDs.length; i++)
            pq.add(new SimilarityPair(itemIDs[i], itemSimilarities[i]));
        rangeFile.delete();
        return pq;
    }

    public static long decisionTicks(List<Double> currentRange,
        List<Double> allTrades)
    {
        PriorityQueue<SimilarityPair> simQueue =
            retrieveSimilarity(currentRange, allTrades);
        if (simQueue == null)
            return 0;

        int numRanges = 0;
        List<Long> tickList = new ArrayList<>();
        double currentScore = scoreDifference(currentRange);
        while (numRanges < MAX_RANGES && !simQueue.isEmpty()) {
            SimilarityPair sp = simQueue.poll();
            // Get the scores
            double spScore =
                scoreDifference(allTrades.subList((int) (sp.item2 - 2)
                    * STATIC_RANGE, Math.min(allTrades.size(),
                    (int) (sp.item2 - 1) * STATIC_RANGE)));
            // We don't care about direction, so much as the trend, represented
            // by the score
            if (!isSameDirection(spScore, currentScore))
                continue;
            // The scores are too dissimilar
            if (sp.similarity < MIN_SIMILARITY)
                continue;
            // If we're at this point, see which direction the market went in
            double futurePastScore;
            try {
                futurePastScore =
                    scoreDifference(allTrades.subList((int) (sp.item2 - 1)
                        * STATIC_RANGE, Math.min(allTrades.size(),
                        (int) sp.item2 * STATIC_RANGE)));
            } catch (IndexOutOfBoundsException e) {
                continue;
            } catch (IllegalArgumentException e) {
                continue;
            }
            numRanges++;
            // System.out.println("Future score: " + futurePastScore);
            // System.out.println(sp);
            // System.out.println("Item 1: " + currentScore + ", item 2: "
            // + spScore);
            // If the score continues in the same direction, increment ticks
            long ticks = STATIC_RANGE / 2;
            int pos = 0;
            while ((sp.item2 + pos) * STATIC_RANGE < allTrades.size()) {
                double futurePastFutureScore =
                    scoreDifference(allTrades.subList((int) (sp.item2 + pos)
                        * STATIC_RANGE, Math.min(allTrades.size(),
                        (int) (sp.item2 + pos + 1) * STATIC_RANGE)));
                if (isSameDirection(futurePastFutureScore, futurePastScore))
                    ticks += STATIC_RANGE;
                else
                    break;
                pos++;
            }
            tickList.add(ticks);
        }
        // Average the ticklist
        long tickSum = 0;
        for (Long tick : tickList)
            tickSum += tick;
        return Math.round(tickSum / (double) tickList.size());
    }

    public static BuyDecision makeTradeDecision(List<Double> currentRange,
        List<Double> allTrades)
    {
        PriorityQueue<SimilarityPair> simQueue =
            retrieveSimilarity(currentRange, allTrades);
        if (simQueue == null)
            return null;

        int numRanges = 0;
        int numBuy = 0, numSell = 0;
        double currentScore = scoreDifference(currentRange);
        while (numRanges < MAX_RANGES && !simQueue.isEmpty()) {
            SimilarityPair sp = simQueue.poll();
            // Get the scores
            double spScore =
                scoreDifference(allTrades.subList((int) (sp.item2 - 2)
                    * STATIC_RANGE, Math.min(allTrades.size(),
                    (int) (sp.item2 - 1) * STATIC_RANGE)));
            // If the two scores aren't in the same direction
            if (!isSameDirection(spScore, currentScore))
                // System.err.println("Not in same direction");
                continue;
            // The scores are too dissimilar
            if (sp.similarity < MIN_SIMILARITY)
                continue;
            // If we're at this point, see which direction the market went in
            double futurePastScore;
            try {
                futurePastScore =
                    scoreDifference(allTrades.subList((int) (sp.item2 - 1)
                        * STATIC_RANGE, Math.min(allTrades.size(),
                        (int) sp.item2 * STATIC_RANGE)));
            } catch (IndexOutOfBoundsException e) {
                continue;
            } catch (IllegalArgumentException e) {
                continue;
            }
            numRanges++;
            // System.out.println("Future score: " + futurePastScore);
            // System.out.println(sp);
            // System.out.println("Item 1: " + currentScore + ", item 2: "
            // + spScore);
            // If the futurePastScore is going up buy
            if (futurePastScore > ABS_HOLD_SCORE)
                numBuy++;
            // If it's going down, sell
            else if (futurePastScore < ABS_HOLD_SCORE)
                numSell++;
        }
        // If the decision ratio is met, make that decision
        if (numBuy / (double) numRanges >= DECISION_RATIO)
            return BuyDecision.BUY;
        else if (numSell / (double) numRanges >= DECISION_RATIO)
            return BuyDecision.SELL;
        else
            return BuyDecision.HOLD;
    }

    private static boolean isSameDirection(double spScore, double currentScore)
    {
        if (spScore == 0 && currentScore == 0 || spScore > 0
            && currentScore > 0 || spScore < 0 && currentScore < 0)
            return true;
        else
            return false;
    }

    private static File
        writeFileItemNormalizedDiffs(List<Double> diffScoreList)
    {
        // Make a temporary file
        File tmpFile;
        try {
            tmpFile = File.createTempFile("diffList", ".csv");
        } catch (IOException e) {
            return null;
        }
        // Make a PrintWriter
        PrintWriter out;
        try {
            out = new PrintWriter(tmpFile);
        } catch (FileNotFoundException e) {
            return null;
        }

        for (int i = 0; i < diffScoreList.size(); i++) {
            double diff1 = diffScoreList.get(i);
            // Compute the percent diffs and find the largest absolute diff
            double maxAbsDiff = 0;
            List<Double> percentDiffs = new ArrayList<>();
            for (int j = i + 1; j < diffScoreList.size(); j++) {
                double diff2 = diffScoreList.get(j);
                double percentDiff;
                if (diff1 == 0 && diff2 == 0)
                    // Well, there's no difference
                    percentDiff = 0;
                else if (diff1 == 0)
                    percentDiff = (diff1 - diff2) / diff2;
                else
                    percentDiff = (diff1 - diff2) / diff1;
                double absDiff = Math.abs(percentDiff);
                if (absDiff > maxAbsDiff)
                    maxAbsDiff = absDiff;
                percentDiffs.add(percentDiff);
            }
            for (int j = 0; j < percentDiffs.size(); j++) {
                double normDiff = percentDiffs.get(j) / maxAbsDiff;
                // Invert the diff. Items with the greatest difference are the
                // least similar
                if (normDiff < 0)
                    normDiff = -1 - normDiff;
                else
                    normDiff = 1 - normDiff;
                out.println(i + 1 + "," + (i + j + 2) + "," + normDiff);
            }
        }
        // System.out.println(tmpFile.getAbsolutePath());
        tmpFile.deleteOnExit();
        out.close();
        return tmpFile;
    }

    /**
     * Scores the difference in prices among this range. May apply some
     * regressions to the range to determine some normalized score.
     *
     * @param range list of doubles, sorted from newest (0) to oldest
     *        (range.size() - 1).
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
        double diffSum = 0;
        for (int i = 1; i < range.size(); i++)
            diffSum += range.get(i) - range.get(0);
        // Create a regression model
        SimpleRegression sr = new SimpleRegression();
        double[][] data = new double[range.size()][range.size()];
        for (int i = 0; i < range.size(); i++) {
            data[i][0] = i + 1;
            data[i][1] = range.get(i);
        }
        sr.addData(data);
        // If the range isn't strongly related, then penalize that
        diffSum *= sr.getRSquare();
        return diffSum;
    }

    public static enum BuyDecision
    {
        BUY, SELL, HOLD;
    }

    public static class SimilarityPair implements Comparable<SimilarityPair>
    {
        final long   item2;
        final Double similarity;

        public SimilarityPair(long item2, double similarity)
        {
            this.item2 = item2;
            this.similarity = similarity;
        }

        @Override
        public int compareTo(SimilarityPair o)
        {
            return o.similarity.compareTo(similarity);
        }

        @Override
        public String toString()
        {
            return item2 + ": " + similarity;
        }
    }

    private TradeRecommender() throws AssertionError
    {
        throw new AssertionError();
    }
}
