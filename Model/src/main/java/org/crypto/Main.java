package org.crypto;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        AES aes = new AES();
        String password = new String("dupadupadupadupa");
        String key = new String("abcdefghijklmnop");
        byte[] plain = password.getBytes();
        byte[] keyBytes = key.getBytes();
        aes.SetKey(keyBytes);
        
        byte[] encrypted = aes.EncryptBlock(plain);
        byte[] decrypted = aes.DecryptBlock(encrypted);
        for (int i = 0; i < encrypted.length; i++) {
            System.out.println(plain[i] + " | " + decrypted[i]);
        }
        System.out.println(new BigInteger(1, plain).toString(16));
        System.out.println(new BigInteger(1, keyBytes).toString(16));
        System.out.println(new BigInteger(1, encrypted).toString(16));
        System.out.println(new BigInteger(1, aes.DecryptBlock(encrypted)).toString(16));
    }
}