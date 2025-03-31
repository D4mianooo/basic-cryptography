package org.crypto;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {
    public RadioButton Btn128;
    public RadioButton Btn192;
    public RadioButton Btn256;
    public TextArea input;
    public TextArea output;
    public Button encryptBtn;
    public Button decryptBtn;
    public Button keyBtn;
    public TextField keyVal;
    public MenuItem desBtn;
    public MenuItem aesBtn;
    public Button saveFileBtn;
    public Button openCiphertextBtn;
    public Button saveCiphertextBtn;
    public Button openFileBtn;
    private FileChooser fileChooser;
    
    private int keySize;
    private byte[] plainText;
    private byte[] cipherText;
    private byte[] keyBytes;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AES aes = new AES();

        openFileBtn.setOnAction(this::OpenPlainTextDialog);
        saveFileBtn.setOnAction(this::SavePlainTextDialog);
        openCiphertextBtn.setOnAction(this::OpenCipherTextDialog);
        saveCiphertextBtn.setOnAction(this::SaveCipherTextDialog);
        input.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue) {
                plainText = input.getText().getBytes(StandardCharsets.UTF_8);
            }
        });
        output.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue) {
                cipherText = getBytesFromHex(output.getText());
            }
        });
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        
        ToggleGroup group = new ToggleGroup();
        Btn128.setToggleGroup(group);
        Btn192.setToggleGroup(group);
        Btn256.setToggleGroup(group);
        
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldToggle, Toggle newToggle) {
                if(newToggle == Btn128){
                    keySize = 128;
                }                
                if(newToggle == Btn192){
                    keySize = 192;
                }                
                if(newToggle == Btn256){
                    keySize = 256;
                }
            }
        });
        

        encryptBtn.setOnAction(event -> {
            aes.SetKey(keyBytes);
            byte[] encrypted = aes.EncryptText(plainText);
            output.setText(new BigInteger(1, encrypted).toString(16));
            cipherText = encrypted;
        });
        
        decryptBtn.setOnAction(event -> {
            aes.SetKey(keyBytes);
            byte[] decrypted = aes.decode(cipherText);
            int idx = 0; 

            for (int i = 0; i < decrypted.length; i++) {
                if(idx == 0 && decrypted[i] == 0){
                    idx = i - 1;
                }
            }
            byte[] removeZeros = new byte[idx + 1];

            System.arraycopy(decrypted, 0, removeZeros, 0, idx + 1);
            System.out.println(Arrays.toString(removeZeros));

            plainText = decrypted;
            input.setText(new String(plainText, StandardCharsets.UTF_8));
        });
                
        keyBtn.setOnAction(event -> {
            if(keySize == 0) return;
            keyBytes = AESKey.CreateKey(keySize);
            keyVal.setText(new BigInteger(1, keyBytes).toString(16));
        });
        
        keyVal.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue) {
                if(IsKeyValid(keyVal.getText())) {
                    keyBytes = getBytesFromHex(keyVal.getText());
                    keySize = keyBytes.length;
                };
            }
        });
    }

    private void SaveCipherTextDialog(ActionEvent actionEvent) {
        
    }

    private void OpenCipherTextDialog(ActionEvent actionEvent) {
        
    }
    
    private void OpenPlainTextDialog(javafx.event.ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(openFileBtn.getScene().getWindow());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            plainText = fileInputStream.readAllBytes();
            input.setText(new BigInteger(1, plainText).toString(16));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void SavePlainTextDialog(javafx.event.ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(saveFileBtn.getScene().getWindow());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(plainText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getBytesFromHex(String hex) {
        int l = hex.length()/2;
        byte[] bytes = new byte[l];
        for(int i = 0; i < l; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    private boolean IsKeyValid(String text) {
        return text.length() % 16 == 0;
    }
}
