package org.crypto;

public class AES {
    private static final byte BLOCK_SIZE = 16;
    private byte ROUNDS;
    private int N_WORDS;
    byte[] key;

    public AES(){
    }

    public void SetKey(byte[] key){
        this.key = key;
        switch (key.length) {
            case 16:
                ROUNDS = 10;
                N_WORDS = 4;
                break;
            case 24:
                ROUNDS = 12;
                N_WORDS = 6;
                break;
            case 32:
                ROUNDS = 14;
                N_WORDS = 8;
                break;
        }
    }

    private byte[] EncryptBlock(byte[] state) {
        byte[][] keyExpanded = KeyExpansion(key);
        byte[] newState = AddRoundKey(state, keyExpanded[0]);
        
        for(int i = 1; i < ROUNDS; i++) {
            newState = SubBytes(newState);
            newState = ShiftRows(newState);
            newState = MixColumns(newState);
            newState = AddRoundKey(newState, keyExpanded[i]);
        }
        newState = SubBytes(newState);
        newState = ShiftRows(newState);
        newState = AddRoundKey(newState, keyExpanded[ROUNDS]);

        return newState;
    }

    private byte[][] KeyExpansion(byte[] key) {
        int expandedKeySize = (ROUNDS + 1) * 4;
        byte[][] expandedKey = new byte[expandedKeySize][4];
        byte[] temp = new byte[4];

        for(int i = 0; i < N_WORDS; i++) {
            expandedKey[i] = new byte[]{key[i * 4], key[i * 4 + 1], key[i * 4 + 2], key[i * 4 + 3]};
        }


        for(int word = N_WORDS; word < expandedKeySize; word++) {
            System.arraycopy(expandedKey[word - 1], 0, temp, 0, 4);
            if (word % N_WORDS == 0) {
                temp =  SubWord(RotWord(temp));
                temp[0] ^= (byte) RCON[(word/N_WORDS) - 1];
            } else if(N_WORDS > 6 && word % N_WORDS == 4){
                temp = SubWord(temp);
            }

            for(int i = 0; i < 4; i++) {
                expandedKey[word][i] = (byte) (expandedKey[word - N_WORDS][i] ^ temp[i]);
            }
        }

        byte[][] result = new byte[(ROUNDS + 1)][BLOCK_SIZE];
        for (int x = 0; x < (ROUNDS + 1); x++) {
            byte[] block = new byte[BLOCK_SIZE];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    block[i * 4 + j] = expandedKey[x * 4 + i][j];
                }
            }
            result[x] = block;
        }

        return result;
    }

    private byte[] AddRoundKey(byte[] state, byte[] key) {
        byte[] newState = new byte[BLOCK_SIZE];
        for(int i = 0; i < BLOCK_SIZE; i++){
            newState[i] = (byte)(state[i]^key[i]);
        }
        return newState;
    }

    private byte[] SubBytes(byte[] state){
        byte[] newState = new byte[BLOCK_SIZE];
        for(int i = 0; i < BLOCK_SIZE; i++){
            newState[i] = (byte) SBOX[state[i] & 255];
        }
        return newState;
    }

    private byte[] ShiftRows(byte[] state) {
        byte[] newState = new byte[BLOCK_SIZE];
        
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                newState[j * 4 + i] = state[((j + i) * 4 + i) % BLOCK_SIZE];
            }
        }
        
        return newState;
    }

    private byte[] MixColumns(byte[] state) {
        byte[] newState = new byte[BLOCK_SIZE];
        byte b2 = 2, b3 = 3;
        for (int i = 0; i < 4; i++) {
            int offset = i * 4;
            newState[offset]     = (byte) (galoismul(state[offset], b2) ^ galoismul(state[offset+1], b3) ^ state[offset+2] ^ state[offset+3]);
            newState[offset + 1] = (byte) (state[offset] ^ galoismul(state[offset+1], b2) ^ galoismul(state[offset+2], b3) ^ state[offset+3]);
            newState[offset + 2] = (byte) (state[offset] ^ state[offset+1] ^ galoismul(state[offset+2], b2) ^ galoismul(state[offset+3], b3));
            newState[offset + 3] = (byte) (galoismul(state[offset], b3) ^ state[offset+1] ^ state[offset+2] ^ galoismul(state[offset+3], b2));
        }

        return newState;
    }

    private byte[] DecryptBlock(byte[] state) {
        byte[][] keyExpanded = KeyExpansion(key);
        byte[] newState = AddRoundKey(state, keyExpanded[ROUNDS]);
        newState = InvShiftRows(newState);  
        newState = InvSubBytes(newState);

        for(int i = ROUNDS - 1; i > 0; i--) {
            newState = AddRoundKey(newState, keyExpanded[i]);
            newState = InvMixColumns(newState);
            newState = InvShiftRows(newState);
            newState = InvSubBytes(newState);
        }

        newState = AddRoundKey(newState, keyExpanded[0]);

        return newState;
    }

    private byte[] InvMixColumns(byte[] state) {
        byte[] newState = new byte[BLOCK_SIZE];
        byte b14 = 14, b9 = 9,  b11 = 11, b13 = 13;
        for (int i = 0; i < 4; i++) {
            int offset = i * 4;
            newState[offset]     = (byte) (galoismul(state[offset], b14) ^ galoismul(state[offset+1], b11) ^ galoismul(state[offset+2],b13) ^ galoismul(state[offset+3],b9));
            newState[offset + 1] = (byte) (galoismul(state[offset], b9) ^ galoismul(state[offset+1], b14) ^ galoismul(state[offset+2],b11) ^ galoismul(state[offset+3],b13));
            newState[offset + 2] = (byte) (galoismul(state[offset], b13) ^ galoismul(state[offset+1], b9) ^ galoismul(state[offset+2],b14) ^ galoismul(state[offset+3],b11));
            newState[offset + 3] = (byte) (galoismul(state[offset], b11) ^ galoismul(state[offset+1], b13) ^ galoismul(state[offset+2],b9) ^ galoismul(state[offset+3],b14));
        }
        return newState;
    }

    private byte[] InvShiftRows(byte[] state) {
        byte[] newState = new byte[BLOCK_SIZE];
        
        newState[0] = state[0];
        newState[4] = state[4];
        newState[8] = state[8];
        newState[12] = state[12];

        newState[1] = state[13];
        newState[5] = state[1];
        newState[9] = state[5];
        newState[13] = state[9];

        newState[2] = state[10];
        newState[6] = state[14];
        newState[10] = state[2];
        newState[14] = state[6];

        newState[3] = state[7];
        newState[7] = state[11];
        newState[11] = state[15];
        newState[15] = state[3];

        return newState;
    }

    private byte[] InvSubBytes(byte[] state) {
        byte[] newState = new byte[BLOCK_SIZE];
        for(int i = 0; i < BLOCK_SIZE; i++){
            newState[i] = InvSubByte(state[i]);
        }
        return newState;
    }

    private byte InvSubByte(byte b){
        return (byte) invSBOX[b & 255];
    }


    private byte galoismul(byte a, byte b) {
        byte result = 0;
        
        for (int i = 0; i < 8; i++){
            if ((b & (1 << i)) != 0) {
                result ^= a;
            }
            boolean MSB = (a & 0x80) != 0;
            a <<= 1;
            if(MSB) {
                a ^= 0x1B;
            }
        }
        
        return result;
    }
    
    private byte[] RotWord(byte[] word){
        return new byte[]{word[1], word[2], word[3], word[0]};
    }

    private byte[] SubWord(byte[] word){
        byte[] newWord = new byte[4];
        
        for (int i = 0; i < 4; i++) {
            newWord[i] = (byte) SBOX[word[i] & 255];
        }
        
        return newWord;
    }
    
    public byte[] EncryptText(byte[] plainText) {
        int blocks = (int) Math.ceil(plainText.length / 16f);
        System.out.println(blocks);
        byte[] state = new byte[BLOCK_SIZE  * blocks];
        byte[] result = new byte[BLOCK_SIZE  * blocks];
        
        System.arraycopy(plainText, 0, state, 0, plainText.length);
        
        for (int i = plainText.length; i < BLOCK_SIZE  * blocks; i++){
            state[i] = 0;
        }
        
        for (int i = 0; i < blocks; i++) {
            byte[] temp = new byte[BLOCK_SIZE];
            for (int j = 0; j < BLOCK_SIZE; j++) {
                temp[j] = state[i * BLOCK_SIZE + j];
            }
            temp = EncryptBlock(temp);
            System.arraycopy(temp, 0, result, i * BLOCK_SIZE, temp.length);
        }
        
        return result;
    }

    public byte[] DecryptText(byte[] cipherText) {
        int blocks = (int) Math.ceil(cipherText.length / 16f);
        System.out.println(blocks);
        byte[] state = new byte[BLOCK_SIZE  * blocks];
        byte[] result = new byte[BLOCK_SIZE  * blocks];

        System.arraycopy(cipherText, 0, state, 0, cipherText.length);

        for (int i = cipherText.length; i < BLOCK_SIZE  * blocks; i++){
            state[i] = 0;
        }

        for (int i = 0; i < blocks; i++) {
            byte[] temp = new byte[BLOCK_SIZE];
            for (int j = 0; j < BLOCK_SIZE; j++) {
                temp[j] = state[i * BLOCK_SIZE + j];
            }
            temp = DecryptBlock(temp);
            System.arraycopy(temp, 0, result, i * BLOCK_SIZE, temp.length);
        }
        
        return result;
    }
    private static final int[] SBOX = {
            0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
            0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
            0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
            0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
            0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
            0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
            0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
            0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
            0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
            0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
            0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
            0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
            0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
            0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
            0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
            0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };
    
    private static final int[] invSBOX = {
            0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
            0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
            0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
            0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
            0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
            0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
            0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
            0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
            0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
            0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
            0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
            0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
            0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
            0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
            0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
            0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d
    };
    
    private static final int[] RCON = {
            0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte)0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a 
    };
}
