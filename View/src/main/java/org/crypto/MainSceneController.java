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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {
    private FileChooser fileChooser;
    private int keySize;
    private byte[] plainText;
    private byte[] cipherText;
    private byte[] keyBytes;
    boolean isFile = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AES aes = new AES();
        fileChooser = new FileChooser();
        List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
        filters.add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        filters.add(new FileChooser.ExtensionFilter("Image", "*.png"));
        filters.add(new FileChooser.ExtensionFilter("Image", "*.jpg"));
        filters.add(new FileChooser.ExtensionFilter("Image", "*.jpeg"));
        filters.add(new FileChooser.ExtensionFilter("Documents", "*.docx"));
        filters.add(new FileChooser.ExtensionFilter("Documents", "*.csv"));
        filters.add(new FileChooser.ExtensionFilter("Key", "*.key"));
        filters.add(new FileChooser.ExtensionFilter("Ciphertext", "*.cipher"));
        filters.add(new FileChooser.ExtensionFilter("All", "*.*"));
        fileChooser.getExtensionFilters().addAll(filters);
        openFileBtn.setOnAction(this::OpenPlainTextDialog);
        saveFileBtn.setOnAction(this::SavePlainTextDialog);
        openCiphertextBtn.setOnAction(this::OpenCipherTextDialog);
        saveCiphertextBtn.setOnAction(this::SaveCipherTextDialog);
        openKeyBtn.setOnAction(this::OpenKeyDialog);
        saveKeyBtn.setOnAction(this::SaveKeyDialog);
        
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

        ToggleGroup group = new ToggleGroup();
        Btn128.setToggleGroup(group);
        Btn192.setToggleGroup(group);
        Btn256.setToggleGroup(group);
        
        ToggleGroup group1 = new ToggleGroup();
        plainTextBtn.setToggleGroup(group1);
        fileBtn.setToggleGroup(group1);
        group1.selectToggle(plainTextBtn);
        
        group1.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            isFile = newToggle == fileBtn;
        });
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
            cipherText = aes.EncryptText(plainText);
            output.setText(new BigInteger(1, cipherText).toString(16));
        });
        
        decryptBtn.setOnAction(event -> {
            aes.SetKey(keyBytes);
            plainText = aes.DecryptText(cipherText);
            
            byte[] removeZeros = getRemoveZeros(plainText);
            
            if(isFile) {
                input.setText(new String(removeZeros, StandardCharsets.UTF_8));
            }else {
                input.setText(new String(plainText, StandardCharsets.UTF_8));
            }
        });
                
        keyBtn.setOnAction(event -> {
            if(keySize == 0) return;
            keyBytes = AESKey.CreateKey(keySize);
            keyVal.setText(new BigInteger(1, keyBytes).toString(16));
        });
        
        keyVal.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue) {
                if(IsKeyValid(keyVal.getText())){
                    keyBytes = getBytesFromHex(keyVal.getText());
                    System.out.println(new BigInteger(1, keyBytes).toString(16));
                };
            }
        });
    }

    private static byte[] getRemoveZeros(byte[] decrypted) {
        int idx = 0;
        for (int i = 0; i < decrypted.length; i++) {
            if(idx == 0 && decrypted[i] == 0){
                idx = i - 1;
            }
        }
        byte[] removeZeros = new byte[idx + 1];

        System.arraycopy(decrypted, 0, removeZeros, 0, idx + 1);
        return removeZeros;
    }

    private void OpenKeyDialog(ActionEvent actionEvent) {
        fileChooser.setTitle("Open Key");
        File file = fileChooser.showOpenDialog(saveKeyBtn.getScene().getWindow());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            keyBytes = fileInputStream.readAllBytes();
            keyVal.setText(new BigInteger(1, keyBytes).toString(16));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void SaveKeyDialog(ActionEvent actionEvent) {
        fileChooser.setTitle("Save Key");
        File file = fileChooser.showSaveDialog(saveKeyBtn.getScene().getWindow());
        try (FileOutputStream fileInputStream = new FileOutputStream(file)) {
            if(keyBytes != null) {
                fileInputStream.write(keyBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void SaveCipherTextDialog(ActionEvent actionEvent) {
        fileChooser.setTitle("Save Ciphertext");
        File file = fileChooser.showSaveDialog(saveCiphertextBtn.getScene().getWindow());
        cipherTextSavePath.setText(file.getAbsolutePath());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(cipherText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void OpenCipherTextDialog(ActionEvent actionEvent) {
        fileChooser.setTitle("Open Ciphertext");
        File file = fileChooser.showOpenDialog(openCiphertextBtn.getScene().getWindow());
        cipherTextPath.setText(file.getAbsolutePath());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            cipherText = fileInputStream.readAllBytes();
            output.setText(new BigInteger(1, cipherText).toString(16));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void OpenPlainTextDialog(javafx.event.ActionEvent actionEvent) {
        fileChooser.setTitle("Open Plaintext");
        File file = fileChooser.showOpenDialog(openFileBtn.getScene().getWindow());
        plainTextPath.setText(file.getAbsolutePath());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            plainText = fileInputStream.readAllBytes();
            if(isFile) {
                input.setText(new String(getRemoveZeros(plainText), StandardCharsets.UTF_8));
            }else {
                input.setText(new String(plainText, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void SavePlainTextDialog(javafx.event.ActionEvent actionEvent) {
        fileChooser.setTitle("Save Plaintext");
        File file = fileChooser.showSaveDialog(saveFileBtn.getScene().getWindow());
        plainTextSavePath.setText(file.getAbsolutePath());
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

    private boolean IsKeyValid(String bytes) {
        return bytes.length() % 16 == 0;
    }
    
    public RadioButton Btn128;
    public RadioButton Btn192;
    public RadioButton Btn256;
    public RadioButton plainTextBtn;
    public RadioButton fileBtn;
    
    public TextArea input;
    public TextArea output;
    
    public TextField keyVal;
    public TextField plainTextPath;
    public TextField plainTextSavePath;
    public TextField cipherTextPath;
    public TextField cipherTextSavePath;
    
    public MenuItem desBtn;
    public MenuItem aesBtn;
    
    public Button encryptBtn;
    public Button decryptBtn;
    public Button keyBtn;
    public Button saveFileBtn;
    public Button openCiphertextBtn;
    public Button saveKeyBtn;
    public Button openKeyBtn;
    public Button saveCiphertextBtn;
    public Button openFileBtn;
}
