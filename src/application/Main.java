package application;

import controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainController controller = new MainController(primaryStage);

        Scene scene = new Scene(controller.getMainView(), 1200, 800);

        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setTitle("Система управления складом");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}