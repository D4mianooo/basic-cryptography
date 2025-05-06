package org.crypto;

import javafx.util.Pair;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

public class DSA  implements Serializable {
    public class DSAParams implements Serializable{
        private BigInteger p, q, g;
        DSAParams(BigInteger p, BigInteger q, BigInteger g) {
            this.p = p;
            this.q = q;
            this.g = g;
        }
        public BigInteger getP() {
            return p;
        }
        public BigInteger getQ() {
            return q;
        }
        public BigInteger getG() {
            return g;
        }
    }
    public class DSAPublicKey implements Serializable {
        private DSAParams params;
        private BigInteger y;
        DSAPublicKey(BigInteger p, BigInteger q, BigInteger g, BigInteger y) {
            params = new DSAParams(p, q, g);
            this.y = y;
        }
        DSAPublicKey(DSAParams params, BigInteger y) {
            this.params = params; 
            this.y = y;
        }
        public BigInteger getY() {
            return y;
        }

        public DSAParams getParams() {
            return params;
        }
    }
    public class DSAPrivateKey implements Serializable {
        private DSAParams params;
        private BigInteger x;
        DSAPrivateKey(BigInteger p, BigInteger q, BigInteger g, BigInteger x) {
            params = new DSAParams(p, q, g);
            this.x = x;
        }
        DSAPrivateKey(DSAParams params, BigInteger x) {
            this.params = params;
            this.x = x;
        }
        public BigInteger getX() {
            return x;
        }
    }
    
    public static class DSASignature implements Serializable {
        private BigInteger r, s;
        public DSASignature(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }


        public BigInteger getR() {
            return r;
        }

        public BigInteger getS() {
            return s;
        }
    }
    
    public DSA() {
    }

    public Pair<DSAPrivateKey, DSAPublicKey> GenerateKeyPair(int bitsSize) {
        int N = 160;
        int L = bitsSize;

        Random random = new Random();
        BigInteger q = BigInteger.probablePrime(N, random);
        //
        BigInteger p;
        
        do {
            p = BigInteger.probablePrime(L, random);
            p = p.subtract(p.subtract(BigInteger.ONE).remainder(q));
        }while (!(p.isProbablePrime(1)));
        
        BigInteger g;
        BigInteger power = p.subtract(BigInteger.ONE).divide(q);
        
        do {
            BigInteger h = new BigInteger(bitsSize, random).mod(p.subtract(BigInteger.valueOf(3))).add(BigInteger.TWO);
            g = h.modPow(power, p);
        }while (!(g.compareTo(BigInteger.ONE) == 1));
        //
        BigInteger x =  new BigInteger(q.bitCount(), random).mod(q.subtract(BigInteger.ONE)).add(BigInteger.ONE);;

        BigInteger y = g.modPow(x, p);

        DSAParams params = new DSAParams(p, q, g);
        DSAPublicKey publicKey = new DSAPublicKey(params, y);
        DSAPrivateKey privateKey  = new DSAPrivateKey(params, x);
        
        return new Pair<>(privateKey, publicKey);
    }
    
    public boolean Verify(byte[] message, DSASignature dsaSignature, DSAPublicKey publicKey) {
        BigInteger q = publicKey.getParams().getQ();
        BigInteger p = publicKey.getParams().getP();
        BigInteger g = publicKey.getParams().getG();
        BigInteger y = publicKey.getY();
        BigInteger r = dsaSignature.getR();
        BigInteger s = dsaSignature.getS();
        
        BigInteger H = new BigInteger(DigestUtils.sha1(message));
        
        BigInteger w = s.modInverse(q);
        
        BigInteger u1 = w.multiply(H).mod(q);
        
        BigInteger u2 = w.multiply(r).mod(q);
        
        BigInteger v = g.mod(p).modPow(u1, p).multiply(y.mod(p).modPow(u2, p)).mod(p).mod(q);

        return v.compareTo(r) == 0;
    }
    
    public DSASignature Sign(byte[] message, DSAPrivateKey privateKey){
        Random random = new Random();

        BigInteger q = privateKey.params.getQ();
        BigInteger p = privateKey.params.getP();
        BigInteger g = privateKey.params.getG();
        BigInteger x = privateKey.getX();
        
        BigInteger k =  new BigInteger(q.bitCount(), random).mod(q.subtract(BigInteger.ONE)).add(BigInteger.ONE);;
        
        BigInteger r = g.modPow(k, p).mod(q);
        
        BigInteger H = new BigInteger( DigestUtils.sha1(message));
        
        BigInteger hm = x.multiply(r).add(H).mod(q);
        BigInteger s = k.modInverse(q).multiply(hm).mod(q);

        return new DSASignature(r, s);    
    }
}
