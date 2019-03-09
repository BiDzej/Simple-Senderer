package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Creating application window.
 */
public class Main extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    public static Stage primaryStage;

    public static void hideStage()
    {
        //primaryStage.hide();
        primaryStage.setIconified(true);
    }

    private void showStage()
    {
        primaryStage.show();
        primaryStage.toFront();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Simple Senderer");
        primaryStage.setScene(new Scene(root, 646, 389));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

        Image image = new Image("/icons/icona.png");
        primaryStage.getIcons().add(image);

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
