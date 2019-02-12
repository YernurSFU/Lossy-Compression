package lossylzwcompression;


import java.util.List;

public class Block {

    List<List<Tuple>> colors;
    private static final int  MAX = 255;

    private static final double[][] QMAT = {
        {1 / (2 * Math.sqrt(2)), 1 / (2 * Math.sqrt(2)), 1 / (2 * Math.sqrt(2)), 1 / (2 * Math.sqrt(2)), 1 / (2 * Math.sqrt(2)), 1 / (2 * Math.sqrt(2)), 1 / (2 * Math.sqrt(2)), 1 / (2 * Math.sqrt(2))},
        {(0.5) * Math.cos(Math.PI / 16), (0.5) * Math.cos(3 * Math.PI / 16), (0.5) * Math.cos(5 * Math.PI / 16), (0.5) * Math.cos(7 * Math.PI / 16), (0.5) * Math.cos(9 * Math.PI / 16), (0.5) * Math.cos(11 * Math.PI / 16), (0.5) * Math.cos(13 * Math.PI / 16), (0.5) * Math.cos(15 * Math.PI / 16)},
        {(0.5) * Math.cos(2 * Math.PI / 16), (0.5) * Math.cos(6 * Math.PI / 16), (0.5) * Math.cos(10 * Math.PI / 16), (0.5) * Math.cos(14 * Math.PI / 16), (0.5) * Math.cos(18 * Math.PI / 16), (0.5) * Math.cos(22 * Math.PI / 16), (0.5) * Math.cos(26 * Math.PI / 16), (0.5) * Math.cos(30 * Math.PI / 16)},
        {(0.5) * Math.cos(3 * Math.PI / 16), (0.5) * Math.cos(9 * Math.PI / 16), (0.5) * Math.cos(15 * Math.PI / 16), (0.5) * Math.cos(21 * Math.PI / 16), (0.5) * Math.cos(27 * Math.PI / 16), (0.5) * Math.cos(33 * Math.PI / 16), (0.5) * Math.cos(39 * Math.PI / 16), (0.5) * Math.cos(45 * Math.PI / 16)},
        {(0.5) * Math.cos(4 * Math.PI / 16), (0.5) * Math.cos(12 * Math.PI / 16), (0.5) * Math.cos(20 * Math.PI / 16), (0.5) * Math.cos(28 * Math.PI / 16), (0.5) * Math.cos(36 * Math.PI / 16), (0.5) * Math.cos(44 * Math.PI / 16), (0.5) * Math.cos(52 * Math.PI / 16), (0.5) * Math.cos(60 * Math.PI / 16)},
        {(0.5) * Math.cos(5 * Math.PI / 16), (0.5) * Math.cos(15 * Math.PI / 16), (0.5) * Math.cos(25 * Math.PI / 16), (0.5) * Math.cos(35 * Math.PI / 16), (0.5) * Math.cos(45 * Math.PI / 16), (0.5) * Math.cos(55 * Math.PI / 16), (0.5) * Math.cos(65 * Math.PI / 16), (0.5) * Math.cos(75 * Math.PI / 16)},
        {(0.5) * Math.cos(6 * Math.PI / 16), (0.5) * Math.cos(18 * Math.PI / 16), (0.5) * Math.cos(30 * Math.PI / 16), (0.5) * Math.cos(42 * Math.PI / 16), (0.5) * Math.cos(54 * Math.PI / 16), (0.5) * Math.cos(66 * Math.PI / 16), (0.5) * Math.cos(78 * Math.PI / 16), (0.5) * Math.cos(90 * Math.PI / 16)},
        {(0.5) * Math.cos(7 * Math.PI / 16), (0.5) * Math.cos(21 * Math.PI / 16), (0.5) * Math.cos(35 * Math.PI / 16), (0.5) * Math.cos(49 * Math.PI / 16), (0.5) * Math.cos(63 * Math.PI / 16), (0.5) * Math.cos(77 * Math.PI / 16), (0.5) * Math.cos(91 * Math.PI / 16), (0.5) * Math.cos(105 * Math.PI / 16)}
    };

    private static final double[][] DCT_MAT_TRANS = {
        {1 / (2 * Math.sqrt(2)), (0.5) * Math.cos(Math.PI / 16), (0.5) * Math.cos(2 * Math.PI / 16), (0.5) * Math.cos(3 * Math.PI / 16), (0.5) * Math.cos(4 * Math.PI / 16), (0.5) * Math.cos(5 * Math.PI / 16), (0.5) * Math.cos(6 * Math.PI / 16), (0.5) * Math.cos(7 * Math.PI / 16)},
        {1 / (2 * Math.sqrt(2)), (0.5) * Math.cos(3 * Math.PI / 16), (0.5) * Math.cos(6 * Math.PI / 16), (0.5) * Math.cos(9 * Math.PI / 16), (0.5) * Math.cos(12 * Math.PI / 16), (0.5) * Math.cos(15 * Math.PI / 16), (0.5) * Math.cos(18 * Math.PI / 16), (0.5) * Math.cos(21 * Math.PI / 16)},
        {1 / (2 * Math.sqrt(2)), (0.5) * Math.cos(5 * Math.PI / 16), (0.5) * Math.cos(10 * Math.PI / 16), (0.5) * Math.cos(15 * Math.PI / 16), (0.5) * Math.cos(20 * Math.PI / 16), (0.5) * Math.cos(25 * Math.PI / 16), (0.5) * Math.cos(30 * Math.PI / 16), (0.5) * Math.cos(35 * Math.PI / 16)},
        {1 / (2 * Math.sqrt(2)), (0.5) * Math.cos(7 * Math.PI / 16), (0.5) * Math.cos(14 * Math.PI / 16), (0.5) * Math.cos(21 * Math.PI / 16), (0.5) * Math.cos(28 * Math.PI / 16), (0.5) * Math.cos(35 * Math.PI / 16), (0.5) * Math.cos(42 * Math.PI / 16), (0.5) * Math.cos(49 * Math.PI / 16)},
        {1 / (2 * Math.sqrt(2)), (0.5) * Math.cos(9 * Math.PI / 16), (0.5) * Math.cos(18 * Math.PI / 16), (0.5) * Math.cos(27 * Math.PI / 16), (0.5) * Math.cos(36 * Math.PI / 16), (0.5) * Math.cos(45 * Math.PI / 16), (0.5) * Math.cos(54 * Math.PI / 16), (0.5) * Math.cos(63 * Math.PI / 16)},
        {1 / (2 * Math.sqrt(2)), (0.5) * Math.cos(11 * Math.PI / 16), (0.5) * Math.cos(22 * Math.PI / 16), (0.5) * Math.cos(33 * Math.PI / 16), (0.5) * Math.cos(44 * Math.PI / 16), (0.5) * Math.cos(55 * Math.PI / 16), (0.5) * Math.cos(66 * Math.PI / 16), (0.5) * Math.cos(77 * Math.PI / 16)},
        {1 / (2 * Math.sqrt(2)), (0.5) * Math.cos(13 * Math.PI / 16), (0.5) * Math.cos(26 * Math.PI / 16), (0.5) * Math.cos(39 * Math.PI / 16), (0.5) * Math.cos(52 * Math.PI / 16), (0.5) * Math.cos(65 * Math.PI / 16), (0.5) * Math.cos(78 * Math.PI / 16), (0.5) * Math.cos(91 * Math.PI / 16)},
        {1 / (2 * Math.sqrt(2)), (0.5) * Math.cos(15 * Math.PI / 16), (0.5) * Math.cos(30 * Math.PI / 16), (0.5) * Math.cos(45 * Math.PI / 16), (0.5) * Math.cos(60 * Math.PI / 16), (0.5) * Math.cos(75 * Math.PI / 16), (0.5) * Math.cos(90 * Math.PI / 16), (0.5) * Math.cos(105 * Math.PI / 16),}
    };

    private static final double[][] Z_MAT = {
        {16, 11, 10, 16, 24, 40, 51, 61},
        {12, 12, 14, 19, 26, 58, 60, 55},
        {14, 13, 16, 24, 40, 57, 69, 56},
        {14, 17, 22, 29, 51, 87, 80, 62},
        {18, 22, 37, 56, 68, 109, 103, 77},
        {24, 35, 55, 64, 81, 104, 113, 92},
        {49, 64, 78, 87, 103, 121, 120, 101},
        {72, 92, 95, 98, 112, 100, 103, 99}
    };

    public Block(List<List<Tuple>> items) {
        this.colors = items;
    }

    public Tuple getColor(int x, int y) {
        return colors.get(y).get(x);
    }

    public void addIntensity(int num) {
        for (int i = 0; i < colors.size(); i++) {
            for (int j = 0; j < colors.get(0).size(); j++) {
                List<Tuple> toModifyRow = colors.get(i);
                int newLuma = (int) toModifyRow.get(j).getElement(0) + num;
                if (newLuma > MAX) {
                    newLuma = MAX;
                } else if (newLuma < 0) {
                    newLuma = 0;
                }
                Tuple toModifyItem = new Tuple(newLuma, (int) toModifyRow.get(j).getElement(1), (int) toModifyRow.get(j).getElement(2));
                toModifyRow.set(j, toModifyItem);
                colors.set(i, toModifyRow);

            }
        }
    }

    // Chaneg this mafaka!
    public static double[][] multiplicar(double[][] A, double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                for (int k = 0; k < aColumns; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }

    public double[][] applyDCT() {
        return multiplicar(multiplicar(QMAT, getIntensities()), DCT_MAT_TRANS);
    }

    public double[][] reverseDCT(double[][] appliedDCT) {
        return multiplicar(multiplicar(DCT_MAT_TRANS, appliedDCT), QMAT);
    }

    public double[][] quanitize(double[][] appliedDCT) {
        double[][] newMat = appliedDCT.clone();
        for (int i = 0; i < newMat.length; i++) {
            for (int j = 0; j < newMat[i].length; j++) {
                double original = newMat[i][j];
                newMat[i][j] = (int) (newMat[i][j] / Z_MAT[i][j]) * Z_MAT[i][j];
            }
        }
        return newMat;
    }

    public void setIntensities(double[][] intensities) {
        for (int i = 0; i < colors.size(); i++) {
            for (int j = 0; j < colors.get(i).size(); j++) {
                List<Tuple> toSetRow = colors.get(i);
                int currentIntensity = (int) intensities[i][j];
                Tuple toSetItem = new Tuple(currentIntensity, toSetRow.get(j).getElement(1), toSetRow.get(j).getElement(2));
                toSetRow.set(j, toSetItem);
                colors.set(i, toSetRow);
            }
        }
    }

    public int getWidth() {
        try {
            return colors.get(0).size();
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    public int getHeight() {
        return colors.size();
    }

    private double[][] getIntensities() {
        double[][] toReturn = new double[colors.size()][colors.get(0).size()];

        for (int i = 0; i < toReturn.length; i++) {
            for (int j = 0; j < toReturn[0].length; j++) {
                toReturn[i][j] = (double) ((Integer) colors.get(i).get(j).getElement(0));
            }
        }

        return toReturn;
    }

    @Override
    public String toString() {
        String output = "ImagePart[\n";
        for (int i = 0; i < colors.size(); i++) {
            if (i > 0) {
                output += "\n ";
            } else {
                output += " ";
            }
            for (int j = 0; j < colors.get(i).size(); j++) {
                if (j > 0) {
                    output += ",";
                }
                output += colors.get(i).get(j).toString();
            }
            if (i < colors.size() - 1) {
                output += ",";
            }
        }
        output += "\n]";
        return output;
    }
}
