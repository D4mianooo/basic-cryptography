package org.crypto;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {
    public RadioButton Btn128;
    public RadioButton Btn192;
    public RadioButton Btn256;
    public TextArea input;
    public TextArea output;
    public Button openBtn;
    public Button encryptBtn;
    public Button decryptBtn;
    public Button keyBtn;
    public TextField keyVal;
    public MenuItem desBtn;
    public MenuItem aesBtn;
    
    private FileChooser fileChooser;

    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AES aes = new AES();
        
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        ToggleGroup group = new ToggleGroup();
        Btn128.setToggleGroup(group);
        Btn192.setToggleGroup(group);
        Btn256.setToggleGroup(group);
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldToggle, Toggle newToggle) {
                System.out.println(newToggle);
            }
        });
        openBtn.setOnAction(this::OpenFileDialog);
        encryptBtn.setOnAction(event -> {
            output.setText(input.getText());
        });
        keyBtn.setOnAction(event -> {
            byte[] bytes = AESKey.CreateKey(128);
            aes.SetKey(bytes);
            keyVal.setText(new BigInteger(1, bytes).toString(16));
        });
        
        keyVal.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue) {
                if(!IsKeyValid(keyVal.getText())) {
                    System.out.println("Dupa");
                };
            }
        });
    }

    private boolean IsKeyValid(String text) {
        return text.length() % 16 == 0;
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
