package graph;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------
 * DynamicDataDemo.java
 * --------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited).
 * Contributor(s):   -;
 *
 * $Id: DynamicDataDemo.java,v 1.12 2004/05/07 16:09:03 mungady Exp $
 *
 * Changes
 * -------
 * 28-Mar-2002 : Version 1 (DG);
 *
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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

/**
 * A demonstration application showing a time series chart where you can dynamically add
 * (random) data by clicking on a button.
 *
 */
public class ForexTrendAnalyzer extends JFrame implements ActionListener
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JMenu display, trends_m, analytics;
	private JMenuItem prices_mi, sma_mi, sd_mi, bb_mi, ema_mi,
					predictions_mi, exitMenuItem;
	private JMenu exitMenu;	
	private JFrame sma_frame, sd_frame, ema_frame, bb_frame,
					pframe, pdframe;
    private JTabbedPane prices, sma, sd, bb, ema, pred;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
    /** Price **/
	private TimeSeries cadusd_price, eurusd_price;
	private TimeSeriesCollection cadds_price, eurds_price;
    private JFreeChart cadc_price, eurc_price;
    private ChartPanel cadcp_price, eurcp_price;
    private JPanel cadcon_price, eurcon_price;
    /** End Price **/
    
    /** Trend **/
    private ArrayList<Double> simple_moving_average_5_cad, simple_moving_average_5_eur,
    					simple_moving_average_10_cad, simple_moving_average_10_eur,
    					standard_deviation_10_cad, standard_deviation_10_eur;
	private TimeSeries cadusd_sma5, eurusd_sma5, cadusd_sma10, eurusd_sma10,
						cadusd_sd10, eurusd_sd10;
	private TimeSeriesCollection cadds_sma5, eurds_sma5, cadds_sma10, eurds_sma10,
									cadds_sd10, eurds_sd10;
    private JFreeChart cadc_sma5, eurc_sma5, cadc_sma10, eurc_sma10,
    					cadc_sd10, eurc_sd10;
    private ChartPanel cadcp_sma5, eurcp_sma5, cadcp_sma10, eurcp_sma10,
    					cadcp_sd10, eurcp_sd10;
    private JPanel cadcon_sma5, eurcon_sma5, cadcon_sma10, eurcon_sma10,
    				cadcon_sd10, eurcon_sd10;
    /** End Trend **/
    
    
	public ForexTrendAnalyzer()
	{
		super("Forex Analyzer");
		
		display = new JMenu("Display");
		exitMenu = new JMenu("Exit");
		trends_m = new JMenu("Trends");
		analytics = new JMenu("Analytics");

		
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
		
		for(int i = 0; i<menuBar.getMenuCount();i++)
		{
			JMenu currentMenu1 = menuBar.getMenu(i);
			for(int j = 0; j<currentMenu1.getItemCount();j++)
			{
				JMenuItem currentItem = currentMenu1.getItem(j);
				System.out.println(currentItem.getClass());
				if(!currentItem.getClass().toString().contains("javax.swing.JMenuItem")){
					for(int k = 0; k< ((JMenu)currentItem).getItemCount();k++){
						JMenuItem currentItem2 = ((JMenu) currentItem).getItem(k);
						System.out.println(currentItem2);
						if(currentItem2 != null)
						{
							currentItem2.addActionListener(this);
						}						
					}
				}
				if(currentItem != null)
				{
					currentItem.addActionListener(this);
				}
			}
		}
		
		setJMenuBar(menuBar);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
        setLocation (
            (screenSize.width - getSize().width) / 2,
            (screenSize.height - getSize().height) / 2);
	}
	
	
	/**
	 * 
	 */
	public void run(){

		Market mkt = new Market("/Users/theocean154/Documents/School_files/College/Programs/eclipse/bda/src/config/market.config",
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
        cadusd_price = new TimeSeries("CADUSD", Millisecond.class);
        cadds_price = new TimeSeriesCollection(cadusd_price);
        cadc_price = createChart(cadds_price, "CADUSD", "TIME", "PRICE");
        cadcp_price = new ChartPanel(cadc_price);
        cadcon_price = new JPanel(new BorderLayout());
        cadcon_price.add(cadcp_price);
        cadcp_price.setPreferredSize(new java.awt.Dimension(500, 270));
        
        /* EURUSD */
        eurusd_price = new TimeSeries("EURUSD", Millisecond.class);
        eurds_price = new TimeSeriesCollection(eurusd_price);
        eurc_price = createChart(eurds_price, "EURUSD", "TIME", "PRICE");
        eurcp_price = new ChartPanel(eurc_price);
        eurcon_price = new JPanel(new BorderLayout());
        eurcon_price.add(eurcp_price);
        eurcp_price.setPreferredSize(new java.awt.Dimension(500, 270));
        /** END OF PRICE DATA **/
        
        /** PRICE FRAMES **/
        prices = new JTabbedPane();
        prices.addTab("CADUSD", cadcon_price);
        prices.addTab("EURUSD", eurcon_price);
        pframe = new JFrame();
        pframe.setDefaultCloseOperation(HIDE_ON_CLOSE);
        pframe.getContentPane().add (prices);
        pframe.setTitle ("Forex Trend Analyzer");
        pframe.pack();
        pframe.setLocation (
          (screenSize.width - pframe.getSize().width) / 2,
          (screenSize.height - pframe.getSize().height) / 2);
        /** END OF PRICE FRAMES **/
    	
    	
    	
    	/** TREND DATA **/
    	simple_moving_average_5_cad = new ArrayList<Double>();
        cadusd_sma5 = new TimeSeries("CADUSD", Millisecond.class);
        cadds_sma5 = new TimeSeriesCollection(cadusd_sma5);
        cadc_sma5 = createChart(cadds_sma5, "CADUSD-SMA5", "TIME", "5 Tick Simple Moving Average");
        cadcp_sma5 = new ChartPanel(cadc_sma5);
        cadcon_sma5 = new JPanel(new BorderLayout());
        cadcon_sma5.add(cadcp_sma5);
        cadcp_sma5.setPreferredSize(new java.awt.Dimension(500, 270));
        
    	simple_moving_average_5_eur = new ArrayList<Double>();
        eurusd_sma5 = new TimeSeries("EURUSD", Millisecond.class);
        eurds_sma5 = new TimeSeriesCollection(eurusd_sma5);
        eurc_sma5 = createChart(eurds_sma5, "EURUSD-SMA5", "TIME", "5 Tick Simple Moving Average");
        eurcp_sma5 = new ChartPanel(eurc_sma5);
        eurcon_sma5 = new JPanel(new BorderLayout());
        eurcon_sma5.add(eurcp_sma5);
        eurcp_sma5.setPreferredSize(new java.awt.Dimension(500, 270));
        
    	simple_moving_average_10_cad = new ArrayList<Double>();
        cadusd_sma10 = new TimeSeries("CADUSD", Millisecond.class);
        cadds_sma10 = new TimeSeriesCollection(cadusd_sma10);
        cadc_sma10 = createChart(cadds_sma10, "CADUSD-SMA10", "TIME", "10 Tick Simple Moving Average");
        cadcp_sma10 = new ChartPanel(cadc_sma5);
        cadcon_sma10 = new JPanel(new BorderLayout());
        cadcon_sma10.add(cadcp_sma10);
        cadcp_sma10.setPreferredSize(new java.awt.Dimension(500, 270));
        
    	simple_moving_average_10_eur = new ArrayList<Double>();
        eurusd_sma10 = new TimeSeries("EURUSD", Millisecond.class);
        eurds_sma10 = new TimeSeriesCollection(eurusd_sma10);
        eurc_sma10 = createChart(eurds_sma10, "EURUSD-SMA10", "TIME", "10 Tick Simple Moving Average");
        eurcp_sma10 = new ChartPanel(eurc_sma10);
        eurcon_sma10 = new JPanel(new BorderLayout());
        eurcon_sma10.add(eurcp_sma10);
        eurcp_sma10.setPreferredSize(new java.awt.Dimension(500, 270));
        
    	standard_deviation_10_cad = new ArrayList<Double>();
        cadusd_sd10 = new TimeSeries("CADUSD", Millisecond.class);
        cadds_sd10 = new TimeSeriesCollection(cadusd_sd10);
        cadc_sd10 = createChart(cadds_sd10, "CADUSD-SD10", "TIME", "10 Tick Standard Deviation");
        cadcp_sd10 = new ChartPanel(cadc_sd10);
        cadcon_sd10 = new JPanel(new BorderLayout());
        cadcon_sd10.add(cadcp_sd10);
        cadcp_sd10.setPreferredSize(new java.awt.Dimension(500, 270));
        
    	standard_deviation_10_eur = new ArrayList<Double>();
        eurusd_sd10 = new TimeSeries("EURUSD", Millisecond.class);
        eurds_sd10 = new TimeSeriesCollection(eurusd_sd10);
        eurc_sd10 = createChart(eurds_sd10, "EURUSD-SD10", "TIME", "10 Tick Standard Deviation");
        eurcp_sd10 = new ChartPanel(eurc_sd10);
        eurcon_sd10 = new JPanel(new BorderLayout());
        eurcon_sd10.add(eurcp_sd10);
        eurcp_sd10.setPreferredSize(new java.awt.Dimension(500, 270));
        
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
        sma.addTab("CADUSD-SMA10", cadcon_sma10);
        sma.addTab("EURUSD-SMA10", eurcon_sma10);
        sd.addTab("CADUSD-SD10", cadcon_sd10);
        sma_frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        sd_frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        ema_frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        bb_frame.setDefaultCloseOperation(HIDE_ON_CLOSE);

        sma_frame.getContentPane().add (sma);
        sd_frame.getContentPane().add (sd);
        sma_frame.setTitle ("Forex Trend Analyzer");
        sd_frame.setTitle ("Forex Trend Analyzer");
        ema_frame.setTitle ("Forex Trend Analyzer");
        bb_frame.setTitle ("Forex Trend Analyzer");

        sma_frame.pack();
        sd_frame.pack();
        ema_frame.pack();
        bb_frame.pack();
        sma_frame.setLocation (
          (screenSize.width - sma_frame.getSize().width) / 2,
          (screenSize.height - sma_frame.getSize().height) / 2);
        sd_frame.setLocation (
                (screenSize.width - sma_frame.getSize().width) / 2,
                (screenSize.height - sma_frame.getSize().height) / 2);
        ema_frame.setLocation (
                (screenSize.width - sma_frame.getSize().width) / 2,
                (screenSize.height - sma_frame.getSize().height) / 2);
        bb_frame.setLocation (
                (screenSize.width - sma_frame.getSize().width) / 2,
                (screenSize.height - sma_frame.getSize().height) / 2);
    	/** END OF TREND FRAMES **/
    	
    	
    	/** PREDICTION DATA **/
    	
    	/** END OF PREDICTION DATA **/
    	/** PREDICTION FRAMES **/
        pred = new JTabbedPane();
        pdframe = new JFrame();
        pdframe.setDefaultCloseOperation(HIDE_ON_CLOSE);
        pdframe.getContentPane().add (pred);
        pdframe.setTitle ("Forex Trend Analyzer");
        pdframe.pack();
        pdframe.setLocation (
          (screenSize.width - pdframe.getSize().width) / 2,
          (screenSize.height - pdframe.getSize().height) / 2);
    	/** END OF PREDICTION FRAMES **/
    	
    	
    	
    	/****************
    	 * 
    	 * RUN TIME BELOW HERE
    	 * 
    	 ****************/
        
        while(true) {
        	try { 
        		Thread.sleep(100); //run it 10x fast
        		mkt.tick(); //increments by 1 second
        		Dictionary<String, Dictionary<String, Dictionary<String, String>>> cur = mkt.getEx().getCurrent();
        		if(cur!=null && cur.get("PRICE")!=null&&cur.get("PRICE").get("CADUSD")!=null) {
        			addPoint(cadusd_price, Double.parseDouble(cur.get("PRICE").get("CADUSD").get("AVERAGE")));
        			addPointSMA(cadusd_sma5, simple_moving_average_5_cad, Double.parseDouble(cur.get("PRICE").get("CADUSD").get("AVERAGE")), 5);
        			addPointSMA(cadusd_sma10, simple_moving_average_10_cad, Double.parseDouble(cur.get("PRICE").get("CADUSD").get("AVERAGE")), 10);
        			addPointSD(cadusd_sd10, standard_deviation_10_cad, Double.parseDouble(cur.get("PRICE").get("CADUSD").get("AVERAGE")), 10);
        		}
        		if(cur!=null && cur.get("PRICE")!=null&&cur.get("PRICE").get("EURUSD")!=null) {
        			addPoint(eurusd_price, Double.parseDouble(cur.get("PRICE").get("EURUSD").get("AVERAGE")));
        			addPointSMA(eurusd_sma5, simple_moving_average_5_eur, Double.parseDouble(cur.get("PRICE").get("EURUSD").get("AVERAGE")), 5);
        			addPointSMA(eurusd_sma10, simple_moving_average_10_eur, Double.parseDouble(cur.get("PRICE").get("EURUSD").get("AVERAGE")), 10);
        			addPointSD(eurusd_sd10, standard_deviation_10_eur, Double.parseDouble(cur.get("PRICE").get("EURUSD").get("AVERAGE")), 10);
        		}
        	} catch(Exception e) {
        		e.printStackTrace();
        		System.exit(1);
        	}
        }
	}
	
	
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return A sample chart.
     */
    public static JFreeChart createChart(final XYDataset dataset, String name, String x, String y) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            name, 
            x, 
            y,
            dataset, 
            true, 
            true, 
            false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(30000.0); //30 seconds
        axis = plot.getRangeAxis();
        axis.setAutoRangeMinimumSize(.00005);
        return result;
    }
        
    /**
     *
     * @param e  the action event.
     */
    public static void addPoint(TimeSeries series, double v) {
        series.add(new Millisecond(), v);
    }
    
    /**
    *
    * @param e  the action event.
    */
   public static void addPointSMA(TimeSeries series, ArrayList<Double> arr, double v, int n) {
       if(arr.size()>n){
    	   arr.remove(arr.size()-1); //size is 4
       }
       arr.add(v);
       if(arr.size()>=n){
    	   series.add(new Millisecond(), sma(arr));
       }
   }

   public static void addPointSD(TimeSeries series, ArrayList<Double> arr, double v, int n) {
       if(arr.size()>n){
    	   arr.remove(arr.size()-1); //size is 4
       }
       arr.add(v);
       if(arr.size()>=n){
    	   series.add(new Millisecond(), sd(arr));
       }
   }
   
   public static Double sma(ArrayList<Double> arr){
	   double total = 0.0;
	   for(Double d: arr){
		   total+=d;
	   }
	   return total/((double)arr.size());
   }
   
   public static Double sd(ArrayList<Double> arr){
	   double var = simple_variance(arr);
	   double sign = Math.signum(var);  
	   return sign*Math.sqrt(Math.abs(var));
   }
   
	public static double simple_variance(ArrayList<Double> arr){
		double sum = 0.0;
		double ave =0.0;
		for(int i=0;i<arr.size();i++){
			sum += arr.get(i);
		}
		if(arr.size()!=0){
			ave = sum/arr.size();
		}
		double sum2 = 0.0;
		double ave2 = 0.0;
		for(int i=0;i<arr.size();i++){
			sum2 += Math.pow(arr.get(i)-ave, 2);
		}
		if(arr.size()!=0){
			ave2 = sum2/arr.size();
		}
		return ave2;
	}
    
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getSource());
		if(e.getSource() == exitMenuItem)
		{
			dispose();
			System.exit(0);
		} else if(e.getSource() == prices_mi){
			pframe.setVisible(true);
		} else if(e.getSource() == sma_mi) {
			sma_frame.setVisible(true);
		} else if(e.getSource() == sd_mi){
			sd_frame.setVisible(true);
		} else if(e.getSource() == ema_mi){
			ema_frame.setVisible(true);
		} else if(e.getSource() == bb_mi){
			bb_frame.setVisible(true);
		} else if(e.getSource() == predictions_mi){
			
		}
	}
    

    
    
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {
		
		ForexTrendAnalyzer d = new ForexTrendAnalyzer();
		d.setVisible(true);
		d.run();
		
    }

}



