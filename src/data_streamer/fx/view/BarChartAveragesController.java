package data_streamer.fx.view;

import java.util.Arrays;
import java.util.Dictionary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import data_streamer.Market;
import data_streamer.fx.ForexTerminal;

public class BarChartAveragesController
{

    @FXML
    private BarChart<String, Double> currencyBarGraph;

    @FXML
    private CategoryAxis             xAxis;

    private ObservableList<String>   forexMarkets = FXCollections
                                                      .observableArrayList();

    private String[]                 exchanges;

    @FXML
    private void initialize()
    {
        exchanges =
            new String[] {"CADUSD", "EURUSD", "JPYUSD", "CHFUSD", "GBPUSD",
                "NZDUSD", "AUDUSD"};
        // Convert it to a list and add it to our ObservableList of months.
        forexMarkets.addAll(Arrays.asList(exchanges));

        // Assign the month names as categories for the horizontal axis.
        xAxis.setCategories(forexMarkets);
    }

    public void setMarketData(final Market mkt)
    {
        Runnable updateMarket =
            () -> {
                while (true) {
                    try {
                        System.out.println(Thread.currentThread() + ": "
                            + System.currentTimeMillis());
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    } // run it 20x fast
                    mkt.tick(); // increments by 1 second
                    Dictionary<String, Dictionary<String, Dictionary<String, String>>> cur =
                        mkt.getEx().getCurrent();
                    if (cur != null && cur.get("PRICE") != null)
                        for (String ex : exchanges)
                            if (cur.get("PRICE").get(ex) != null)
                                System.out.println(Double.parseDouble(cur.get(
                                    "PRICE").get(ex).get("AVERAGE")));
                }
            };

        ForexTerminal.exec.execute(updateMarket);
    }
}
