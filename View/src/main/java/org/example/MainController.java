package org.example;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button openBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        openBtn.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(openBtn.getScene().getWindow());
        });
    }
}
