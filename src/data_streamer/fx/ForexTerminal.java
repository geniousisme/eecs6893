package data_streamer.fx;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import data_streamer.Market;
import data_streamer.fx.view.BarChartAveragesController;
import data_streamer.fx.view.LineGraphController;
import data_streamer.fx.view.TableViewController;

public class ForexTerminal extends Application
{
    public static final ExecutorService exec      = Executors
                                                      .newCachedThreadPool();

    private Set<String>                 exchanges = new HashSet<>(Arrays
                                                      .asList(new String[] {
        "CADUSD", "EURUSD", "JPYUSD", "CHFUSD", "GBPUSD", "NZDUSD", "AUDUSD"}));

    private BorderPane                  rootLayout;
    private Stage                       primaryStage;
    private AnchorPane                  terminalView;
    private Market                      mkt;

    private BarChartAveragesController  barChartController;

    private LineGraphController         lineGraphController;

    private TableViewController         tableViewController;

    public Stage getPrimaryStage()
    {
        return primaryStage;
    }

    public void initRootLayout()
    {
        mkt =
            new Market("src/config/market.config",
                "src/config/data_config.txt", "./");

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

        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ForexTerminal.class
                .getResource("/data_streamer/fx/view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTerminalView()
    {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ForexTerminal.class
                .getResource("/data_streamer/fx/view/TerminalView.fxml"));
            terminalView = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(terminalView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showExchangeBarGraph()
    {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader() {};
            loader
                .setLocation(ForexTerminal.class
                    .getResource("/data_streamer/fx/view/BarChartAveragesView.fxml"));
            BarChart<?, ?> sp = (BarChart<?, ?>) loader.load();

            barChartController = loader.getController();
            // The only child of terminalView is the SplitPane
            for (Node node : terminalView.getChildren()) {
                SplitPane pane = (SplitPane) node;
                for (Node split : pane.getItems()) {
                    String id = split.getId();
                    if (id != null && id.equals("barChartPane")) {
                        // Found the pane, get the scroll pane
                        AnchorPane aPane = (AnchorPane) split;
                        AnchorPane.setTopAnchor(sp, 0.0);
                        AnchorPane.setRightAnchor(sp, 0.0);
                        AnchorPane.setBottomAnchor(sp, 0.0);
                        AnchorPane.setLeftAnchor(sp, 0.0);
                        aPane.getChildren().add(sp);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runMarket()
    {
        Runnable updateMarket = () -> {
            while (true) {
                try {
                    // System.out.println(Thread.currentThread() + ": "
                    // + System.currentTimeMillis());
            Thread.sleep(100);
        } catch (InterruptedException e) {
            break;
        } // run it 20x fast
        mkt.tick(); // increments by 1 second
        Dictionary<String, Dictionary<String, Dictionary<String, String>>> cur =
            mkt.getEx().getCurrent();
        if (cur != null && cur.get("PRICE") != null) {
            Map<String, Double> avgMap = new HashMap<>();
            for (String ex : exchanges) {
                Dictionary<String, String> priceDict = cur.get("PRICE").get(ex);
                if (priceDict != null) {
                    Double avg;
                    try {
                        avg = Double.parseDouble(priceDict.get("AVERAGE"));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    avgMap.put(ex, avg);
                }
            }
            barChartController.updateBars(avgMap);
            lineGraphController.updateGraphs(avgMap);
            tableViewController.updateTables(avgMap);
        }
    }
}       ;
        exec.execute(updateMarket);
    }

    private void showLineGraphs()
    {
        // The only child of terminalView is the SplitPane
        for (Node node : terminalView.getChildren()) {
            SplitPane pane = (SplitPane) node;
            for (Node split : pane.getItems()) {
                String id = split.getId();
                if (id != null && id.equals("topBarPane")) {
                    // Found the pane, get the split pane
                    AnchorPane aPane = (AnchorPane) split;
                    SplitPane topPane = (SplitPane) aPane.getChildren().get(0);
                    for (Node topPaneNode : topPane.getItems()) {
                        id = topPaneNode.getId();
                        if (id != null && id.equals("graphsAnchorPane")) {
                            AnchorPane graphAnchorPane =
                                (AnchorPane) topPaneNode;
                            // Get the VBox
                            VBox lineGraphBox =
                                (VBox) ((ScrollPane) graphAnchorPane
                                    .getChildren().get(0)).getContent();
                            lineGraphController = new LineGraphController();
                            lineGraphController.initialize(lineGraphBox);
                        }
                    }
                }
            }
        }
    }

    private void showTableStates()
    {
        tableViewController = new TableViewController();
        // The only child of terminalView is the SplitPane
        for (Node node : terminalView.getChildren()) {
            SplitPane pane = (SplitPane) node;
            for (Node split : pane.getItems()) {
                String id = split.getId();
                if (id != null && id.equals("topBarPane")) {
                    // Found the pane, get the split pane
                    AnchorPane aPane = (AnchorPane) split;
                    SplitPane topPane = (SplitPane) aPane.getChildren().get(0);
                    for (Node topPaneNode : topPane.getItems()) {
                        id = topPaneNode.getId();
                        if (id != null && id.equals("tableAnchorPane")) {
                            AnchorPane tabAnchorPane = (AnchorPane) topPaneNode;
                            // Get the TabPane
                            TabPane tabPane =
                                (TabPane) tabAnchorPane.getChildren().get(0);
                            for (Tab tab : tabPane.getTabs()) {
                                id = tab.getId();
                                if (id != null && id.equals("averages"))
                                    tableViewController
                                        .setAverages((ListView<String>) ((ScrollPane) tab
                                            .getContent()).getContent());
                                else if (id != null && id.equals("variances"))
                                    tableViewController
                                        .setVariances((ListView<String>) ((ScrollPane) tab
                                            .getContent()).getContent());
                                else if (id != null && id.equals("trends"))
                                    tableViewController
                                        .setTrends((ListView<String>) ((ScrollPane) tab
                                            .getContent()).getContent());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Forex Terminal");

        initRootLayout();

        showTerminalView();
        showExchangeBarGraph();
        showLineGraphs();
        showTableStates();

        runMarket();
    }

    @Override
    public void stop()
    {
        exec.shutdownNow();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
