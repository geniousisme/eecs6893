/*
 *
 */
package data_streamer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import data_streamer.feed.Feed;

/**
 * The Class Timer.
 */
public class Timer
{

    /**
     * feeds is a dict that maps "type" (i.e. currency price, equity price, index, economic data,
     * etc) to its associated set of time series. Example: "price": EURUSD_series, USDJPY_series,
     * ... "Econ" : US_gdp, JP_gdp, ...
     */
    private Dictionary<String, Dictionary<String, Feed>>   all_feeds;     // all of the different
                                                                           // data feeds
    private Dictionary<String, ArrayList<String>>          feed_names_per; // feed names for each
                                                                           // type i.e. PRICE

    private Dictionary<String, String>                     type_files;    // location of type files
    private ArrayList<String>                              type_names;    // different types i.e
                                                                           // PRICE, INDEX
    private ArrayList<String>                              feed_names;     // different time series
                                                                           // i.e. USDJPY, SPY
    private Dictionary<String, Dictionary<String, String>> interest_rates;

    /* Config vars */
    /** the time window */
    private long                                           window;
    /** when to start thread */
    private long                                           start_time;
    /** when to end thread */
    private long                                           end_time;

    private String                                         data_file;

    /**
     * Instantiates a new timer.
     * 
     * @throws IOException
     */
    public Timer(String dfp)
    {
        data_file = dfp;
        run = true;
    }

    /******************************
     * Configuration
     ******************************/

    /**
     * @throws IOException
     */
    public void loadTimer() throws IOException
    {
        /* load types */
        getTypes();

        all_feeds = new Hashtable<>();
        for (String t : type_names)
            try {
                System.out.println("loading feeds: " + t);
                loadFeeds(t);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * @param w
     * @param s
     * @param e
     */
    public void loadConfigs(long w, long s, long e, String irl, String irs)
    {
        window = w;
        start_time = s;
        end_time = e;
        time = start_time;
        /* TODO load overnight interest rates */
        try {
            processInterestRates(irl, irs);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void processInterestRates(String irl, String irs) throws IOException,
        FileNotFoundException
    {

        interest_rates = new Hashtable<String, Dictionary<String, String>>();

        BufferedReader brl = new BufferedReader(new FileReader(irl));
        BufferedReader brs = new BufferedReader(new FileReader(irs));

        String line;
        String[] parts;

        /* Longs */
        while ((line = brl.readLine()) != null) {
            Dictionary<String, String> tmp = new Hashtable<String, String>();
            parts = line.split("=");
            tmp.put("LONG", parts[1]);
            interest_rates.put(parts[0], tmp);
            System.out.println("onir long added: " + parts[0]);
        }
        brl.close();

        /* shorts */
        while ((line = brs.readLine()) != null) {
            parts = line.split("=");
            interest_rates.get(parts[0]).put("SHORT", parts[1]);
            System.out.println("onir short added: " + parts[0]);
        }
        brs.close();

        System.out.println(interest_rates);
    }

    /**
     * @throws IOException
     */
    private void getTypes() throws IOException
    {
        type_files = new Hashtable<>();
        type_names = new ArrayList<String>();

        feed_names = new ArrayList<String>();
        feed_names_per = new Hashtable<String, ArrayList<String>>();

        BufferedReader br = new BufferedReader(new FileReader(data_file));
        String line;
        String[] parts;
        while ((line = br.readLine()) != null) {
            parts = line.split("=");
            type_names.add(parts[0]);
            type_files.put(parts[0], parts[1]);
            System.out.println("type added: " + parts[0]);
        }
        br.close();
    }

    /**
     * loads feeds based on a configuration file
     * 
     * @return
     * @throws FileNotFoundException
     */
    private void loadFeeds(String type) throws IOException
    {
        Dictionary<String, Feed> all_of_type = new Hashtable<>();
        ArrayList<String> feeds = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new FileReader(type_files.get(type)));
        String line;
        String[] parts;
        while ((line = br.readLine()) != null) {
            // process the line.
            parts = line.split("=");
            Feed feed = new Feed(parts[0], parts[1]);
            feed.loadFeed();
            feed_names.add(parts[0]);
            feeds.add(parts[0]);
            all_of_type.put(parts[0], feed);
            System.out.println("feed loaded: " + parts[0]);
        }

        feed_names_per.put(type, feeds);
        all_feeds.put(type, all_of_type);
        br.close();
        System.out.println("feeds loaded: " + type);
    }

    /**
     * @return
     */
    public ArrayList<String> getAllTypes()
    {
        return type_names;
    }

    /**
     * @return
     */
    public ArrayList<String> getAllFeeds()
    {
        return feed_names;
    }

    public Dictionary<String, Dictionary<String, Feed>> getFeeds()
    {
        return all_feeds;
    }

    /**
     * @return
     */
    public Dictionary<String, ArrayList<String>> getFeedNamesPer()
    {
        return feed_names_per;
    }

    /******************************
     * RUN TIME METHODS BELOW HERE
     ******************************/

    private Dictionary<String, Dictionary<String, Dictionary<String, String>>> current; // type /
                                                                                        // name /
                                                                                        // price
    private boolean                                                            run;
    private long                                                               time;

    /**
     * @return
     */
    public boolean running()
    {
        return run;
    }

    // Called by Consumer
    /**
     * @return
     * @throws InterruptedException
     * @throws ParseException
     */
    public Dictionary<String, Dictionary<String, Dictionary<String, String>>> getCurrent()
    {
        // return current
        time += window;
        System.out.println(time);
        try {
            current = new Hashtable<String, Dictionary<String, Dictionary<String, String>>>(); // type
                                                                                               // /
                                                                                               // feed
                                                                                               // /
                                                                                               // price
            if (current.isEmpty()) {

            }
            collectFeeds(time);
            if (current.isEmpty()) {
                // System.out.println("WHAZZAAAPP");
            }
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        if (time >= end_time)
            run = false;

        Dictionary<String, Dictionary<String, Dictionary<String, String>>> ret = current;
        // System.out.println("data collected");
        return ret; // return data to market
    }

    /**
     * @param time
     * @throws ParseException
     */
    @SuppressWarnings("deprecation")
    private void collectFeeds(long time) throws ParseException
    {
        /* aggregate data feeds into current */
        for (String type : type_names) {
            Dictionary<String, Dictionary<String, String>> temp = // dict of Name->data
                new Hashtable<String, Dictionary<String, String>>();
            for (String name : feed_names_per.get(type))
                if (all_feeds.get(type).get(name).peekTime() < time)
                    if (type.equalsIgnoreCase("PRICE")) {
                        ArrayList<Dictionary<String, String>> temp_arr =
                            all_feeds.get(type).get(name).pop(time);
                        temp.put(name, extractAllPrice(temp_arr));
                    }
            // TODO add other types here
            // System.out.println("temp " + temp);
            current.put(type, temp);
            // System.out.println("current " + current);
        }

        /* add overnight rate at 5pm */
        Date date = new Date(time);
        if (date.getDay() != Calendar.SATURDAY && date.getDay() != Calendar.SUNDAY
            && date.getHours() == 17 && date.getMinutes() == 0) {
            current.put("ONIR", interest_rates);
            System.out.println("Interest rates put");
        }
    }

    private Dictionary<String, String> extractAllPrice(
        ArrayList<Dictionary<String, String>> temp_arr)
    {
        Dictionary<String, String> ret = new Hashtable<String, String>();
        ret.put("MAX", extractMax(temp_arr));
        ret.put("MIN", extractMin(temp_arr));
        ret.put("AVERAGE", extractAve(temp_arr));

        return ret;
    }

    private String extractAve(ArrayList<Dictionary<String, String>> temp_arr)
    {
        double ave = 0.0;
        for (Dictionary<String, String> d : temp_arr)
            ave = ave + (Double.parseDouble(d.get("MAX")) + Double.parseDouble(d.get("MIN")) / 2.0);
        if (temp_arr.size() > 0)
            ave = ave / temp_arr.size();
        return Double.toString(ave);
    }

    private String extractMin(ArrayList<Dictionary<String, String>> temp_arr)
    {
        double min = Double.MAX_VALUE;
        for (Dictionary<String, String> d : temp_arr)
            if (Double.parseDouble(d.get("MIN")) < min)
                min = Double.parseDouble(d.get("MIN"));
        if (min >= Double.MAX_VALUE)
            min = 0.0;
        return Double.toString(min);
    }

    private String extractMax(ArrayList<Dictionary<String, String>> temp_arr)
    {
        double max = 0.0;
        for (Dictionary<String, String> d : temp_arr)
            if (Double.parseDouble(d.get("MAX")) > max)
                max = Double.parseDouble(d.get("MAX"));
        return Double.toString(max);
    }

    @Override
    public String toString()
    {
        if (current == null)
            return "Must Tick first";
        String s = "";
        for (String type : type_names)
            for (String feed : feed_names_per.get(type))
                try {
                    s =
                        s + type + "," + feed + ","
                            + parseCurrent((Hashtable<String, String>) current.get(type).get(feed));
                } catch (NullPointerException e) {
                    s = s + type + "," + feed + "," + "none" + ",";
                }

        return s.subSequence(0, s.length() - 1).toString();
    }

    private String parseCurrent(Hashtable<String, String> d)
    {
        String s = "";
        for (String key : d.keySet())
            s = s + key + "," + d.get(key) + ",";
        return s;
    }

}
