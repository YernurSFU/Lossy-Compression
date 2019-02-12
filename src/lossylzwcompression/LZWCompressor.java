package lossylzwcompression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javafx.util.Pair;

public class LZWCompressor {

    private final List<Byte> buffer;

    public LZWCompressor() {
        buffer = new ArrayList<>();
    }

    public List<Byte> getSamples() {
        return buffer;
    }

    public void clearSamples() {
        buffer.clear();
    }

    public void addSample(byte sample) {
        buffer.add(sample);
    }

    public void addSamples(byte[] samples) {
        for (byte sample : samples) {
            this.buffer.add(sample);
        }
    }

    public void addSamples(List<Byte> samples) {
        samples.forEach((sample) -> {
            this.buffer.add(sample);
        });
    }

    public void setSamples(byte[] samples) {
        clearSamples();
        addSamples(samples);
    }

    public void setSamples(List<Byte> samples) {
        clearSamples();
        addSamples(samples);
    }

    public Map<String, Integer> getDic() {
        Map<String, Integer> toReturn = new ConcurrentHashMap<>();
        for (int i = 0; i < 256; i++) {
            toReturn.put(Integer.toString(i), i);
        }
        return toReturn;
    }

    BitWord buildDic(Map<Integer, String> dictionary) {
        BitWord toReturn = new BitWord();

        BitWord dictSize = new BitWord();
        String dictSizeAsBits = Integer.toBinaryString(dictionary.size());
        for (int i = 0; i < dictSizeAsBits.length(); i += Byte.SIZE) {
            int j;
            int bitsSet = 0;
            for (j = i; j < dictSizeAsBits.length() && j < Byte.SIZE; j++) {
                dictSize.add(dictSizeAsBits.charAt(j) == '1');
                bitsSet++;
            }
            while (bitsSet != Byte.SIZE) {
                dictSize.add(false);
                bitsSet++;
            }
            if (j < dictSizeAsBits.length() - 1) {
                dictSize.add(true);
            } else {
                dictSize.add(false);
            }
        }

        return toReturn;
    }

    public byte[] compress() {
        Map<String, Integer> codeWordsDictionary = getDic();
        String p = "";
        BitWord finalOutput = new BitWord();
        for (int i = 0; i < buffer.size(); i++) {
            String c = Integer.toUnsignedString(buffer.get(i) & 0xFF);
            String currentCodeword;
            if (!("".equals(p))) {
                currentCodeword = p + "|" + c;
            } else {
                currentCodeword = c;
            }
            if (codeWordsDictionary.containsKey(currentCodeword)) {
                p = currentCodeword;
            } else {
                finalOutput.add(BitWord.numToBytes(codeWordsDictionary.get(p)));
                codeWordsDictionary.put(currentCodeword, (int) codeWordsDictionary.size());
                p = c;
            }
        }
        if (!buffer.isEmpty()) {
            finalOutput.add(BitWord.numToBytes(codeWordsDictionary.get(p)));
        }
        return finalOutput.toByteArray();
    }

    private void output(String word) {
        String[] temp = word.split("\\|");
        for (String part : temp) {
            addSample((byte) Integer.parseInt(part));
        }
    }

    public List<Byte> decompress(byte[] compressionResultsAsBytes) {
        Map<Integer, String> decodeDic = getDic().entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        BitWord data = new BitWord(compressionResultsAsBytes);
        if (data.size() != 0) {
            clearSamples();
            decompressHelper(decodeDic, data);
            return getSamples();
        }
        return null;
    }

    void decompressHelper(Map<Integer, String> dic, BitWord data) {
        Pair<Integer, Integer> cW = BitWord.bytesToNum(data, 0);
        output(dic.get(cW.getKey()));
        String pW = dic.get(cW.getKey());
        while (cW.getValue() < data.size()) {
            cW = BitWord.bytesToNum(data, cW.getValue());
            String entry;
            if (dic.containsKey(cW.getKey())) {
                entry = dic.get(cW.getKey());
            } else {
                entry = pW + "|" + pW.split("\\|")[0];
            }
            output(entry);
            dic.put(dic.size(), pW + "|" + entry.split("\\|")[0]);
            pW = entry;
        }
    }
}
