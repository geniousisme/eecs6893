package data_streamer.fx.view;

import graph.ForexTrendAnalyzer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class LineGraphController
{
    private static long                                         UPDATE_INTERVAL  =
                                                                                     250;

    private AtomicBoolean                                       isUpdatingGraphs =
                                                                                     new AtomicBoolean(
                                                                                         false);

    private VBox                                                lineGraphBox;

    private Map<String, Map<String, LineChart<String, Number>>> graphMap         =
                                                                                     new HashMap<>();

    private Map<String, List<Double>>                           avgListMap       =
                                                                                     new HashMap<>();

    private static final long                                   MAX_TICKS        =
                                                                                     50;

    private static final String                                 PRICE            = "PRICE",
        FIVE_TICK_SIM_MOV_AVG = "5 Tick Simple Moving Average",
        TEN_TICK_SIM_MOV_AVG = "10 Tick Simple Moving Average",
        TEN_TICK_STD_DEV = "10 Tick Standard Deviation",
        TEN_TICK_BB = "10 Tick Bollinger Bands";
    public static final List<String>                            chartStrings     =
                                                                                     new ArrayList<>(
                                                                                         Arrays
                                                                                             .asList(new String[] {
        PRICE, TEN_TICK_STD_DEV, FIVE_TICK_SIM_MOV_AVG, TEN_TICK_SIM_MOV_AVG,
        TEN_TICK_BB                                                                          }));

    private SimpleDateFormat                                    tickFormat       =
                                                                                     new SimpleDateFormat(
                                                                                         "HH:mm:ss:SSS");

    private String                                              currentShowingEx;

    private long                                                lastUpdated      =
                                                                                     0;

    public void initialize(VBox lineGraphBox)
    {
        this.lineGraphBox = lineGraphBox;
    }

    public void updateGraphs(Map<String, Double> avgMap)
    {
        if (System.currentTimeMillis() - lastUpdated < UPDATE_INTERVAL)
            return;
        if (isUpdatingGraphs.getAndSet(true))
            return;
        lastUpdated = System.currentTimeMillis();
        // For each update
        for (Map.Entry<String, Double> avgEntry : avgMap.entrySet()) {
            Map<String, LineChart<String, Number>> exCharts =
                graphMap.get(avgEntry.getKey());
            List<Double> exList = avgListMap.get(avgEntry.getKey());
            // First time seen, setup the charts
            if (exCharts == null) {
                // Make the new variables
                exCharts = new HashMap<>();
                exList = new ArrayList<>();
                avgListMap.put(avgEntry.getKey(), exList);
                exList.add(avgEntry.getValue());
                graphMap.put(avgEntry.getKey(), exCharts);
                // Make the new charts
                makeNewCharts(exCharts);
            } else
                exList.add(avgEntry.getValue());

            // Compute the next values in the chart data
            updateChartData(exCharts, exList);

            // Add the first currency
            if (lineGraphBox.getChildren().isEmpty()) {
                Label label = new Label(avgEntry.getKey());
                label.setStyle("    -fx-font-size: 32pt;\r\n"
                    + "    -fx-font-family: \"Segoe UI\";\r\n"
                    + "    -fx-opacity: 1; -fx-weight: bolder;");
                final Map<String, LineChart<String, Number>> exCharts2 =
                    exCharts;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        lineGraphBox.getChildren().add(label);
                        label.setTextAlignment(TextAlignment.CENTER);
                        for (String chartString : chartStrings)
                            lineGraphBox.getChildren().add(
                                exCharts2.get(chartString));
                    }
                });
                currentShowingEx = avgEntry.getKey();
            }
        }
        isUpdatingGraphs.set(false);
    }

    private void updateChartData(
        Map<String, LineChart<String, Number>> exCharts, List<Double> exList)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                for (String chartString : chartStrings) {
                    // Just add the average
                    LineChart<String, Number> chart = exCharts.get(chartString);
                    XYChart.Series<String, Number> series =
                        chart.getData().get(0);
                    ObservableList<Data<String, Number>> data =
                        series.getData();
                    ObservableList<Data<String, Number>> data2 = null;
                    if (chartString.equals(TEN_TICK_BB))
                        data2 = chart.getData().get(1).getData();

                    if (data.size() >= MAX_TICKS) {
                        data.remove(0);
                        if (data2 != null)
                            data2.remove(0);
                    }

                    if (chartString.equals(PRICE))
                        data.add(new XYChart.Data<String, Number>(tickFormat
                            .format(new Date()), exList.get(exList.size() - 1)));
                    else if (chartString.equals(FIVE_TICK_SIM_MOV_AVG)) {
                        List<Double> subList =
                            exList.subList(Math.max(exList.size() - 5, 0),
                                exList.size());
                        data.add(new XYChart.Data<String, Number>(tickFormat
                            .format(new Date()), ForexTrendAnalyzer
                            .sma(subList)));
                    } else if (chartString.equals(TEN_TICK_SIM_MOV_AVG)) {
                        List<Double> subList =
                            exList.subList(Math.max(exList.size() - 10, 0),
                                exList.size());
                        data.add(new XYChart.Data<String, Number>(tickFormat
                            .format(new Date()), ForexTrendAnalyzer
                            .sma(subList)));
                    } else if (chartString.equals(TEN_TICK_STD_DEV)) {
                        List<Double> subList =
                            exList.subList(Math.max(exList.size() - 10, 0),
                                exList.size());
                        data.add(new XYChart.Data<String, Number>(tickFormat
                            .format(new Date()), ForexTrendAnalyzer
                            .sd(new ArrayList<>(subList))));
                    } else if (chartString.equals(TEN_TICK_BB)) {
                        List<Double> subList =
                            exList.subList(Math.max(exList.size() - 10, 0),
                                exList.size());

                        double sd = ForexTrendAnalyzer.sd(subList);
                        double sma = ForexTrendAnalyzer.sma(subList);
                        String format = tickFormat.format(new Date());
                        data.add(new XYChart.Data<String, Number>(format, sma
                            + 2 * sd));
                        data2.add(new XYChart.Data<String, Number>(format, sma
                            - 2 * sd));
                    }
                }
            }
        });
    }

    private void makeNewCharts(Map<String, LineChart<String, Number>> exCharts)
    {
        for (String chartString : chartStrings) {
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Time");
            xAxis.setAnimated(false);
            yAxis.setLabel(chartString);
            yAxis.setAnimated(false);
            yAxis.setForceZeroInRange(false);
            // creating the chart
            final LineChart<String, Number> lineChart =
                new LineChart<String, Number>(xAxis, yAxis);
            // lineChart.setAnimated(false);

            // defining a series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            // populating the series with data
            lineChart.getData().add(series);
            if (chartString.equals(TEN_TICK_BB))
                lineChart.getData().add(new XYChart.Series<>());
            lineChart.setLegendVisible(false);
            exCharts.put(chartString, lineChart);
        }
    }
}
