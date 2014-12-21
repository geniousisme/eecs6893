package data_streamer.analytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.file.FileItemSimilarity;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public final class TradeRecommender
{
    private static final int STATIC_RANGE = 10;
    private static final long STARTING_UID = 1, STARTING_IID = 1;

    public static BuyDecision makeBuyDecision(List<Double> currentRange, List<Double> allTrades)
    {
        if (allTrades.size() < STATIC_RANGE)
            return BuyDecision.HOLD;
        double diff = scoreDifference(currentRange);
        // Chunk the data
        List<Double> diffScoreList = new ArrayList<Double>();
        for (int i = 0; i < allTrades.size(); i += STATIC_RANGE)
            diffScoreList.add(scoreDifference(allTrades.subList(i, Math.min(allTrades.size(), i
                + STATIC_RANGE))));
        List<Double> normalizedList = new ArrayList<Double>(diffScoreList.size() + 1);
        normalizedList.add(diff);
        normalizedList.addAll(diffScoreList);
        // Write it out into a temporary file
        File rangeFile = writeFileItemNormalizedDiffs(normalizedList);
        // DataModel dm;
        // try {
        // dm = new FileDataModel(rangeFile);
        // } catch (IOException e) {
        // System.err.println("failed to make DataModel of " + rangeFile.getAbsolutePath() + ": "
        // + e.getMessage());
        // return null;
        // }
        // Get the item similarity
        ItemSimilarity is = new FileItemSimilarity(rangeFile);
        // ItemBasedRecommender rec = new GenericItemBasedRecommender(dm, is);
        try {
            // System.out.println(dm.getPreferencesForItem(STARTING_IID));
            // System.out.println(dm.getPreferencesForItem(STARTING_IID + 1));
            // System.out.println("ItemBasedRecommender: most similar items: "
            // + rec.mostSimilarItems(STARTING_IID, 10));
            // System.out.println("ItemBasedRecommender: data model: " + rec.getDataModel());
            // System.out.println("ItemBasedRecommender: estimate preference: "
            // + rec.estimatePreference(STARTING_UID, STARTING_IID));
            System.out.println("ItemBasedRecommender: all similar items: "
                + is.allSimilarItemIDs(STARTING_IID).length);
        } catch (TasteException e) {
            System.err.println("ItemSimilarity failed");
            return null;
        }
        // System.out.println("Maximum preference: " + dm.getMaxPreference());
        // System.out.println("Minimum preference: " + dm.getMinPreference());
        System.out.println("Diff: " + diff);
        return null;
    }

    private static File writeFileItemNormalizedDiffs(List<Double> diffScoreList)
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
            for (int j = 0; j < percentDiffs.size(); j++)
                out.println(i + 1 + "," + (i + j + 2) + "," + percentDiffs.get(j) / maxAbsDiff);
        }
        System.out.println(tmpFile.getAbsolutePath());
        // tmpFile.deleteOnExit();
        out.close();
        return tmpFile;
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
        // System.out.println("Diff: " + diff + ", sr intercept: " + sr.getIntercept() + ", R: "
        // + sr.getR() + " R2: " + sr.getRSquare());
        diff *= sr.getRSquare();
        // Use the original starting price as a weight, so that ranges with the same difference will
        // differ based on where they started from
        // diff += range.get(0);
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
