package data_streamer.fx.view;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;

public class BarChartAveragesController
{
    private static long                    UPDATE_INTERVAL = 500;

    @FXML
    private BarChart<String, Double>       currencyBarGraph;

    @FXML
    private CategoryAxis                   xAxis;

    private XYChart.Series<String, Double> xSeries;

    private ObservableList<String>         forexMarkets    =
                                                               FXCollections
                                                                   .observableArrayList();
    private Map<String, Integer>           forexSet        = new HashMap<>();

    private long                           lastUpdated     = 0;

    private AtomicBoolean                  isUpdatingBars  = new AtomicBoolean(
                                                               false);

    private Timeline                       tl;

    @FXML
    private void initialize()
    {
        // Assign the month names as categories for the horizontal axis.
        xAxis.setCategories(forexMarkets);
        xSeries = new XYChart.Series<>();
        currencyBarGraph.getData().add(xSeries);
        tl = new Timeline();
    }

    public void updateBars(Map<String, Double> newValues)
    {
        if (System.currentTimeMillis() - lastUpdated < UPDATE_INTERVAL)
            return;
        if (isUpdatingBars.getAndSet(true))
            return;
        lastUpdated = System.currentTimeMillis();
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                for (Map.Entry<String, Double> values : newValues.entrySet())
                    if (!forexSet.containsKey(values.getKey())) {
                        // Add a new bar
                        forexSet.put(values.getKey(), forexMarkets.size());
                        forexMarkets.add(values.getKey());
                        xSeries.getData().add(
                            new XYChart.Data<String, Double>(values.getKey(),
                                values.getValue()));
                    }
                tl.getKeyFrames().add(
                    new KeyFrame(Duration.millis(3 * UPDATE_INTERVAL
                        / (double) 4), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event)
                        {
                            for (XYChart.Data<String, Double> data : xSeries
                                .getData())
                                if (newValues.containsKey(data.getXValue()))
                                    // System.out.println("Old value: "
                                    // + data.getYValue() + ", new value: "
                                    // + newValues.get(data.getXValue()));
                                    data.setYValue(newValues.get(data
                                        .getXValue()));
                        }
                    }));
                tl.play();
            }
        });
        isUpdatingBars.set(false);
    }
}
