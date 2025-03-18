package org.crypto;

import java.nio.charset.StandardCharsets;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String password = new String("DUPA");
        byte[] bytes = password.getBytes();
        System.out.println(bytes.length);
        System.out.println((16 - bytes.length) );
        AES aes = new AES();
    }
}