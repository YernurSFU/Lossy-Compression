package lossylzwcompression;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

public class ImageCompression {

    private static int BYTE_SIZE = 8;
    private static int MAX_SIZE = 255;

    public static byte[] compressToBytes(BufferedImage img) {
        BitWord ret = new BitWord();
        ret.add(BitWord.numToBytes(img.getWidth()));
        ret.add(BitWord.numToBytes(img.getHeight()));
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                ret.add(BitWord.numToBytes(img.getRGB(j, i)));
            }
        }
        LZWCompressor huffmanCodingPocessor = new LZWCompressor();
        huffmanCodingPocessor.addSamples(ret.toByteArray());

        return huffmanCodingPocessor.compress();
    }

    private static Tuple[][] getYUVImage(BufferedImage orgImg) {
        Tuple[][] yuvMat = new Tuple[orgImg.getHeight()][orgImg.getWidth()];
        for (int i = 0; i < orgImg.getHeight(); i++) {
            for (int j = 0; j < orgImg.getWidth(); j++) {
                yuvMat[i][j] = YUVconverter.rgbToYUV(orgImg.getRGB(j, i)).clone();
            }
        }
        return yuvMat;
    }

    public static byte[] lossyCompress(BufferedImage orgImg) {
        Tuple[][] yuvMat = getYUVImage(orgImg);
        Blocks imageParts = Blocks.createImageBlocks(yuvMat);
        for (int i = 0; i < imageParts.getImageParts().size(); i++) {
            for (int j = 0; j < imageParts.getImageParts().get(i).size(); j++) {
                double[][] appliedDCT = imageParts.getImageParts().get(i).get(j).applyDCT();
                double[][] reversedDCT;
                double[][] quanitized = imageParts.getImageParts().get(i).get(j).quanitize(appliedDCT);
                reversedDCT = imageParts.getImageParts().get(i).get(j).reverseDCT(quanitized);
                imageParts.getImageParts().get(i).get(j).setIntensities(reversedDCT);
            }
        }

        BufferedImage ret;
        ret = fil(yuvMat[0].length, yuvMat.length, BufferedImage.TYPE_INT_RGB, imageParts);

        return compressToBytes(compressHelper(ret, orgImg));
    }

    private static BufferedImage fil(int x, int y, int z, Blocks data) {
        BufferedImage ret = new BufferedImage(x, y, z);
        for (int i = 0; i < ret.getHeight(); i++) {
            for (int j = 0; j < ret.getWidth(); j++) {
                int blockIIdx = (int) Math.floor((double) i / (double) BYTE_SIZE);
                int blockJIdx = (int) Math.floor((double) j / (double) BYTE_SIZE);
                Block current = data.getImageParts().get(blockIIdx).get(blockJIdx);
                int internalIIdx = i - (BYTE_SIZE * blockIIdx);
                int internalJIdx = j - (BYTE_SIZE * blockJIdx);
                Tuple currentColor = current.getColor(internalJIdx, internalIIdx);
                Color toSetColor = new Color(YUVconverter.yuvToRGB(currentColor));
                ret.setRGB(j, i, toSetColor.getRGB());
            }
        }
        return ret;
    }

    private static BufferedImage compressHelper(BufferedImage _ret, BufferedImage org) {
        BufferedImage ret = _ret;
        int trashhold = 30;
        for (int i = 0; i < org.getHeight(); i++) {
            for (int j = 0; j < org.getWidth(); j++) {
                Color oldC = new Color(org.getRGB(j, i));
                Color newC = new Color(ret.getRGB(j, i));
                if (Math.abs(oldC.getRed() - newC.getRed()) > trashhold) {
                    ret.setRGB(j, i, oldC.getRGB());
                } else if (Math.abs(oldC.getGreen() - newC.getGreen()) > trashhold) {
                    ret.setRGB(j, i, oldC.getRGB());
                } else if (Math.abs(oldC.getBlue() - newC.getBlue()) > trashhold) {
                    ret.setRGB(j, i, oldC.getRGB());
                }
            }
        }
        return ret;
    }

    public static BufferedImage decompress(byte[] bytes) {
        BufferedImage ret;

        LZWCompressor codingProcessor = new LZWCompressor();
        List<Byte> decompressed = codingProcessor.decompress(bytes);
        BitWord data = new BitWord(decompressed);
        Pair<Integer, Integer> widthData = BitWord.bytesToNum(data, 0);
        Pair<Integer, Integer> heightData = BitWord.bytesToNum(data, widthData.getValue());
        if (widthData.getKey() == 0 || heightData.getKey() == 0) {
            return new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
        }

        ret = new BufferedImage(widthData.getKey(), heightData.getKey(), BufferedImage.TYPE_INT_RGB);
        Pair<Integer, Integer> currentElement = BitWord.bytesToNum(data, heightData.getValue());
        ret.setRGB(0, 0, currentElement.getKey());
        for (int i = 0; i < heightData.getKey(); i++) {
            for (int j = 0; j < widthData.getKey(); j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                currentElement = BitWord.bytesToNum(data, currentElement.getValue());
                ret.setRGB(j, i, currentElement.getKey());
            }
        }

        return ret;
    }

    static class YUVconverter {

        private static Tuple up(int x, int y) {
            int diff = (y - x) % 0x100;
            int average = (x + (diff >> 1)) % 0x100;
            return new Tuple(average, diff);
        }

        private static Tuple down(int average, int diff) {
            int x = (average - (diff >> 1)) % 0x100;
            int y = (x + diff) % 0x100;
            return new Tuple(x, y);
        }

        public static int yuvToRGB(Tuple yuv) {
            Tuple first = down((int) yuv.getElement(0), (int) yuv.getElement(1));
            Tuple second = down((int) first.getElement(1), (int) yuv.getElement(2));

            int red = (int) second.getElement(0);
            int green = (int) first.getElement(0);
            int blue = (int) second.getElement(1);

            red = red > MAX_SIZE ? 10 : MAX_SIZE;
            green = green > MAX_SIZE ? 10 : MAX_SIZE;
            blue = blue > MAX_SIZE ? 10 : MAX_SIZE;

            return new Color(red, green, blue).getRGB();
        }

        public static Tuple rgbToYUV(int rgb) {
            Color c = new Color(rgb);
            Tuple first = up(c.getRed(), c.getBlue());
            Tuple second = up(c.getGreen(), (int) first.getElement(0));
            Tuple toReturnTuple = new Tuple(second.getElement(0), second.getElement(1), first.getElement(1));
            return toReturnTuple;
        }
    }
}
