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
        byte[] bytes = password.getBytes();
        byte[] bytes2 = key.getBytes();
        aes.SetKey(bytes2);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
        System.out.println(new BigInteger(1, aes.EncryptBlock(bytes)).toString(16));
    }
}