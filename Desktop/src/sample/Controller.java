package sample;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Main controller. It supports all buttons and commands.
 */
public class Controller implements Initializable {

    public Pane background;
    public TextField destFolderText;
    public Button destFolderButton;
    public Button closeButton;
    public TextFlow textFlow;
    public Button minimalizeButton;
    public Button infoButton;
    private Serwer serwer;
    private MulticastReceiv multicastReceiv;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        background.setStyle("-fx-background-color: linear-gradient(from 0% 10% to 0% 90%, #42275a, #734b6d);");


        minimalizeButton.setStyle(
                "-fx-base: #38214d;" +
                "-fx-faint-focus-color: transparent;" +
                "-fx-focus-color: rgba(216, 27, 96, 0.4);");

        closeButton.setStyle(
                "-fx-base: #38214d;" +
                "-fx-faint-focus-color: transparent;" +
                "-fx-focus-color: rgba(216, 27, 96, 0.4);");

        destFolderButton.setStyle(
                "-fx-base: #38214d;" +
                "-fx-faint-focus-color: transparent;" +
                "-fx-focus-color: rgba(216, 27, 96, 0.4);");

        infoButton.setStyle(
                "-fx-base: #38214d;" +
                "-fx-faint-focus-color: transparent;" +
                "-fx-focus-color: rgba(216, 27, 96, 0.4);");

        destFolderText.setStyle(
                "-fx-faint-focus-color: transparent;" +
                "-fx-focus-color: rgba(216, 27, 96, 0.4);"
        );

        destFolderText.setText("C:\\");
        serwer = new Serwer("C:\\", this);
        serwer.start();
        multicastReceiv = new MulticastReceiv();
        multicastReceiv.start();
    }

    public void onCloseButtonAction()
    {
        Alert alert =  new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Application");
        alert.setHeaderText("Are you sure want to close application?");
        alert.setContentText(" You won't be able to send photos until you run it.");

        Optional<ButtonType> option = alert.showAndWait();

        if(option.get()==ButtonType.OK)
            System.exit(0);
    }

    public void onDesFolderButtonAction()
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(destFolderText.getText()));
        File selectedDir = directoryChooser.showDialog(background.getScene().getWindow());

        if(selectedDir != null)
        {
            String path = selectedDir.getAbsolutePath() + "\\";
            destFolderText.setText(path);
            serwer.changePath(path);
        }
    }

    public void addLog(String log)
    {
        Text newLog = new Text(log);
        newLog.setFill(Color.WHITE);
        textFlow.getChildren().add(newLog);
        if(textFlow.getChildren().size() > 6)
            textFlow.getChildren().remove(0);
    }

    public void onMinimalizeButtonAction()
    {
        Main.hideStage();
    }


    public void onInfoButton(ActionEvent actionEvent) throws IOException {
        URI uri = URI.create("https://jurcus111.wixsite.com/simplesenderer");
        java.awt.Desktop.getDesktop().browse(uri);
    }
}
