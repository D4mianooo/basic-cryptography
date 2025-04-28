package org.crypto;

import javafx.util.Pair;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        DSA dsa = new DSA();
        Pair<DSA.DSAPrivateKey, DSA.DSAPublicKey> keys = dsa.GenerateKeyPair(1024);
        String text = "Hello World!";
        DSA.DSASignature signature = dsa.Sign(text.getBytes(), keys.getKey());
        String text2 = "Hello World!";
        System.out.println(dsa.Verify(text2.getBytes(), signature, keys.getValue()));
    }
}
