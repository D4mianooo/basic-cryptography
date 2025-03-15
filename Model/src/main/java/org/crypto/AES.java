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
    
    private byte[] SubBytes(byte[] state){
        byte[] newState = new byte[BLOCK_SIZE];
        for(int i = 0; i < BLOCK_SIZE; i++){
            newState[i] = SubByte(state[i]);
        }
        return newState;
    }
    
    public byte SubByte(byte state){
        return (byte) box[(state + 256) % 256];
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
