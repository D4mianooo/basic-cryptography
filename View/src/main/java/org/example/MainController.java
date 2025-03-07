package org.example;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private TextArea input;
    @FXML
    private TextArea output;
    @FXML
    private Button openBtn;
    @FXML
    private Button encryptBtn;
    @FXML
    private Button decryptBtn;
    @FXML
    private Button keyBtn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        openBtn.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(openBtn.getScene().getWindow());
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                input.setText(new String(fileInputStream.readAllBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        encryptBtn.setOnAction(event -> {
            output.setText(input.getText());
        });
        keyBtn.setOnAction(event -> {

        })
    }
}
