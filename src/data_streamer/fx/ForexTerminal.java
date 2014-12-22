package data_streamer.fx;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import data_streamer.Market;
import data_streamer.fx.view.BarChartAveragesController;

public class ForexTerminal extends Application
{
    public static final ExecutorService exec = Executors.newCachedThreadPool();

    private BorderPane                  rootLayout;
    private Stage                       primaryStage;
    private AnchorPane                  terminalView;
    private Market                      mkt;

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
            AnchorPane page = (AnchorPane) loader.load();

            // Set the persons into the controller.
            BarChartAveragesController controller = loader.getController();
            // The only child of terminalView is the SplitPane
            for (Node node : terminalView.getChildren()) {
                SplitPane pane = (SplitPane) node;
                for (Node split : pane.getItems()) {
                    String id = split.getId();
                    if (id != null && id.equals("barChartPane")) {
                        // Found the pane, get the scroll pane
                        AnchorPane aPane = (AnchorPane) split;
                        ScrollPane sPane =
                            (ScrollPane) aPane.getChildren().get(0);
                        sPane.setContent(page);
                    }
                }
            }

            controller.setMarketData(mkt);

        } catch (IOException e) {
            e.printStackTrace();
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
