package org.crypto;

public class Main {
    public static void main(String[] args) {
        DSA dsa = new DSA();
        dsa.GenerateKey(128);
    }
}
