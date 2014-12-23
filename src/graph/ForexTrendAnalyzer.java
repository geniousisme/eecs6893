package graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import data_streamer.Market;
import data_streamer.analytics.TradeRecommender;
import data_streamer.analytics.TradeRecommender.BuyDecision;

/**
 * A demonstration application showing a time series chart where you can
 * dynamically add (random) data by clicking on a button.
 */
public class ForexTrendAnalyzer extends JFrame implements ActionListener
{

    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    private JMenu             display, trends_m;
    private JMenuItem         prices_mi, sma_mi, sd_mi, bb_mi, ema_mi,
        predictions_mi, exitMenuItem;
    private JMenu             exitMenu;
    private JFrame            sma_frame, sd_frame, ema_frame, bb_frame, pframe,
        pdframe;
    private JTabbedPane       prices, sma, sd, bb, ema;
    private JPanel            pred;
    private Dimension         screenSize       = Toolkit.getDefaultToolkit()
                                                   .getScreenSize();

    private JLabel            eur_jl, cad_jl, chf_jl, nzd_jl, jpy_jl, aud_jl,
        gbp_jl;

    /** Price **/
    private TimeSeries        cadusd_price, eurusd_price, jpyusd_price,
        nzdusd_price, gbpusd_price, chfusd_price, audusd_price;
    private TimeSeriesCollection cadds_price, eurds_price, jpyds_price,
        nzdds_price, gbpds_price, chfds_price, audds_price;
    private JFreeChart           cadc_price, eurc_price, jpyc_price,
        nzdc_price, gbpc_price, chfc_price, audc_price;
    private ChartPanel           cadcp_price, eurcp_price, jpycp_price,
        nzdcp_price, gbpcp_price, chfcp_price, audcp_price;
    private JPanel               cadcon_price, eurcon_price, jpycon_price,
        nzdcon_price, gbpcon_price, chfcon_price, audcon_price;
    /** End Price **/

    /** Trend **/
    private ArrayList<Double>    simple_moving_average_5_cad,
        simple_moving_average_5_eur, simple_moving_average_5_jpy,
        simple_moving_average_5_nzd, simple_moving_average_5_chf,
        simple_moving_average_5_gbp, simple_moving_average_5_aud,
        simple_moving_average_10_cad, simple_moving_average_10_eur,
        simple_moving_average_10_jpy, simple_moving_average_10_nzd,
        simple_moving_average_10_chf, simple_moving_average_10_gbp,
        simple_moving_average_10_aud, standard_deviation_10_cad,
        standard_deviation_10_eur, standard_deviation_10_jpy,
        standard_deviation_10_nzd, standard_deviation_10_gbp,
        standard_deviation_10_chf, standard_deviation_10_aud, cad, eur, jpy,
        nzd, chf, gbp, aud;
    private TimeSeries           cadusd_sma5, eurusd_sma5, jpyusd_sma5,
        nzdusd_sma5, chfusd_sma5, gbpusd_sma5, audusd_sma5, cadusd_sma10,
        eurusd_sma10, jpyusd_sma10, nzdusd_sma10, gbpusd_sma10, chfusd_sma10,
        audusd_sma10, cadusd_sd10, eurusd_sd10, jpyusd_sd10, nzdusd_sd10,
        chfusd_sd10, gbpusd_sd10, audusd_sd10, cadusd_bb10_h, cadusd_bb10_l;
    private TimeSeriesCollection cadds_sma5, eurds_sma5, jpyds_sma5,
        nzdds_sma5, chfds_sma5, gbpds_sma5, audds_sma5, cadds_sma10,
        eurds_sma10, jpyds_sma10, gbpds_sma10, nzdds_sma10, audds_sma10,
        chfds_sma10, cadds_sd10, eurds_sd10, jpyds_sd10, nzdds_sd10,
        gbpds_sd10, audds_sd10, chfds_sd10, cadds_bb10;
    private JFreeChart           cadc_sma5, eurc_sma5, jpyc_sma5, audc_sma5,
        nzdc_sma5, gbpc_sma5, chfc_sma5, cadc_sma10, eurc_sma10, jpyc_sma10,
        nzdc_sma10, gbpc_sma10, audc_sma10, chfc_sma10, cadc_sd10, eurc_sd10,
        jpyc_sd10, nzdc_sd10, gbpc_sd10, chfc_sd10, audc_sd10, cadc_bb10;
    private ChartPanel           cadcp_sma5, eurcp_sma5, jpycp_sma5,
        nzdcp_sma5, chfcp_sma5, gbpcp_sma5, audcp_sma5, cadcp_sma10,
        eurcp_sma10, jpycp_sma10, nzdcp_sma10, chfcp_sma10, gbpcp_sma10,
        audcp_sma10, cadcp_sd10, eurcp_sd10, jpycp_sd10, nzdcp_sd10,
        chfcp_sd10, gbpcp_sd10, audcp_sd10, cadcp_bb10;
    private JPanel               cadcon_sma5, eurcon_sma5, jpycon_sma5,
        nzdcon_sma5, gbpcon_sma5, chfcon_sma5, audcon_sma5, cadcon_sma10,
        eurcon_sma10, jpycon_sma10, nzdcon_sma10, gbpcon_sma10, chfcon_sma10,
        audcon_sma10, cadcon_sd10, eurcon_sd10, jpycon_sd10, nzdcon_sd10,
        gbpcon_sd10, chfcon_sd10, audcon_sd10, cadcon_bb10, gbp_pp;

    /** End Trend **/

    public ForexTrendAnalyzer()
    {
        super("Forex Analyzer");

        display = new JMenu("Display");
        exitMenu = new JMenu("Exit");
        trends_m = new JMenu("Trends");

        prices_mi = new JMenuItem("Prices");
        predictions_mi = new JMenuItem("Predictions");
        sma_mi = new JMenuItem("Simple Moving Averages");
        ema_mi = new JMenuItem("Exponential Moving Averages");
        sd_mi = new JMenuItem("Standard deviations");
        bb_mi = new JMenuItem("Bollinger Bands");

        exitMenuItem = new JMenuItem("Exit");

        trends_m.add(sma_mi);
        trends_m.add(ema_mi);
        trends_m.add(sd_mi);
        trends_m.add(bb_mi);

        display.add(prices_mi);
        display.add(trends_m);
        display.add(predictions_mi);

        exitMenu.add(exitMenuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(display);
        menuBar.add(exitMenu);

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu currentMenu1 = menuBar.getMenu(i);
            for (int j = 0; j < currentMenu1.getItemCount(); j++) {
                JMenuItem currentItem = currentMenu1.getItem(j);
                if (!currentItem.getClass().toString().contains(
                    "javax.swing.JMenuItem"))
                    for (int k = 0; k < ((JMenu) currentItem).getItemCount(); k++) {
                        JMenuItem currentItem2 =
                            ((JMenu) currentItem).getItem(k);
                        if (currentItem2 != null)
                            currentItem2.addActionListener(this);
                    }
                if (currentItem != null)
                    currentItem.addActionListener(this);
            }
        }

        setJMenuBar(menuBar);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocation((screenSize.width - getSize().width) / 2,
            (screenSize.height - getSize().height) / 2);
    }

    /**
	 *
	 */
    public void run()
    {

        Market mkt =
            new Market(
                "/Users/theocean154/Documents/School_files/College/Programs/eclipse/bda/src/config/market.config",
                "/Users/theocean154/Documents/School_files/College/Programs/eclipse/bda/src/config/data_config.txt",
                "./");

        try {
            mkt.loadConfigs();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            mkt.setupTimer();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        /** PRICE DATA **/
        /* CADUSD */
        cadusd_price = new TimeSeries("CADUSD-Price", Millisecond.class);
        cadds_price = new TimeSeriesCollection(cadusd_price);
        cadc_price = createChart(cadds_price, "CADUSD", "TIME", "PRICE");
        cadcp_price = new ChartPanel(cadc_price);
        cadcon_price = new JPanel(new BorderLayout());
        cadcon_price.add(cadcp_price);
        cadcp_price.setPreferredSize(new java.awt.Dimension(500, 270));

        /* EURUSD */
        eurusd_price = new TimeSeries("EURUSD-Price", Millisecond.class);
        eurds_price = new TimeSeriesCollection(eurusd_price);
        eurc_price = createChart(eurds_price, "EURUSD", "TIME", "PRICE");
        eurcp_price = new ChartPanel(eurc_price);
        eurcon_price = new JPanel(new BorderLayout());
        eurcon_price.add(eurcp_price);
        eurcp_price.setPreferredSize(new java.awt.Dimension(500, 270));

        /* JPYUSD */
        jpyusd_price = new TimeSeries("JPYUSD-Price", Millisecond.class);
        jpyds_price = new TimeSeriesCollection(jpyusd_price);
        jpyc_price = createChart(jpyds_price, "JPYUSD", "TIME", "PRICE");
        jpycp_price = new ChartPanel(jpyc_price);
        jpycon_price = new JPanel(new BorderLayout());
        jpycon_price.add(jpycp_price);
        jpycp_price.setPreferredSize(new java.awt.Dimension(500, 270));

        /* CHFUSD */
        chfusd_price = new TimeSeries("CHFUSD-Price", Millisecond.class);
        chfds_price = new TimeSeriesCollection(chfusd_price);
        chfc_price = createChart(chfds_price, "CHFUSD", "TIME", "PRICE");
        chfcp_price = new ChartPanel(chfc_price);
        chfcon_price = new JPanel(new BorderLayout());
        chfcon_price.add(chfcp_price);
        chfcp_price.setPreferredSize(new java.awt.Dimension(500, 270));

        /* AUDUSD */
        audusd_price = new TimeSeries("AUDUSD-Price", Millisecond.class);
        audds_price = new TimeSeriesCollection(audusd_price);
        audc_price = createChart(audds_price, "AUDUSD", "TIME", "PRICE");
        audcp_price = new ChartPanel(audc_price);
        audcon_price = new JPanel(new BorderLayout());
        audcon_price.add(audcp_price);
        audcp_price.setPreferredSize(new java.awt.Dimension(500, 270));

        /* GBPUSD */
        gbpusd_price = new TimeSeries("GBPUSD-Price", Millisecond.class);
        gbpds_price = new TimeSeriesCollection(gbpusd_price);
        gbpc_price = createChart(gbpds_price, "GBPUSD", "TIME", "PRICE");
        gbpcp_price = new ChartPanel(gbpc_price);
        gbpcon_price = new JPanel(new BorderLayout());
        gbpcon_price.add(gbpcp_price);
        gbpcp_price.setPreferredSize(new java.awt.Dimension(500, 270));

        /* NZDUSD */
        nzdusd_price = new TimeSeries("NZDUSD-Price", Millisecond.class);
        nzdds_price = new TimeSeriesCollection(nzdusd_price);
        nzdc_price = createChart(nzdds_price, "NZDUSD", "TIME", "PRICE");
        nzdcp_price = new ChartPanel(nzdc_price);
        nzdcon_price = new JPanel(new BorderLayout());
        nzdcon_price.add(nzdcp_price);
        nzdcp_price.setPreferredSize(new java.awt.Dimension(500, 270));

        /** END OF PRICE DATA **/

        /** PRICE FRAMES **/
        prices = new JTabbedPane();
        prices.addTab("CADUSD", cadcon_price);
        prices.addTab("EURUSD", eurcon_price);
        prices.addTab("JPYUSD", jpycon_price);
        prices.addTab("CHFUSD", chfcon_price);
        prices.addTab("GBPUSD", gbpcon_price);
        prices.addTab("NZDUSD", nzdcon_price);
        prices.addTab("AUDUSD", audcon_price);
        pframe = new JFrame();
        pframe.setDefaultCloseOperation(HIDE_ON_CLOSE);
        pframe.getContentPane().add(prices);
        pframe.setTitle("Forex Trend Analyzer");
        pframe.pack();
        pframe.setLocation((screenSize.width - pframe.getSize().width) / 2,
            (screenSize.height - pframe.getSize().height) / 2);
        /** END OF PRICE FRAMES **/

        /** TREND DATA **/
        simple_moving_average_5_cad = new ArrayList<Double>();
        cadusd_sma5 = new TimeSeries("CADUSD-SMA5", Millisecond.class);
        cadds_sma5 = new TimeSeriesCollection();
        cadds_sma5.addSeries(cadusd_sma5);
        cadds_sma5.addSeries(cadusd_price);
        cadc_sma5 =
            createChart(cadds_sma5, "CADUSD-SMA5", "TIME",
                "5 Tick Simple Moving Average");
        cadcp_sma5 = new ChartPanel(cadc_sma5);
        cadcon_sma5 = new JPanel(new BorderLayout());
        cadcon_sma5.add(cadcp_sma5);
        cadcp_sma5.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_5_eur = new ArrayList<Double>();
        eurusd_sma5 = new TimeSeries("EURUSD-SMA5", Millisecond.class);
        eurds_sma5 = new TimeSeriesCollection();
        eurds_sma5.addSeries(eurusd_sma5);
        eurds_sma5.addSeries(eurusd_price);
        eurc_sma5 =
            createChart(eurds_sma5, "EURUSD-SMA5", "TIME",
                "5 Tick Simple Moving Average");
        eurcp_sma5 = new ChartPanel(eurc_sma5);
        eurcon_sma5 = new JPanel(new BorderLayout());
        eurcon_sma5.add(eurcp_sma5);
        eurcp_sma5.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_5_jpy = new ArrayList<Double>();
        jpyusd_sma5 = new TimeSeries("JPYUSD-SMA5", Millisecond.class);
        jpyds_sma5 = new TimeSeriesCollection();
        jpyds_sma5.addSeries(jpyusd_sma5);
        jpyds_sma5.addSeries(jpyusd_price);
        jpyc_sma5 =
            createChart(jpyds_sma5, "JPYUSD-SMA5", "TIME",
                "5 Tick Simple Moving Average");
        jpycp_sma5 = new ChartPanel(jpyc_sma5);
        jpycon_sma5 = new JPanel(new BorderLayout());
        jpycon_sma5.add(jpycp_sma5);
        jpycp_sma5.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_5_chf = new ArrayList<Double>();
        chfusd_sma5 = new TimeSeries("CHFUSD-SMA5", Millisecond.class);
        chfds_sma5 = new TimeSeriesCollection();
        chfds_sma5.addSeries(chfusd_sma5);
        chfds_sma5.addSeries(chfusd_price);
        chfc_sma5 =
            createChart(chfds_sma5, "CHFUSD-SMA5", "TIME",
                "5 Tick Simple Moving Average");
        chfcp_sma5 = new ChartPanel(chfc_sma5);
        chfcon_sma5 = new JPanel(new BorderLayout());
        chfcon_sma5.add(chfcp_sma5);
        chfcp_sma5.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_5_nzd = new ArrayList<Double>();
        nzdusd_sma5 = new TimeSeries("NZDUSD-SMA5", Millisecond.class);
        nzdds_sma5 = new TimeSeriesCollection();
        nzdds_sma5.addSeries(nzdusd_sma5);
        nzdds_sma5.addSeries(nzdusd_price);
        nzdc_sma5 =
            createChart(nzdds_sma5, "NZDUSD-SMA5", "TIME",
                "5 Tick Simple Moving Average");
        nzdcp_sma5 = new ChartPanel(nzdc_sma5);
        nzdcon_sma5 = new JPanel(new BorderLayout());
        nzdcon_sma5.add(nzdcp_sma5);
        nzdcp_sma5.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_5_gbp = new ArrayList<Double>();
        gbpusd_sma5 = new TimeSeries("GBPUSD-SMA5", Millisecond.class);
        gbpds_sma5 = new TimeSeriesCollection();
        gbpds_sma5.addSeries(gbpusd_sma5);
        gbpds_sma5.addSeries(gbpusd_price);
        gbpc_sma5 =
            createChart(gbpds_sma5, "GBPUSD-SMA5", "TIME",
                "5 Tick Simple Moving Average");
        gbpcp_sma5 = new ChartPanel(gbpc_sma5);
        gbpcon_sma5 = new JPanel(new BorderLayout());
        gbpcon_sma5.add(gbpcp_sma5);
        gbpcp_sma5.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_5_aud = new ArrayList<Double>();
        audusd_sma5 = new TimeSeries("AUDUSD-SMA5", Millisecond.class);
        audds_sma5 = new TimeSeriesCollection();
        audds_sma5.addSeries(audusd_sma5);
        audds_sma5.addSeries(audusd_price);
        audc_sma5 =
            createChart(audds_sma5, "AUDUSD-SMA5", "TIME",
                "5 Tick Simple Moving Average");
        audcp_sma5 = new ChartPanel(audc_sma5);
        audcon_sma5 = new JPanel(new BorderLayout());
        audcon_sma5.add(audcp_sma5);
        audcp_sma5.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_10_cad = new ArrayList<Double>();
        cadusd_sma10 = new TimeSeries("CADUSD-SMA10", Millisecond.class);
        cadds_sma10 = new TimeSeriesCollection();
        cadds_sma10.addSeries(cadusd_sma10);
        cadds_sma10.addSeries(cadusd_price);
        cadc_sma10 =
            createChart(cadds_sma10, "CADUSD-SMA10", "TIME",
                "10 Tick Simple Moving Average");
        cadcp_sma10 = new ChartPanel(cadc_sma10);
        cadcon_sma10 = new JPanel(new BorderLayout());
        cadcon_sma10.add(cadcp_sma10);
        cadcp_sma10.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_10_eur = new ArrayList<Double>();
        eurusd_sma10 = new TimeSeries("EURUSD-SMA10", Millisecond.class);
        eurds_sma10 = new TimeSeriesCollection();
        eurds_sma10.addSeries(eurusd_sma10);
        eurds_sma10.addSeries(eurusd_price);
        eurc_sma10 =
            createChart(eurds_sma10, "EURUSD-SMA10", "TIME",
                "10 Tick Simple Moving Average");
        eurcp_sma10 = new ChartPanel(eurc_sma10);
        eurcon_sma10 = new JPanel(new BorderLayout());
        eurcon_sma10.add(eurcp_sma10);
        eurcp_sma10.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_10_jpy = new ArrayList<Double>();
        jpyusd_sma10 = new TimeSeries("JPYUSD-SMA10", Millisecond.class);
        jpyds_sma10 = new TimeSeriesCollection();
        jpyds_sma10.addSeries(jpyusd_sma10);
        jpyds_sma10.addSeries(jpyusd_price);
        jpyc_sma10 =
            createChart(jpyds_sma10, "JPYUSD-SMA10", "TIME",
                "10 Tick Simple Moving Average");
        jpycp_sma10 = new ChartPanel(jpyc_sma10);
        jpycon_sma10 = new JPanel(new BorderLayout());
        jpycon_sma10.add(jpycp_sma10);
        jpycp_sma10.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_10_nzd = new ArrayList<Double>();
        nzdusd_sma10 = new TimeSeries("NZDUSD-SMA10", Millisecond.class);
        nzdds_sma10 = new TimeSeriesCollection();
        nzdds_sma10.addSeries(nzdusd_sma10);
        nzdds_sma10.addSeries(nzdusd_price);
        nzdc_sma10 =
            createChart(nzdds_sma10, "NZDUSD-SMA10", "TIME",
                "10 Tick Simple Moving Average");
        nzdcp_sma10 = new ChartPanel(nzdc_sma10);
        nzdcon_sma10 = new JPanel(new BorderLayout());
        nzdcon_sma10.add(nzdcp_sma10);
        nzdcp_sma10.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_10_gbp = new ArrayList<Double>();
        gbpusd_sma10 = new TimeSeries("GBPUSD-SMA10", Millisecond.class);
        gbpds_sma10 = new TimeSeriesCollection();
        gbpds_sma10.addSeries(gbpusd_sma10);
        gbpds_sma10.addSeries(gbpusd_price);
        gbpc_sma10 =
            createChart(gbpds_sma10, "GBPUSD-SMA10", "TIME",
                "10 Tick Simple Moving Average");
        gbpcp_sma10 = new ChartPanel(gbpc_sma10);
        gbpcon_sma10 = new JPanel(new BorderLayout());
        gbpcon_sma10.add(gbpcp_sma10);
        gbpcp_sma10.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_10_chf = new ArrayList<Double>();
        chfusd_sma10 = new TimeSeries("CHFUSD-SMA10", Millisecond.class);
        chfds_sma10 = new TimeSeriesCollection();
        chfds_sma10.addSeries(chfusd_sma10);
        chfds_sma10.addSeries(chfusd_price);
        chfc_sma10 =
            createChart(chfds_sma10, "CHFUSD-SMA10", "TIME",
                "10 Tick Simple Moving Average");
        chfcp_sma10 = new ChartPanel(chfc_sma10);
        chfcon_sma10 = new JPanel(new BorderLayout());
        chfcon_sma10.add(chfcp_sma10);
        chfcp_sma10.setPreferredSize(new java.awt.Dimension(500, 270));

        simple_moving_average_10_aud = new ArrayList<Double>();
        audusd_sma10 = new TimeSeries("AUDUSD-SMA10", Millisecond.class);
        audds_sma10 = new TimeSeriesCollection();
        audds_sma10.addSeries(audusd_sma10);
        audds_sma10.addSeries(audusd_price);
        audc_sma10 =
            createChart(audds_sma10, "AUDUSD-SMA10", "TIME",
                "10 Tick Simple Moving Average");
        audcp_sma10 = new ChartPanel(audc_sma10);
        audcon_sma10 = new JPanel(new BorderLayout());
        audcon_sma10.add(audcp_sma10);
        audcp_sma10.setPreferredSize(new java.awt.Dimension(500, 270));

        standard_deviation_10_cad = new ArrayList<Double>();
        cadusd_sd10 = new TimeSeries("CADUSD-SD10", Millisecond.class);
        cadds_sd10 = new TimeSeriesCollection();
        cadds_sd10.addSeries(cadusd_sd10);
        // cadds_sd10.addSeries(cadusd_price);
        cadc_sd10 =
            createChart(cadds_sd10, "CADUSD-SD10", "TIME",
                "10 Tick Standard Deviation");
        cadcp_sd10 = new ChartPanel(cadc_sd10);
        cadcon_sd10 = new JPanel(new BorderLayout());
        cadcon_sd10.add(cadcp_sd10);
        cadcp_sd10.setPreferredSize(new java.awt.Dimension(500, 270));

        standard_deviation_10_eur = new ArrayList<Double>();
        eurusd_sd10 = new TimeSeries("EURUSD-SD10", Millisecond.class);
        eurds_sd10 = new TimeSeriesCollection();
        eurds_sd10.addSeries(eurusd_sd10);
        // eurds_sd10.addSeries(eurusd_price);
        eurc_sd10 =
            createChart(eurds_sd10, "EURUSD-SD10", "TIME",
                "10 Tick Standard Deviation");
        eurcp_sd10 = new ChartPanel(eurc_sd10);
        eurcon_sd10 = new JPanel(new BorderLayout());
        eurcon_sd10.add(eurcp_sd10);
        eurcp_sd10.setPreferredSize(new java.awt.Dimension(500, 270));

        standard_deviation_10_jpy = new ArrayList<Double>();
        jpyusd_sd10 = new TimeSeries("JPYUSD-SD10", Millisecond.class);
        jpyds_sd10 = new TimeSeriesCollection();
        eurds_sd10.addSeries(eurusd_sd10);
        // eurds_sd10.addSeries(eurusd_price);
        jpyc_sd10 =
            createChart(jpyds_sd10, "JPYUSD-SD10", "TIME",
                "10 Tick Standard Deviation");
        jpycp_sd10 = new ChartPanel(jpyc_sd10);
        jpycon_sd10 = new JPanel(new BorderLayout());
        jpycon_sd10.add(jpycp_sd10);
        jpycp_sd10.setPreferredSize(new java.awt.Dimension(500, 270));

        standard_deviation_10_nzd = new ArrayList<Double>();
        nzdusd_sd10 = new TimeSeries("NZDUSD-SD10", Millisecond.class);
        nzdds_sd10 = new TimeSeriesCollection(nzdusd_sd10);
        nzdds_sd10.addSeries(nzdusd_sd10);
        // nzdds_sd10.addSeries(nzdusd_price);
        nzdc_sd10 =
            createChart(nzdds_sd10, "NZDUSD-SD10", "TIME",
                "10 Tick Standard Deviation");
        nzdcp_sd10 = new ChartPanel(nzdc_sd10);
        nzdcon_sd10 = new JPanel(new BorderLayout());
        nzdcon_sd10.add(nzdcp_sd10);
        nzdcp_sd10.setPreferredSize(new java.awt.Dimension(500, 270));

        standard_deviation_10_chf = new ArrayList<Double>();
        chfusd_sd10 = new TimeSeries("CHFUSD-SD10", Millisecond.class);
        chfds_sd10 = new TimeSeriesCollection();
        chfds_sd10.addSeries(chfusd_sd10);
        // chfds_sd10.addSeries(chfusd_price);
        chfc_sd10 =
            createChart(chfds_sd10, "CHFUSD-SD10", "TIME",
                "10 Tick Standard Deviation");
        chfcp_sd10 = new ChartPanel(chfc_sd10);
        chfcon_sd10 = new JPanel(new BorderLayout());
        chfcon_sd10.add(chfcp_sd10);
        chfcp_sd10.setPreferredSize(new java.awt.Dimension(500, 270));

        standard_deviation_10_gbp = new ArrayList<Double>();
        gbpusd_sd10 = new TimeSeries("GBPUSD-SD10", Millisecond.class);
        gbpds_sd10 = new TimeSeriesCollection();
        gbpds_sd10.addSeries(gbpusd_sd10);
        // gbpds_sd10.addSeries(gbpusd_price);
        gbpc_sd10 =
            createChart(gbpds_sd10, "GBPUSD-SD10", "TIME",
                "10 Tick Standard Deviation");
        gbpcp_sd10 = new ChartPanel(gbpc_sd10);
        gbpcon_sd10 = new JPanel(new BorderLayout());
        gbpcon_sd10.add(gbpcp_sd10);
        gbpcp_sd10.setPreferredSize(new java.awt.Dimension(500, 270));

        standard_deviation_10_aud = new ArrayList<Double>();
        audusd_sd10 = new TimeSeries("AUDUSD-SD10", Millisecond.class);
        audds_sd10 = new TimeSeriesCollection();
        audds_sd10.addSeries(audusd_sd10);
        // audds_sd10.addSeries(audusd_price);
        audc_sd10 =
            createChart(audds_sd10, "AUDUSD-SD10", "TIME",
                "10 Tick Standard Deviation");
        audcp_sd10 = new ChartPanel(audc_sd10);
        audcon_sd10 = new JPanel(new BorderLayout());
        audcon_sd10.add(audcp_sd10);
        audcp_sd10.setPreferredSize(new java.awt.Dimension(500, 270));

        cadusd_bb10_h = new TimeSeries("CADUSD-BBH", Millisecond.class);
        cadusd_bb10_l = new TimeSeries("CADUSD-BBL", Millisecond.class);
        cadds_bb10 = new TimeSeriesCollection();
        cadds_bb10.addSeries(cadusd_price);
        cadds_bb10.addSeries(cadusd_bb10_h);
        cadds_bb10.addSeries(cadusd_bb10_l);
        cadc_bb10 =
            createChart(cadds_bb10, "CADUSD-BB10", "TIME",
                "10 Tick Bollinger Bands");
        cadcp_bb10 = new ChartPanel(cadc_bb10);
        cadcon_bb10 = new JPanel(new BorderLayout());
        cadcon_bb10.add(cadcp_bb10);
        cadcp_bb10.setPreferredSize(new java.awt.Dimension(500, 270));

        cad = new ArrayList<Double>();
        aud = new ArrayList<Double>();
        eur = new ArrayList<Double>();
        nzd = new ArrayList<Double>();
        jpy = new ArrayList<Double>();
        gbp = new ArrayList<Double>();
        chf = new ArrayList<Double>();

        /** END OF TREND DATA **/

        /** TRENDS FRAMES **/
        sma = new JTabbedPane();
        ema = new JTabbedPane();
        sd = new JTabbedPane();
        bb = new JTabbedPane();

        sma_frame = new JFrame();
        sd_frame = new JFrame();
        ema_frame = new JFrame();
        bb_frame = new JFrame();

        sma.addTab("CADUSD-SMA5", cadcon_sma5);
        sma.addTab("EURUSD-SMA5", eurcon_sma5);
        sma.addTab("JPYUSD-SMA5", jpycon_sma5);
        sma.addTab("NZDUSD-SMA5", nzdcon_sma5);
        sma.addTab("GBPUSD-SMA5", gbpcon_sma5);
        sma.addTab("CHFUSD-SMA5", chfcon_sma5);
        sma.addTab("AUDUSD-SMA5", audcon_sma5);
        sma.addTab("CADUSD-SMA10", cadcon_sma10);
        sma.addTab("EURUSD-SMA10", eurcon_sma10);
        sma.addTab("JPYUSD-SMA10", jpycon_sma10);
        sma.addTab("NZDUSD-SMA10", nzdcon_sma10);
        sma.addTab("GBPUSD-SMA10", gbpcon_sma10);
        sma.addTab("CHFUSD-SMA10", chfcon_sma10);
        sma.addTab("AUDUSD-SMA10", audcon_sma10);

        sd.addTab("CADUSD-SD10", cadcon_sd10);
        sd.addTab("EURUSD-SD10", eurcon_sd10);
        sd.addTab("JPYUSD-SD10", jpycon_sd10);
        sd.addTab("NZDUSD-SD10", nzdcon_sd10);
        sd.addTab("GBPUSD-SD10", gbpcon_sd10);
        sd.addTab("CHFUSD-SD10", chfcon_sd10);
        sd.addTab("AUDUSD-SD10", audcon_sd10);

        bb.addTab("CADUSD-BB10", cadcon_bb10);

        sma_frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        sd_frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        ema_frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        bb_frame.setDefaultCloseOperation(HIDE_ON_CLOSE);

        sma_frame.getContentPane().add(sma);
        sd_frame.getContentPane().add(sd);
        bb_frame.getContentPane().add(bb);

        sma_frame.setTitle("Forex Trend Analyzer");
        sd_frame.setTitle("Forex Trend Analyzer");
        ema_frame.setTitle("Forex Trend Analyzer");
        bb_frame.setTitle("Forex Trend Analyzer");

        sma_frame.pack();
        sd_frame.pack();
        ema_frame.pack();
        bb_frame.pack();
        sma_frame.setLocation(
            (screenSize.width - sma_frame.getSize().width) / 2,
            (screenSize.height - sma_frame.getSize().height) / 2);
        sd_frame.setLocation(
            (screenSize.width - sma_frame.getSize().width) / 2,
            (screenSize.height - sma_frame.getSize().height) / 2);
        ema_frame.setLocation(
            (screenSize.width - sma_frame.getSize().width) / 2,
            (screenSize.height - sma_frame.getSize().height) / 2);
        bb_frame.setLocation(
            (screenSize.width - sma_frame.getSize().width) / 2,
            (screenSize.height - sma_frame.getSize().height) / 2);

        // Trend clustering

        /** END OF TREND FRAMES **/

        /** PREDICTION DATA **/

        /** END OF PREDICTION DATA **/
        /** PREDICTION FRAMES **/
        pred = new JPanel();
        pred.setSize(1425, 100);
        pdframe = new JFrame();
        pdframe.setDefaultCloseOperation(HIDE_ON_CLOSE);
        pdframe.getContentPane().add(pred);
        pdframe.setTitle("Forex Trend Analyzer");
        pdframe.pack();
        pdframe.setLocation((screenSize.width - pdframe.getSize().width) / 2,
            (screenSize.height - pdframe.getSize().height) / 2);
        pdframe.setSize(new Dimension(1425, 65));

        eur_jl = new JLabel("EURUSD HOLD");
        jpy_jl = new JLabel("JPYUSD HOLD");
        chf_jl = new JLabel("CHFUSD HOLD");
        cad_jl = new JLabel("CADUSD HOLD");
        aud_jl = new JLabel("AUDUSD HOLD");
        gbp_jl = new JLabel("GBPUSD HOLD");
        nzd_jl = new JLabel("NZDUSD HOLD");

        eur_jl.setOpaque(true);
        jpy_jl.setOpaque(true);
        cad_jl.setOpaque(true);
        chf_jl.setOpaque(true);
        gbp_jl.setOpaque(true);
        nzd_jl.setOpaque(true);
        aud_jl.setOpaque(true);

        eur_jl.setBackground(Color.YELLOW);
        jpy_jl.setBackground(Color.YELLOW);
        cad_jl.setBackground(Color.YELLOW);
        chf_jl.setBackground(Color.YELLOW);
        nzd_jl.setBackground(Color.YELLOW);
        gbp_jl.setBackground(Color.YELLOW);
        aud_jl.setBackground(Color.YELLOW);

        eur_jl.setSize(200, 100);
        jpy_jl.setSize(200, 100);
        cad_jl.setSize(200, 100);
        chf_jl.setSize(200, 100);
        nzd_jl.setSize(200, 100);
        gbp_jl.setSize(200, 100);
        aud_jl.setSize(200, 100);

        Font labelFont = eur_jl.getFont();
        String labelText = eur_jl.getText();

        int stringWidth =
            eur_jl.getFontMetrics(labelFont).stringWidth(labelText);
        int componentWidth = eur_jl.getWidth();
        double widthRatio = (double) componentWidth / (double) stringWidth;
        int newFontSize = (int) (labelFont.getSize() * widthRatio);
        int componentHeight = eur_jl.getHeight();
        int fontSizeToUse = Math.min(newFontSize, componentHeight);
        eur_jl
            .setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        jpy_jl
            .setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        cad_jl
            .setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        chf_jl
            .setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        nzd_jl
            .setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        gbp_jl
            .setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        aud_jl
            .setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));

        pred.add(eur_jl);
        pred.add(jpy_jl);
        pred.add(cad_jl);
        pred.add(chf_jl);
        pred.add(nzd_jl);
        pred.add(gbp_jl);
        pred.add(aud_jl);

        /** END OF PREDICTION FRAMES **/

        /****************
         * RUN TIME BELOW HERE
         ****************/

        while (mkt.running())
            try {

                Thread.sleep(1); // run it fast
                mkt.tick(); // increments by 1 second
                Dictionary<String, Dictionary<String, Dictionary<String, String>>> cur =
                    mkt.getEx().getCurrent();
                if (cur != null && cur.get("PRICE") != null
                    && cur.get("PRICE").get("CADUSD") != null) {
                    addPoint(cadusd_price, Double.parseDouble(cur.get("PRICE")
                        .get("CADUSD").get("AVERAGE")));
                    addPointSMA(cadusd_sma5, simple_moving_average_5_cad,
                        Double.parseDouble(cur.get("PRICE").get("CADUSD").get(
                            "AVERAGE")), 5);
                    addPointSMA(cadusd_sma10, simple_moving_average_10_cad,
                        Double.parseDouble(cur.get("PRICE").get("CADUSD").get(
                            "AVERAGE")), 10);
                    addPointSD(cadusd_sd10, standard_deviation_10_cad, Double
                        .parseDouble(cur.get("PRICE").get("CADUSD").get(
                            "AVERAGE")), 10);
                    addPointBB(cadusd_bb10_h, cadusd_bb10_l,
                        standard_deviation_10_cad);
                    cad.add(Double.parseDouble(cur.get("PRICE").get("CADUSD")
                        .get("AVERAGE")));

                }
                if (cur != null && cur.get("PRICE") != null
                    && cur.get("PRICE").get("EURUSD") != null) {
                    addPoint(eurusd_price, Double.parseDouble(cur.get("PRICE")
                        .get("EURUSD").get("AVERAGE")));
                    addPointSMA(eurusd_sma5, simple_moving_average_5_eur,
                        Double.parseDouble(cur.get("PRICE").get("EURUSD").get(
                            "AVERAGE")), 5);
                    addPointSMA(eurusd_sma10, simple_moving_average_10_eur,
                        Double.parseDouble(cur.get("PRICE").get("EURUSD").get(
                            "AVERAGE")), 10);
                    addPointSD(eurusd_sd10, standard_deviation_10_eur, Double
                        .parseDouble(cur.get("PRICE").get("EURUSD").get(
                            "AVERAGE")), 10);
                    eur.add(Double.parseDouble(cur.get("PRICE").get("EURUSD")
                        .get("AVERAGE")));

                }
                if (cur != null && cur.get("PRICE") != null
                    && cur.get("PRICE").get("JPYUSD") != null) {
                    addPoint(jpyusd_price, Double.parseDouble(cur.get("PRICE")
                        .get("JPYUSD").get("AVERAGE")));
                    addPointSMA(jpyusd_sma5, simple_moving_average_5_jpy,
                        Double.parseDouble(cur.get("PRICE").get("JPYUSD").get(
                            "AVERAGE")), 5);
                    addPointSMA(jpyusd_sma10, simple_moving_average_10_jpy,
                        Double.parseDouble(cur.get("PRICE").get("JPYUSD").get(
                            "AVERAGE")), 10);
                    addPointSD(jpyusd_sd10, standard_deviation_10_jpy, Double
                        .parseDouble(cur.get("PRICE").get("JPYUSD").get(
                            "AVERAGE")), 10);
                    jpy.add(Double.parseDouble(cur.get("PRICE").get("JPYUSD")
                        .get("AVERAGE")));

                }
                if (cur != null && cur.get("PRICE") != null
                    && cur.get("PRICE").get("NZDUSD") != null) {
                    addPoint(nzdusd_price, Double.parseDouble(cur.get("PRICE")
                        .get("NZDUSD").get("AVERAGE")));
                    addPointSMA(nzdusd_sma5, simple_moving_average_5_nzd,
                        Double.parseDouble(cur.get("PRICE").get("NZDUSD").get(
                            "AVERAGE")), 5);
                    addPointSMA(nzdusd_sma10, simple_moving_average_10_nzd,
                        Double.parseDouble(cur.get("PRICE").get("NZDUSD").get(
                            "AVERAGE")), 10);
                    addPointSD(nzdusd_sd10, standard_deviation_10_nzd, Double
                        .parseDouble(cur.get("PRICE").get("NZDUSD").get(
                            "AVERAGE")), 10);
                    nzd.add(Double.parseDouble(cur.get("PRICE").get("NZDUSD")
                        .get("AVERAGE")));
                }
                if (cur != null && cur.get("PRICE") != null
                    && cur.get("PRICE").get("GBPUSD") != null) {
                    addPoint(gbpusd_price, Double.parseDouble(cur.get("PRICE")
                        .get("GBPUSD").get("AVERAGE")));
                    addPointSMA(gbpusd_sma5, simple_moving_average_5_gbp,
                        Double.parseDouble(cur.get("PRICE").get("GBPUSD").get(
                            "AVERAGE")), 5);
                    addPointSMA(gbpusd_sma10, simple_moving_average_10_gbp,
                        Double.parseDouble(cur.get("PRICE").get("GBPUSD").get(
                            "AVERAGE")), 10);
                    addPointSD(gbpusd_sd10, standard_deviation_10_gbp, Double
                        .parseDouble(cur.get("PRICE").get("GBPUSD").get(
                            "AVERAGE")), 10);
                    gbp.add(Double.parseDouble(cur.get("PRICE").get("GBPUSD")
                        .get("AVERAGE")));

                }
                if (cur != null && cur.get("PRICE") != null
                    && cur.get("PRICE").get("CHFUSD") != null) {
                    addPoint(chfusd_price, Double.parseDouble(cur.get("PRICE")
                        .get("CHFUSD").get("AVERAGE")));
                    addPointSMA(chfusd_sma5, simple_moving_average_5_chf,
                        Double.parseDouble(cur.get("PRICE").get("CHFUSD").get(
                            "AVERAGE")), 5);
                    addPointSMA(chfusd_sma10, simple_moving_average_10_chf,
                        Double.parseDouble(cur.get("PRICE").get("CHFUSD").get(
                            "AVERAGE")), 10);
                    addPointSD(chfusd_sd10, standard_deviation_10_chf, Double
                        .parseDouble(cur.get("PRICE").get("CHFUSD").get(
                            "AVERAGE")), 10);
                    chf.add(Double.parseDouble(cur.get("PRICE").get("CHFUSD")
                        .get("AVERAGE")));

                }
                if (cur != null && cur.get("PRICE") != null
                    && cur.get("PRICE").get("AUDUSD") != null) {
                    addPoint(audusd_price, Double.parseDouble(cur.get("PRICE")
                        .get("AUDUSD").get("AVERAGE")));
                    addPointSMA(audusd_sma5, simple_moving_average_5_aud,
                        Double.parseDouble(cur.get("PRICE").get("AUDUSD").get(
                            "AVERAGE")), 5);
                    addPointSMA(audusd_sma10, simple_moving_average_10_aud,
                        Double.parseDouble(cur.get("PRICE").get("AUDUSD").get(
                            "AVERAGE")), 10);
                    addPointSD(audusd_sd10, standard_deviation_10_aud, Double
                        .parseDouble(cur.get("PRICE").get("AUDUSD").get(
                            "AVERAGE")), 10);
                    aud.add(Double.parseDouble(cur.get("PRICE").get("AUDUSD")
                        .get("AVERAGE")));

                }

                if (cad.size() > TradeRecommender.STATIC_RANGE * 2) {
                    BuyDecision buyDecision =
                        TradeRecommender.makeTradeDecision(cad.subList(0,
                            TradeRecommender.STATIC_RANGE), cad.subList(
                            TradeRecommender.STATIC_RANGE, cad.size()));
                    // System.out.println("Buy decision: " + buyDecision);
                    if (buyDecision != null)
                        if (buyDecision.toString().equalsIgnoreCase("BUY")) {
                            cad_jl.setText("CADUSD BUY");
                            cad_jl.setBackground(Color.GREEN);
                        } else if (buyDecision.toString().equalsIgnoreCase(
                            "SELL")) {
                            cad_jl.setText("CADUSD SELL");
                            cad_jl.setBackground(Color.RED);
                        } else {
                            cad_jl.setText("CADUSD HOLD");
                            cad_jl.setBackground(Color.YELLOW);
                        }
                    long decisionTicks =
                        TradeRecommender.decisionTicks(cad.subList(0,
                            TradeRecommender.STATIC_RANGE), cad.subList(
                            TradeRecommender.STATIC_RANGE, cad.size()));
                    // System.out.println("Trade duration: " + decisionTicks);
                }

                if (eur.size() > TradeRecommender.STATIC_RANGE * 2) {
                    BuyDecision buyDecision =
                        TradeRecommender.makeTradeDecision(eur.subList(0,
                            TradeRecommender.STATIC_RANGE), eur.subList(
                            TradeRecommender.STATIC_RANGE, eur.size()));
                    // System.out.println("Buy decision: " + buyDecision);
                    if (buyDecision != null)
                        if (buyDecision.toString().equalsIgnoreCase("BUY")) {
                            eur_jl.setText("EURUSD BUY");
                            eur_jl.setBackground(Color.GREEN);
                        } else if (buyDecision.toString().equalsIgnoreCase(
                            "SELL")) {
                            eur_jl.setText("EURUSD SELL");
                            eur_jl.setBackground(Color.RED);
                        } else {
                            eur_jl.setText("EURUSD HOLD");
                            eur_jl.setBackground(Color.YELLOW);
                        }
                    long decisionTicks =
                        TradeRecommender.decisionTicks(eur.subList(0,
                            TradeRecommender.STATIC_RANGE), eur.subList(
                            TradeRecommender.STATIC_RANGE, eur.size()));
                    // System.out.println("Trade duration: " + decisionTicks);
                }

                if (chf.size() > TradeRecommender.STATIC_RANGE * 2) {
                    BuyDecision buyDecision =
                        TradeRecommender.makeTradeDecision(chf.subList(0,
                            TradeRecommender.STATIC_RANGE), chf.subList(
                            TradeRecommender.STATIC_RANGE, chf.size()));
                    // System.out.println("Buy decision: " + buyDecision);
                    if (buyDecision != null)
                        if (buyDecision.toString().equalsIgnoreCase("BUY")) {
                            chf_jl.setText("CHFUSD BUY");
                            chf_jl.setBackground(Color.GREEN);
                        } else if (buyDecision.toString().equalsIgnoreCase(
                            "SELL")) {
                            chf_jl.setText("CHFUSD SELL");
                            chf_jl.setBackground(Color.RED);
                        } else {
                            chf_jl.setText("CHFUSD HOLD");
                            chf_jl.setBackground(Color.YELLOW);
                        }
                    long decisionTicks =
                        TradeRecommender.decisionTicks(chf.subList(0,
                            TradeRecommender.STATIC_RANGE), chf.subList(
                            TradeRecommender.STATIC_RANGE, chf.size()));
                    // System.out.println("Trade duration: " + decisionTicks);
                }

                if (gbp.size() > TradeRecommender.STATIC_RANGE * 2) {
                    BuyDecision buyDecision =
                        TradeRecommender.makeTradeDecision(gbp.subList(0,
                            TradeRecommender.STATIC_RANGE), gbp.subList(
                            TradeRecommender.STATIC_RANGE, gbp.size()));
                    // System.out.println("Buy decision: " + buyDecision);
                    if (buyDecision != null)
                        if (buyDecision.toString().equalsIgnoreCase("BUY")) {
                            gbp_jl.setText("GBPUSD BUY");
                            gbp_jl.setBackground(Color.GREEN);
                        } else if (buyDecision.toString().equalsIgnoreCase(
                            "SELL")) {
                            gbp_jl.setText("GBPUSD SELL");
                            gbp_jl.setBackground(Color.RED);
                        } else {
                            gbp_jl.setText("GBPUSD HOLD");
                            gbp_jl.setBackground(Color.YELLOW);
                        }
                    long decisionTicks =
                        TradeRecommender.decisionTicks(gbp.subList(0,
                            TradeRecommender.STATIC_RANGE), gbp.subList(
                            TradeRecommender.STATIC_RANGE, gbp.size()));
                    // System.out.println("Trade duration: " + decisionTicks);
                }

                if (jpy.size() > TradeRecommender.STATIC_RANGE * 2) {
                    BuyDecision buyDecision =
                        TradeRecommender.makeTradeDecision(jpy.subList(0,
                            TradeRecommender.STATIC_RANGE), jpy.subList(
                            TradeRecommender.STATIC_RANGE, jpy.size()));
                    // System.out.println("Buy decision: " + buyDecision);
                    if (buyDecision != null)
                        if (buyDecision.toString().equalsIgnoreCase("BUY")) {
                            jpy_jl.setText("JPYUSD BUY");
                            jpy_jl.setBackground(Color.GREEN);
                        } else if (buyDecision.toString().equalsIgnoreCase(
                            "SELL")) {
                            jpy_jl.setText("JPYUSD SELL");
                            jpy_jl.setBackground(Color.RED);
                        } else {
                            jpy_jl.setText("JPYUSD HOLD");
                            jpy_jl.setBackground(Color.YELLOW);
                        }
                    long decisionTicks =
                        TradeRecommender.decisionTicks(jpy.subList(0,
                            TradeRecommender.STATIC_RANGE), jpy.subList(
                            TradeRecommender.STATIC_RANGE, jpy.size()));
                    // System.out.println("Trade duration: " + decisionTicks);
                }

                if (aud.size() > TradeRecommender.STATIC_RANGE * 2) {
                    BuyDecision buyDecision =
                        TradeRecommender.makeTradeDecision(aud.subList(0,
                            TradeRecommender.STATIC_RANGE), aud.subList(
                            TradeRecommender.STATIC_RANGE, aud.size()));
                    // System.out.println("Buy decision: " + buyDecision);
                    if (buyDecision != null)
                        if (buyDecision.toString().equalsIgnoreCase("BUY")) {
                            aud_jl.setText("AUDUSD BUY");
                            aud_jl.setBackground(Color.GREEN);
                        } else if (buyDecision.toString().equalsIgnoreCase(
                            "SELL")) {
                            aud_jl.setText("AUDUSD SELL");
                            aud_jl.setBackground(Color.RED);
                        } else {
                            aud_jl.setText("AUDUSD HOLD");
                            aud_jl.setBackground(Color.YELLOW);
                        }
                    long decisionTicks =
                        TradeRecommender.decisionTicks(aud.subList(0,
                            TradeRecommender.STATIC_RANGE), aud.subList(
                            TradeRecommender.STATIC_RANGE, aud.size()));
                    // System.out.println("Trade duration: " + decisionTicks);
                }

                if (nzd.size() > TradeRecommender.STATIC_RANGE * 2) {
                    BuyDecision buyDecision =
                        TradeRecommender.makeTradeDecision(nzd.subList(0,
                            TradeRecommender.STATIC_RANGE), nzd.subList(
                            TradeRecommender.STATIC_RANGE, nzd.size()));
                    // System.out.println("Buy decision: " + buyDecision);
                    if (buyDecision != null)
                        if (buyDecision.toString().equalsIgnoreCase("BUY")) {
                            nzd_jl.setText("NZDUSD BUY");
                            nzd_jl.setBackground(Color.GREEN);
                        } else if (buyDecision.toString().equalsIgnoreCase(
                            "SELL")) {
                            nzd_jl.setText("NZDUSD SELL");
                            nzd_jl.setBackground(Color.RED);
                        } else {
                            nzd_jl.setText("NZDUSD HOLD");
                            nzd_jl.setBackground(Color.YELLOW);
                        }
                    long decisionTicks =
                        TradeRecommender.decisionTicks(nzd.subList(0,
                            TradeRecommender.STATIC_RANGE), nzd.subList(
                            TradeRecommender.STATIC_RANGE, nzd.size()));
                    // System.out.println("Trade duration: " + decisionTicks);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
    }

    /**
     * Creates a sample chart.
     *
     * @param dataset the dataset.
     * @return A sample chart.
     */
    public static JFreeChart createChart(final XYDataset dataset, String name,
        String x, String y)
    {
        final JFreeChart result =
            ChartFactory.createTimeSeriesChart(name, x, y, dataset, true, true,
                false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0); // 30 seconds
        axis = plot.getRangeAxis();
        axis.setAutoRangeMinimumSize(.0000005);
        return result;
    }

    /**
     * @param e the action event.
     */
    public static void addPoint(TimeSeries series, double v)
    {
        series.add(new Millisecond(), v);
    }

    /**
     * @param e the action event.
     */
    public static void addPointSMA(TimeSeries series, ArrayList<Double> arr,
        double v, int n)
    {
        if (arr.size() > n)
            arr.remove(arr.size() - 1); // size is 4
        arr.add(v);
        if (arr.size() >= n)
            series.add(new Millisecond(), sma(arr));
    }

    public static void addPointSD(TimeSeries series, ArrayList<Double> arr,
        double v, int n)
    {
        if (arr.size() > n)
            arr.remove(arr.size() - 1); // size is 4
        arr.add(v);
        if (arr.size() >= n)
            series.add(new Millisecond(), sd(arr));
    }

    public static void addPointBB(TimeSeries series_h, TimeSeries series_l,
        ArrayList<Double> arr)
    {
        double sd = sd(arr);
        series_h.add(new Millisecond(), sma(arr) + 2 * sd);
        series_l.add(new Millisecond(), sma(arr) - 2 * sd);
    }

    public static Double sma(List<Double> arr)
    {
        double total = 0.0;
        for (Double d : arr)
            total += d;
        return total / arr.size();
    }

    public static Double sd(List<Double> arr)
    {
        double var = simple_variance(arr);
        double sign = Math.signum(var);
        return sign * Math.sqrt(Math.abs(var));
    }

    public static double simple_variance(List<Double> arr)
    {
        double sum = 0.0;
        double ave = 0.0;
        for (int i = 0; i < arr.size(); i++)
            sum += arr.get(i);
        if (arr.size() != 0)
            ave = sum / arr.size();
        double sum2 = 0.0;
        double ave2 = 0.0;
        for (int i = 0; i < arr.size(); i++)
            sum2 += Math.pow(arr.get(i) - ave, 2);
        if (arr.size() != 0)
            ave2 = sum2 / arr.size();
        return ave2;
    }

    public static List<Vector> getPoints(List<ArrayList<Double>> raw)
    {
        List<Vector> points = new ArrayList<Vector>();
        for (int i = 0; i < raw.size(); i++) {
            Double[] fr = raw.get(i).toArray(new Double[raw.get(i).size()]);
            Vector vec = new RandomAccessSparseVector(fr.length);
            double[] fr2 = new double[fr.length];
            for (int j = 0; j < fr.length; j++)
                fr2[j] = fr[j];
            vec.assign(fr2);
            points.add(vec);
        }
        return points;
    }

    /* listener for menu bar */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.out.println(e.getSource());
        if (e.getSource() == exitMenuItem) {
            dispose();
            System.exit(0);
        } else if (e.getSource() == prices_mi)
            pframe.setVisible(true);
        else if (e.getSource() == sma_mi)
            sma_frame.setVisible(true);
        else if (e.getSource() == sd_mi)
            sd_frame.setVisible(true);
        else if (e.getSource() == ema_mi)
            ema_frame.setVisible(true);
        else if (e.getSource() == bb_mi)
            bb_frame.setVisible(true);
        else if (e.getSource() == predictions_mi)
            pdframe.setVisible(true);
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main(final String[] args)
    {

        ForexTrendAnalyzer d = new ForexTrendAnalyzer();
        d.setVisible(true);
        d.run();

    }

}
