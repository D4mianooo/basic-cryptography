package org.crypto;

import java.security.SecureRandom;

public class AESKey {
    public static byte[] CreateKey(int bitsSize){
        byte[] key = new byte[bitsSize/8];
        SecureRandom random = new SecureRandom();    
        random.nextBytes(key);
        return key;
    }
}
