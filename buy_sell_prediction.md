- Buy/Sell Prediction

Wise men often say look to the past to see the future. Our Buy/Sell prediction takes that to heart in its analysis. With the wealth of knowledge of previous trends, we can make extremely accurate predictions about whether or not to sell based on previous trends.

The algorithm takes in two lists of trades in a market; the recent history of the market (e.g. the test set), that you want it to decide on, and the entire history of the market, sans the test set (e.g. the train set). The more training history you give it, the more judicious it can be in what it considers similar, and the more accurate its final decision will be.

-- Score

The first task it performs is it computes scores of ranges of trades. The score is the sum of the difference between the ith item and the first item (i does not include the first item), multiplied by the r2 of the least linear regression. of the range. For example, the range:

```
        {1.0, 2.0, 3.0, 4.0, 5.0}
```

Has a score of 10, which indicates an increasing market. A range like this, though:

```
        {1.0, 2.0, 3.0, 4.0, 5.0, 1.0}
```

Has a score of 1.071. The range is heavily penalized for having a poor regression, and thus being unreliable, even though its difference sum is exactly the same (10).

-- Normalizing

The second task is to normalize all the scores, and feed them to Mahout for processing. In order for Mahout to utilize the values, we have to correlate the scores in some way. The way this is approached is by the following:
- For every two scores:
    - We compute the percent difference between the scores. A larger percent difference therefore means that two ranges are more dissimilar.
    - We then subtract this score from -1 (1) for negative (positive) results, which gives us the exact opposite semantic: more similar scores are closer to 1 (anti-similar scores are closer to -1, i.e. the ranges trend in opposite directions).

This gives us n^2 scores, as each range has been correlated with every other range. We give this to a Mahout ItemSimilarity, which then considers the similarity matrix of all ranges.

-- Cherry Picking

We then ask Mahout to score the similarity of all ranges to the test range. We sort by most similar, and only filter results that are trending in the same direction (due to the small variance, some ranges may be erratic and thus be similar because of poor correlaion, even though they are trending in opposite directions. Recall that the similarity of two ranges is a function of their similarity, and the other ranges they are similar to).

Once we have the most similar ranges, we score a small range of the market ahead of that range, and see which direction the market went in.
- If, for a majority of those ranges, the market went up, we say SELL (you're going to lose money otherwise)!
- If, for a majority of those ranges, the market went down, we say BUY (you're going to be rich soon)!
- Otherwise, just hold (the market is too unpredictable in similar scenarios).

-- Decision Duration

We can also approximate how many ticks of the market this decision will be valid for. Going back to Cherry Picking, we peek into the future to see whether to buy or sell. By continuing to look forward into the future, we can see how long this trend will last.

To decide how long to maintain a decision, we continue evaluating the future market, until we run out of history (we reach the present), or the trend is broken. We maintain how long each similar range lasted for, average their durations, and return that as our decision.
