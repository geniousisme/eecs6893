package data_streamer.fx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ForexTerminal extends Application
{
    private BorderPane rootLayout;
    private Stage      primaryStage;

    public void initRootLayout()
    {
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

    public void showTerminalView() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ForexTerminal.class.getResource("/data_streamer/fx/view/TerminalView.fxml"));
            AnchorPane terminalView = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(terminalView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage()
    {
        return primaryStage;
    }
    
    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Forex Terminal");

        initRootLayout();

        showTerminalView();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
