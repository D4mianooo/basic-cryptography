package org.crypto;

import java.security.SecureRandom;

public class AES {
    private static final int KEY_SIZE = 128;
    public AES(){
    }
    
    public void EncryptBlock(byte[] state, byte[] key) {
        byte[] keyExpanded = KeyExpansion(key);
        byte[] newState = AddRoundKey(state, keyExpanded);
    }

    private byte[] AddRoundKey(byte[] state, byte[] key) {
        byte[] newState = new byte[KEY_SIZE];
        for(int i = 0; i < KEY_SIZE; i++){
            newState[i] = (byte)(state[i]^key[i]);
        }
        return newState;
    }

    public byte[]  KeyExpansion(byte[] key){
        return key;     
    }
    
    public byte[] InitialKey(){
        byte[] key = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return key;
    }

}
