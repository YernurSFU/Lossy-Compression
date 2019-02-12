package lossylzwcompression;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;

public final class BitWord {

    private final List<Boolean> word;
    private final static int BYTE_SIZE = 8;

    public BitWord() {
        word = new ArrayList<>();
    }

    public BitWord(int n) {
        word = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            word.add(false);
        }
    }

    public BitWord(byte[] bits) {
        word = new ArrayList<>();
        add(bits);
    }
    
    public BitWord(List<Byte> bits) {
        word = new ArrayList<>();
        add(bits);
    }

    public BitWord add(boolean bit) {
        word.add(bit);
        return this;
    }

    public BitWord add(BitWord bits) {
        for (int i = 0; i < bits.size(); i++) {
            add(bits.get(i));
        }
        return this;
    }

    public BitWord add(byte[] bits) {
        for (int i = 0; i < (bits.length * BYTE_SIZE); i++) {
            int posByte = i / BYTE_SIZE;
            int posBit = i % BYTE_SIZE;
            byte valByte = bits[posByte];
            int valInt = valByte >> (BYTE_SIZE - (posBit + 1)) & 0x0001;
            word.add(valInt != 0);
        }
        return this;
    }
    
    public BitWord add(List<Byte> bits) {
        for (int i = 0; i < (bits.size() * BYTE_SIZE); i++) {
            int posByte = i / BYTE_SIZE;
            int posBit = i % BYTE_SIZE;
            byte valByte = bits.get(posByte);
            int valInt = valByte >> (BYTE_SIZE - (posBit + 1)) & 0x0001;
            word.add(valInt != 0);
        }
        return this;
    }
    
    public BitWord add(Byte[] bits) {
        for (int i = 0; i < (bits.length * BYTE_SIZE); i++) {
            int posByte = i / BYTE_SIZE;
            int posBit = i % BYTE_SIZE;
            byte valByte = bits[posByte];
            int valInt = valByte >> (BYTE_SIZE - (posBit + 1)) & 0x0001;
            word.add(valInt != 0);
        }
        return this;
    }

    public boolean get(int idx) {
        return word.get(idx);
    }

    public int size() {
        return word.size();
    }

    public BitWord set(int idx, boolean val) {
        word.set(idx, val);
        return this;
    }

    public BitWord clear() {
        word.clear();
        return this;
    }

    public byte[] toByteArray() {
        if (size() == 0) {
            return new byte[0];
        }
        int bytes = this.word.size()/ BYTE_SIZE;
        byte[] toReturn = new byte[bytes];
        Arrays.fill(toReturn, (byte) 0);
        for (int i = 0; i < word.size(); i++) {
            byte oldByte = toReturn[i / BYTE_SIZE];
            oldByte = (byte) (((0xFF7F >> i % BYTE_SIZE) & oldByte) & 0x00FF);
            byte newByte = (byte) (((word.get(i) == true ? 1 : 0) << (BYTE_SIZE - (i % BYTE_SIZE + 1))) | oldByte);
            toReturn[i / BYTE_SIZE] = newByte;
        }
        return toReturn;
    }

    public String toByteString() {
        byte[] byteArray = toByteArray();
        String newString = "";
        for (byte b : byteArray) {
            if (!newString.equals("")) {
                newString += " ";
            }
            newString += Integer.toBinaryString(b & 255 | 256).substring(1);
        }
        return newString;
    }

    public String toBitString() {
        String newString = "";
        newString = word.stream().map((bit) -> bit ? "1" : "0").reduce(newString, String::concat);
        return newString;
    }

    public static BitWord numToBytes(int num) {
        num = Math.abs(num);
        String valAsString = Integer.toBinaryString(num);
        int zerosToAdd = 6 - (valAsString.length() % 7);
        for (int i = 0; i < zerosToAdd; i++) {
            valAsString = '0' + valAsString;
        }
        
        BitWord toReturn = new BitWord();
        BitWord current = new BitWord(BYTE_SIZE);
        current.word.set(6, num < 0);
        int currentStringIdx = 0;
        boolean first = true;
        while (true) {
            for (int i = 0; i < (first ? 6 : 7) && currentStringIdx < valAsString.length(); i++) {
                current.word.set(i, valAsString.charAt(currentStringIdx) == '1');
                currentStringIdx++;
            }
            boolean willEnd = false;
            if (currentStringIdx < valAsString.length()) {
                current.word.set(7, false); 
            } else {
                current.word.set(7, true); 
                willEnd = true;
            }
            
            if (first) {
                first = false;
            }
            toReturn.add(current);
            current = new BitWord(BYTE_SIZE);

            if (willEnd) {
                break;
            }
        }
        return new BitWord(toReturn.toByteArray());
    }

    public static Pair<Integer, Integer> bytesToNum(BitWord val, int _idx){
        boolean first = true;
        boolean isNegative = false;
        int toReturn;
        int idx;
        String toReturnAsUnsignedString = "";
        for (idx = _idx; idx < val.size(); idx += BYTE_SIZE) {
            String current = "";
            for (int j = idx; j < (first ? (idx + 6) : (idx + 7)) && j < val.size(); j++) {
                current += val.get(j) ? '1' : '0';
                isNegative = val.get(idx + 6);
            }
            if (val.get(idx + 7)) {
                idx += BYTE_SIZE;
                toReturnAsUnsignedString += current;
                break;
            } else {
                toReturnAsUnsignedString += current;
            }
            if (first) {
                first = false;
            }
        }
        toReturn = Integer.parseInt(toReturnAsUnsignedString, 2);
        if (isNegative) {
            toReturn = toReturn * -1;
        }
        System.out.println("toReturn = "+ toReturn + "| idx = " + idx );
        return new Pair<>(toReturn, idx);
    }
}
