/*
 *
 */
package data_streamer.feed;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Dictionary;

import data_streamer.utils.File;

/**
 * The Class Feed.
 */
@SuppressWarnings("unchecked")
public class Feed extends TimeSeries<Dictionary<String, String>>
{

    /**
     * Instantiates a new feed.
     *
     * @param filename the filename
     */
    public Feed(String filename, String p)
    {
        super(filename, p);
    }

    /**
     * Load feed from filename.
     *
     * @return true, if successful (file exists), false otherwise
     * @throws IOException
     */
    public void loadFeed() throws IOException
    {
        // System.out.println(path);
        q = File.import_csv(path);
    }

    /* pop and return top element */
    /* (non-Javadoc)
     * @see market.feed.TimeSeries#pop() */
    @Override
    public Dictionary<String, String> pop()
    {
        return q.remove(0);
    }

    @Override
    public ArrayList<Dictionary<String, String>> pop(long t) throws ParseException
    {
        ArrayList<Dictionary<String, String>> ret = new ArrayList<Dictionary<String, String>>();
        if (q.size() > 0)
            // System.out.println("sim time:\t" + t);
            // System.out.println("data time:\t" + peekTime());
            while (peekTime() < t)
                ret.add(pop());
        return ret;

    }

    /* peak at top element w/o removing */
    /* (non-Javadoc)
     * @see market.feed.TimeSeries#peek() */
    @Override
    public Dictionary<String, String> peek()
    {
        return q.get(0);
    }

    /* get time of top element */
    /* (non-Javadoc)
     * @see market.feed.TimeSeries#peak_time() */
    @Override
    public Long peekTime() throws ParseException
    {
        if (q.size() > 0)
            return f.parse(q.get(0).get("TIME")).getTime();
        return Long.MAX_VALUE;
    }

    /* push element to queue */
    /* (non-Javadoc)
     * @see market.feed.TimeSeries#push(java.util.Map) */
    @Override
    public void push(Dictionary<String, String> add)
    {
        q.add(add);
    }

    @Override
    public ArrayList<Dictionary<String, String>> grab(int n)
    {
        return new ArrayList<Dictionary<String, String>>(q.subList(0, n));
    }

    @Override
    public ArrayList<Dictionary<String, String>> grab(int start, int end)
    {
        return new ArrayList<Dictionary<String, String>>(q.subList(start, end));
    }

    @Override
    public String toString()
    {
        // TODO MAYBE
        return "";
    }
}
