/*
 *
 */
package data_streamer.feed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Queue;

/**
 * The Class TimeSeries.
 *
 * @param <T> the generic type
 */
public abstract class TimeSeries<T> implements Queue<Object>
{

    /** feed name */
    protected String                                name;
    /** path to file */
    protected String                                path;

    /** convert yyyyMMdd hhmmssfff to long */
    protected SimpleDateFormat                      f;

    /** time series */
    protected ArrayList<Dictionary<String, String>> q;

    /**
     * Instantiates a new time series.
     *
     * @param filename the filename
     */
    public TimeSeries(String filename, String p)
    {
        name = filename;
        path = p;
        q = new ArrayList<Dictionary<String, String>>();
        f = new SimpleDateFormat("yyyyMMdd hhmmssSSS");
    }

    public String getPath()
    {
        return path;
    }

    /**
     * pop and return top element
     *
     * @return the map
     */
    public abstract Dictionary<String, String> pop();

    /** peak at top element w/o removing */
    @Override
    public abstract Dictionary<String, String> peek();

    /** pops all past timeseries elements */
    public abstract ArrayList<Dictionary<String, String>> pop(long t) throws ParseException;

    /**
     * get time of top element
     *
     * @return the long
     */
    public abstract Long peekTime() throws ParseException;

    /**
     * push element to queue
     *
     * @param add the add
     */
    public abstract void push(Dictionary<String, String> add);

    /**
     * grab first n elements from queue without deleting DANGER THIS GIVES FUTURE INFORMATION
     *
     * @param n
     * @return
     */
    public abstract ArrayList<Dictionary<String, String>> grab(int n);

    /**
     * grab elements from start to end DANGER THIS GIVES FUTURE INFORMATION
     *
     * @param start
     * @param end
     * @return
     */
    public abstract ArrayList<Dictionary<String, String>> grab(int start, int end);

    @Override
    public int size()
    {
        return q.size();
    }

    @Override
    public boolean isEmpty()
    {
        return q.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return q.contains(o);
    }

    @Override
    public boolean remove(Object o)
    {
        return q.remove(o);
    }

    public ArrayList<Dictionary<String, String>> getQ()
    {
        return q;
    }

    /* unused VVV */
    @Override
    public Object[] toArray()
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] toArray(Object[] a)
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean containsAll(Collection c)
    {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean addAll(Collection c)
    {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean removeAll(Collection c)
    {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean retainAll(Collection c)
    {
        return false;
    }

    @Override
    public void clear()
    {
        q.clear();
    }

    @Override
    public boolean add(Object e)
    {
        return false;
    }

    @Override
    public boolean offer(Object e)
    {
        return false;
    }

    @Override
    public Object remove()
    {
        return null;
    }

    @Override
    public Object poll()
    {
        return null;
    }

    @Override
    public Object element()
    {
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Iterator iterator()
    {
        return null;
    }

}
