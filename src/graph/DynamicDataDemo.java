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
import java.io.IOException;
import java.util.Dictionary;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
public class DynamicDataDemo {

    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return A sample chart.
     */
    public static JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            "Price", 
            "Time", 
            "Value",
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
    
    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Handles a click on the button by adding new (random) data.
     *
     * @param e  the action event.
     */
    public static void addPoint(TimeSeries series, double v) {
        //final double value = v;
        final Millisecond now = new Millisecond();
        System.out.println("Now = " + now.toString() + "\tPrice: " + v);
        series.add(new Millisecond(), v);
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

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
        TimeSeries cadusd = new TimeSeries("CADUSD", Millisecond.class);
        TimeSeriesCollection cadds = new TimeSeriesCollection(cadusd);
        JFreeChart cadc = createChart(cadds);
        ChartPanel cadcp = new ChartPanel(cadc);
        JPanel cadcon = new JPanel(new BorderLayout());
        cadcon.add(cadcp);
        cadcp.setPreferredSize(new java.awt.Dimension(500, 270));
        //setContentPane(content);
        
        /* EURUSD */
        TimeSeries eurusd = new TimeSeries("EURUSD", Millisecond.class);
        TimeSeriesCollection eurds = new TimeSeriesCollection(eurusd);
        JFreeChart eurc = createChart(eurds);
        ChartPanel eurcp = new ChartPanel(eurc);
        JPanel eurcon = new JPanel(new BorderLayout());
        eurcon.add(eurcp);
        eurcp.setPreferredSize(new java.awt.Dimension(500, 270));
        //setContentPane(content);
        /** END OF PRICE DATA **/
        
        
        /** PRICE FRAMES **/
        JTabbedPane prices = new JTabbedPane();
        prices.addTab("CADUSD", cadcon);
        prices.addTab("EURUSD", eurcon);
        JFrame pframe = new JFrame();
        pframe.getContentPane().add (prices);
        pframe.setTitle ("Forex Trend Analyzer");
        pframe.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        pframe.setLocation (
          (screenSize.width - pframe.getSize().width) / 2,
          (screenSize.height - pframe.getSize().height) / 2);
    	pframe.setVisible(true);
        /** END OF PRICE FRAMES **/
    	
    	
    	/** TRENDS FRAMES **/
        JTabbedPane trends = new JTabbedPane();
        JFrame tframe = new JFrame();
        tframe.getContentPane().add (trends);
        tframe.setTitle ("Forex Trend Analyzer");
        tframe.pack();
        tframe.setLocation (
          (screenSize.width - tframe.getSize().width) / 2,
          (screenSize.height - tframe.getSize().height) / 2);
    	tframe.setVisible(true);
    	/** END OF TREND FRAMES **/
    	
    	
    	
    	
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
        			addPoint(cadusd, Double.parseDouble(cur.get("PRICE").get("CADUSD").get("AVERAGE")));
        		}
        		if(cur!=null && cur.get("PRICE")!=null&&cur.get("PRICE").get("EURUSD")!=null) {
        			addPoint(eurusd, Double.parseDouble(cur.get("PRICE").get("EURUSD").get("AVERAGE")));
        		}
        	} catch(Exception e) {
        		continue;
        	}
        }
        
        
    }

}



