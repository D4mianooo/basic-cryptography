package org.crypto;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        DSA dsa = new DSA();
        dsa.Sign("abcdefg");
    }
}
