package data_streamer.fx.view;

import javafx.scene.control.ListView;

public class TableViewController
{
    private ListView<String> averageTable;
    private ListView<String> varianceTable;
    private ListView<String> trendTable;

    public void setAverages(ListView<String> listView)
    {
        averageTable = listView;
    }

    public void setVariances(ListView<String> listView)
    {
        varianceTable = listView;
    }

    public void setTrends(ListView<String> listView)
    {
        trendTable = listView;
    }
}
