package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {
    @FXML
    private Button openBtn;
    public void initialize() {
        openBtn.getScene().getWindow();
    }
}
