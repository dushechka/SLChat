package client.view.search;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Searching servers menu;
 */
public class Search extends Application implements Runnable {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Search.fxml"));

        Scene scene = new Scene(root,240,360);

        stage.setTitle("SLChat");
        stage.setScene(scene);
        stage.show();
    }

    public void run() {
        System.out.println("Search thread started.");
//        String[] args = new String[0];
//        this.launch(args);
    }
}
