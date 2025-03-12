package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AESController implements Initializable {

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
    @FXML
    private MenuItem desBtn;
    @FXML
    public MenuItem aesBtn;
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
        desBtn.setOnAction(event -> {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/DES.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = (Stage) openBtn.getScene().getWindow() ;
            Button btn = new Button("Encrypt");
            stage.setScene(new Scene(root));
        });
//        keyBtn.setOnAction(event -> {
//
//        })
    }
}
