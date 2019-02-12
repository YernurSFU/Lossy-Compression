package lossylzwcompression;

import java.util.ArrayList;
import java.util.List;

public class Blocks {

    private final List<List<Block>> imageParts;
    private static int BLOCK_SIZE = 8;

    private Blocks(List<List<Block>> imageParts) {
        this.imageParts = imageParts;
    }

    public static Blocks createImageBlocks(Tuple[][] qMAT) {
        List<List<Block>> imageParts = new ArrayList<>();
        for (int i = 0; i < qMAT.length; i += 8) {
            List<Block> wBlock = new ArrayList<>();
            for (int j = 0; j < qMAT[0].length; j += 8) {
                List<List<Tuple>> imagePartMat = new ArrayList<>();
                
                for (int k = i; k < (i + 8); k++) {
                    List<Tuple> temp = new ArrayList<>();
                    for (int z = j; z < (j + 8); z++) {
                        try {
                            temp.add(qMAT[k][z]);
                        } catch (IndexOutOfBoundsException ex) {
                            temp.add(new Tuple(0, 0, 0));
                        }
                    }
                    if (temp.size() > 0) {
                        imagePartMat.add(temp);
                    }
                }
                if (imagePartMat.size() > 0) {
                    wBlock.add(new Block(imagePartMat));
                }
            }
            imageParts.add(wBlock);
        }

        return new Blocks(imageParts);
    }

    public List<List<Block>> getImageParts() {
        return imageParts;
    }

    public Block getImagePart(int x, int y) {
        return imageParts.get(y).get(x);
    }

    public void addIntensity(int num) {
        for (int i = 0; i < imageParts.size(); i++) {
            for (int j = 0; j < imageParts.get(0).size(); j++) {
                imageParts.get(i).get(j).addIntensity(num);
            }
        }
    }

    @Override
    public String toString() {
        String output = "ImageParts:[\n";
        for (int i = 0; i < imageParts.size(); i++) {
            if (i > 0) {
                output += "\n\n";
            }
            output += "Level(" + i + ")[\n";
            for (int j = 0; j < imageParts.get(i).size(); j++) {
                if (j > 0) {
                    output += ",\n";
                }
                output += imageParts.get(i).get(j).toString();
            }
            output += "\n]\n";
        }
        output += "]";
        return output;
    }
}
