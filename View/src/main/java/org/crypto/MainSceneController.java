package org.crypto;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.jfr.Event;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {

    @FXML
    private TextField keyValue;
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
    
    private FileChooser fileChooser;

    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        openBtn.setOnAction(this::OpenFileDialog);
        encryptBtn.setOnAction(event -> {
            output.setText(input.getText());
        });
        keyBtn.setOnAction(event -> {
            AES aes = new AES();
            byte[] bytes = aes.InitialKey();
            System.out.println(bytes.length);
            keyValue.setText(bytes.toString());
        });
    }

    private void OpenFileDialog(javafx.event.ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(openBtn.getScene().getWindow());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            input.setText(new String(fileInputStream.readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
