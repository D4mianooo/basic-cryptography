package org.crypto;

import com.sun.javafx.tk.FileChooserType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    public Button saveBtn;
    private FileChooser fileChooser;
    
    private int keySize;
    private byte[] plainText;
    private byte[] cipherText;
    private byte[] keyBytes;
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
        openBtn.setOnAction(this::OpenFileDialog);
        saveBtn.setOnAction(this::SaveFileDialog);
        
        encryptBtn.setOnAction(event -> {
            byte[] plainTextBytes = input.getText().getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = aes.EncryptText(plainTextBytes);
            output.setText(new BigInteger(1, encrypted).toString(16));
            plainText = plainTextBytes;
            cipherText = encrypted;
        });
        decryptBtn.setOnAction(event -> {
            byte[] bytes = getBytesFromHex(output.getText());
            byte[] decrypted = aes.decode(bytes);
            int idx = 0; 
            for (int i = 0; i < decrypted.length; i++) {
                if(idx == 0 && decrypted[i] == 0){
                    idx = i - 1;
                }
            }
            byte[] removeZeros = new byte[idx + 1];
            System.arraycopy(decrypted, 0, removeZeros, 0, idx + 1);
            System.out.println(Arrays.toString(removeZeros));
            System.out.println(Arrays.toString(decrypted));
            
            plainText = removeZeros;
            cipherText = bytes;
            input.setText(new String(removeZeros, StandardCharsets.UTF_8));
        });
                
        keyBtn.setOnAction(event -> {
            if(keySize == 0) return;
            keyBytes = AESKey.CreateKey(keySize);
            aes.SetKey(keyBytes);
            keyVal.setText(new BigInteger(1, keyBytes).toString(16));
        });
        
        keyVal.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue) {
                if(!IsKeyValid(keyVal.getText())) {
                    System.out.println("Dupa");
                }else {
                    byte[] key = getBytesFromHex(keyVal.getText());
                    System.out.println(Arrays.toString(key));
                    aes.SetKey(key);
                };
            }
        });
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

    private void OpenFileDialog(javafx.event.ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(openBtn.getScene().getWindow());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            input.setText(new String(fileInputStream.readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void SaveFileDialog(javafx.event.ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(openBtn.getScene().getWindow());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(input.getText().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
