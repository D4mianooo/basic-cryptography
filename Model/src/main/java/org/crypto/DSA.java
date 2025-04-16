package org.crypto;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;

public class DSA {
    public DSA(){
        GenerateKey(2048);
        
    }
    
    public byte[] GenerateKey(int bitsSize){
        String str = DigestUtils.sha1Hex("dupa");
        int N = 256;
        int H = str.length() * 8;
        int L = bitsSize;
        
        byte[] q = new byte[N/8];
        byte[] p = new byte[bitsSize/8];
        System.out.println(str);
        return q;     
    }
}
