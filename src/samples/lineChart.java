package samples;

import java.io.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.ChartUtilities; 
public class lineChart {  
      public static void main(String[] args){
         try {
                
                /* Step - 1: Define the data for the line chart  */
                DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
                line_chart_dataset.addValue(15, "schools", "1970");
                line_chart_dataset.addValue(30, "schools", "1980");
                line_chart_dataset.addValue(60, "schools", "1990");
                line_chart_dataset.addValue(120, "schools", "2000");
                line_chart_dataset.addValue(240, "schools", "2010");                
                
                /* Step -2:Define the JFreeChart object to create line chart */
                JFreeChart lineChartObject=ChartFactory.createLineChart("Schools Vs Years","Year","Schools Count",line_chart_dataset,PlotOrientation.VERTICAL,true,true,false);                
                          
                /* Step -3 : Write line chart to a file */               
                 int width=640; /* Width of the image */
                 int height=480; /* Height of the image */                
                 File lineChart=new File("line_Chart_example.png");              
                 ChartUtilities.saveChartAsPNG(lineChart,lineChartObject,width,height); 
         }
         catch (Exception i)
         {
             System.out.println(i.getStackTrace());
         }
     }
 }