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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import data_streamer.Market;

/**
 * A demonstration application showing a time series chart where you can dynamically add
 * (random) data by clicking on a button.
 *
 */
public class ForexTrendAnalyzer extends JFrame implements ActionListener
{
	
	private JMenu display;
		private JMenuItem prices_mi, trends_mi, predictions_mi, exitMenuItem;
	private JMenu exitMenu;	
	private JFrame tframe, pframe, pdframe;
    private JTabbedPane prices, trends, pred;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
    /** Price **/
	private TimeSeries cadusd_price, eurusd_price;
	private TimeSeriesCollection cadds_price, eurds_price;
    private JFreeChart cadc_price, eurc_price;
    private ChartPanel cadcp_price, eurcp_price;
    private JPanel cadcon_price, eurcon_price;
    /** End Price **/
    
    /** Trend **/
    private ArrayList<Double> simple_moving_average_5_cad, simple_moving_average_5_eur;
	private TimeSeries cadusd_sma5, eurusd_sma5;
	private TimeSeriesCollection cadds_sma5, eurds_sma5;
    private JFreeChart cadc_sma5, eurc_sma5;
    private ChartPanel cadcp_sma5, eurcp_sma5;
    private JPanel cadcon_sma5, eurcon_sma5;
    /** End Trend **/
    
    
	public ForexTrendAnalyzer()
	{
		super("Forex Analyzer");
		
		display = new JMenu("Display");
		exitMenu = new JMenu("Exit");
		
		prices_mi = new JMenuItem("Prices");
		trends_mi = new JMenuItem("Trends");
		predictions_mi = new JMenuItem("Predictions");
		
		exitMenuItem = new JMenuItem("Exit");
		
		display.add(prices_mi);
		display.add(trends_mi);
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
    	pframe.setVisible(true);
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
    	/** END OF TREND DATA **/
        
        
    	/** TRENDS FRAMES **/
        trends = new JTabbedPane();
        tframe = new JFrame();
        trends.addTab("CADUSD-SMA5", cadcon_sma5);
        trends.addTab("EURUSD-SMA5", eurcon_sma5);
        tframe.setDefaultCloseOperation(HIDE_ON_CLOSE);
        tframe.getContentPane().add (trends);
        tframe.setTitle ("Forex Trend Analyzer");
        tframe.pack();
        tframe.setLocation (
          (screenSize.width - tframe.getSize().width) / 2,
          (screenSize.height - tframe.getSize().height) / 2);
    	tframe.setVisible(true);
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
    	pdframe.setVisible(true);
    	/** END OF PREDICTION FRAMES **/
    	
    	
    	
    	/****************
    	 * 
    	 * RUN TIME BELOW HERE
    	 * 
    	 ****************/
        
        while(true) {
        	try { 
        		Thread.sleep(500); //run it slightly fast
        		mkt.tick(); //increments by 1 second
        		Dictionary<String, Dictionary<String, Dictionary<String, String>>> cur = mkt.getEx().getCurrent();
        		if(cur!=null && cur.get("PRICE")!=null&&cur.get("PRICE").get("CADUSD")!=null) {
        			addPoint(cadusd_price, Double.parseDouble(cur.get("PRICE").get("CADUSD").get("AVERAGE")));
        			addPointSMA(cadusd_sma5, simple_moving_average_5_cad, Double.parseDouble(cur.get("PRICE").get("CADUSD").get("AVERAGE")), 5);
        		}
        		if(cur!=null && cur.get("PRICE")!=null&&cur.get("PRICE").get("EURUSD")!=null) {
        			addPoint(eurusd_price, Double.parseDouble(cur.get("PRICE").get("EURUSD").get("AVERAGE")));
        			addPointSMA(eurusd_sma5, simple_moving_average_5_eur, Double.parseDouble(cur.get("PRICE").get("EURUSD").get("AVERAGE")), 5);
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

   public static Double sma(ArrayList<Double> arr){
	   double total = 0.0;
	   for(Double d: arr){
		   total+=d;
	   }
	   return total/((double)arr.size());
   }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exitMenuItem)
		{
			dispose();
			System.exit(0);
		} else if(e.getSource() == prices_mi){
			pframe.setVisible(true);
		} else if(e.getSource() == trends_mi) {
			tframe.setVisible(true);
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



