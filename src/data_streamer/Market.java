/*
 *
 */
package data_streamer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

// import utils.Output;

/**
 * The Class Market.
 */
public class Market
{

    /** keeps track of feeds */
    private Timer                      timer;
    /** interacts with clients */
    private Exchange                   ex;

    /** path to market.config */
    private String                     config_file;
    /** different config variables */
    private Dictionary<String, String> config;
    /** name of config variables */
    private ArrayList<String>          config_names;

    /* log output */
    // private Output out;
    // private String output_path;

    /** convert yyyyMMdd hhmmssfff to long */
    protected SimpleDateFormat         f;

    /**
     * Instantiates a new market.
     */
    public Market(String cf, String df, String lf)
    {
        timer = new Timer(df);
        config_file = cf;
        // output_path = lf;
        ex = new Exchange();
        /* try { out = new Output(output_path, "market"); } catch (IOException e) { System.exit(1);
         * } */
        f = new SimpleDateFormat("yyyyMMdd hhmmssSSS");
    }

    /******************************
     * Configuration
     ******************************/

    /**
     * @throws IOException
     */
    public void loadConfigs() throws IOException, ParseException
    {
        config = new Hashtable<>();
        config_names = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new FileReader(config_file));
        String line;
        String[] parts;
        while ((line = br.readLine()) != null) {
            parts = line.split("=");
            config_names.add(parts[0]);
            config.put(parts[0], parts[1]);
        }
        br.close();

        processConfigs();
    }

    /**
     * @throws ParseException
     */
    private void processConfigs() throws ParseException
    {
        /* timer config vars */
        long window = Long.parseLong(config.get("window"));
        long start_time = f.parse(config.get("start_time")).getTime();
        long end_time = f.parse(config.get("end_time")).getTime();
        timer.loadConfigs(window, start_time, end_time, config.get("onirl"), config.get("onirs"));

    }

    /**
     * @throws IOException
     */
    public void setupTimer() throws IOException
    {
        timer.loadTimer();
    }

    /******************************
     * RUN TIME METHODS BELOW HERE
     ******************************/

    public void tick()
    {
        Dictionary<String, Dictionary<String, Dictionary<String, String>>> current;
        if (timer.running()) {
            // System.out.println("tick");
            current = timer.getCurrent(); // current is available
            // send to exchange
            ex.setCurrent(current);
            log(toString());
            // send client new data
            // DONE IN CLIENT
            // get client strategy decisions
            // DONE IN CLIENT
            // iterate
        }
    }

    /**
     * @return
     */
    public Exchange getEx()
    {
        return ex;
    }

    public Timer getTimer()
    {
        return timer;
    }

    /**
     * @return
     */
    public boolean running()
    {
        return timer.running();
    }

    public void log(String s)
    {
        // out.log(s);
    }

    public void endLog()
    {
        // out.end();
    }

    @Override
    public String toString()
    {
        return timer.toString();
    }

}
