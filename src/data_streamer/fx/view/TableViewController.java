package data_streamer.fx.view;

import graph.ForexTrendAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class TableViewController
{
    private static long               UPDATE_INTERVAL  = 250;

    private ListView<String>          averageTable;
    private ListView<String>          varianceTable;
    private ListView<String>          trendTable;
    private ObservableList<String>    averageList      =
                                                           FXCollections
                                                               .observableArrayList();
    private ObservableList<String>    varianceList     =
                                                           FXCollections
                                                               .observableArrayList();
    private ObservableList<String>    trendList        =
                                                           FXCollections
                                                               .observableArrayList();
    private Map<String, Integer>      listExPos        = new HashMap<>();

    private Map<String, List<Double>> avgListMap       = new HashMap<>();

    private long                      lastUpdated      = 0;
    private AtomicBoolean             isUpdatingGraphs = new AtomicBoolean(
                                                           false);

    public void setAverages(ListView<String> listView)
    {
        averageTable = listView;
        averageTable.setItems(averageList);
    }

    public void setVariances(ListView<String> listView)
    {
        varianceTable = listView;
        varianceTable.setItems(varianceList);
    }

    public void setTrends(ListView<String> listView)
    {
        trendTable = listView;
        trendTable.setItems(trendList);
    }

    public void updateTables(Map<String, Double> avgMap)
    {
        if (System.currentTimeMillis() - lastUpdated < UPDATE_INTERVAL)
            return;
        if (isUpdatingGraphs.getAndSet(true))
            return;
        lastUpdated = System.currentTimeMillis();
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                for (Map.Entry<String, Double> avgEntry : avgMap.entrySet())
                    // If the averateList doesn't contain this ex, add it
                    if (!listExPos.containsKey(avgEntry.getKey())) {
                        List<Double> avgList = new ArrayList<>();
                        avgListMap.put(avgEntry.getKey(), avgList);
                        int pos = averageList.size();
                        listExPos.put(avgEntry.getKey(), pos);
                        averageList.add(avgEntry.getKey() + "\t"
                            + avgEntry.getValue());
                        avgList.add(avgEntry.getValue());
                        List<Double> subList =
                            avgList.subList(Math.max(avgList.size() - 10, 0),
                                avgList.size());
                        Double variance =
                            ForexTrendAnalyzer.sd(new ArrayList<>(subList));
                        varianceList.add(avgEntry.getKey() + "\t" + variance);
                    } else {
                        // Update it
                        int pos = listExPos.get(avgEntry.getKey());
                        averageList.set(pos, avgEntry.getKey() + "\t"
                            + avgEntry.getValue());
                        List<Double> avgList =
                            avgListMap.get(avgEntry.getKey());
                        avgList.add(avgEntry.getValue());
                        List<Double> subList =
                            avgList.subList(Math.max(avgList.size() - 10, 0),
                                avgList.size());
                        Double variance =
                            ForexTrendAnalyzer.sd(new ArrayList<>(subList));
                        varianceList.set(pos, avgEntry.getKey() + "\t"
                            + variance);
                    }
            }
        });
        isUpdatingGraphs.set(false);
    }
}
