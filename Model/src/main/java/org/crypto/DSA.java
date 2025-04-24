package org.crypto;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.KeyGenerator;
import java.math.BigInteger;
import java.nio.channels.NonWritableChannelException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DSA {
    private BigInteger p;
    private BigInteger q;
    private BigInteger g;
    private BigInteger x;
    private BigInteger y;

    public DSA() {
        GenerateKeyPair(1024);
        System.out.println("p: " + p.toString(16));
        System.out.println("q: " + q.toString(16));
        System.out.println("g: " + g.toString(16));
        System.out.println("x: " + x.toString(16));
        System.out.println("y: " + y.toString(16));
    }
            
    public void Sign(String message){
        Random random = new Random();
        String H = DigestUtils.sha1Hex(message);
        
        BigInteger k =  BigInteger.ZERO;
        while (k.equals(BigInteger.ZERO)) {
            k = new BigInteger(q.bitCount(), random);
        }
        
        BigInteger r = g.modPow(k, p).mod(q);
        BigInteger b = new BigInteger(H, 16);
        BigInteger hm = x.multiply(r).add(b).mod(q);
        BigInteger s = k.modInverse(q).multiply(hm).mod(q);
        
        System.out.println("(" + r.toString(16) + ", " + s.toString(16) + ")");
    }
    
    public void GenerateKeyPair(int bitsSize) {
        int N = 160;
        int L = bitsSize;

        Random random = new Random();
        
        
        BigInteger q = BigInteger.probablePrime(N, random);
        BigInteger p = BigInteger.probablePrime(L, random);

        int h = getRandomNumber(2, p.intValue() - 2);

        BigInteger power = p.subtract(BigInteger.ONE).divide(q);
        BigInteger g = BigInteger.valueOf(h).modPow(power, p);
        //FINE

        BigInteger x = BigInteger.ZERO;

        while (x.equals(BigInteger.ZERO)) {
            x = new BigInteger(q.bitCount(), random);
        }
        
        BigInteger y = g.modPow(x, p);
        
        this.x = x;
        this.y = y;
        this.q = q;
        this.p = p;
        this.g = g;
    }
        
    public int getRandomNumber ( int min, int max){
        return (int) ((Math.random() * (max - min)) + min);
    }

    public BigInteger getY() {
        return y;
    }
}
