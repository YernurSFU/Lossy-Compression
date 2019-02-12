/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lossylzwcompression;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author yernurnursultanov
 */
public class BMPEditor extends javax.swing.JFrame {

    private static final int[] HISTOGRAM_ADDRESS_OFFSET = {13, 12, 11, 10};
    private static final int[] HISTOGRAM_ADDRESS_WIDTH = {21, 20, 19, 18};
    private static final int[] HISTOGRAM_ADDRESS_HEIGHT = {25, 24, 23, 22};
    private static final int[] HISTOGRAM_ADDRESS_IMPORTANT_COLORS = {49, 48, 47, 46};
    private static final int[] HISTOGRAM_ADDRESS_BBP = {29, 28};
    private static final int BMP_FILE_ADDRESS_DATA = 50;
    private static final int MASK = 0xff;
    private static boolean isLossy;
    private static byte[] compressed;
    private static String filepath;

    public BMPEditor(String _filepath, boolean _isLossy, boolean _isEncoding) throws IOException {
        initComponents();
        filepath = _filepath;
        isLossy = _isLossy;

        imgBuffer = Files.readAllBytes(Paths.get(filepath));
        this.setPreferredSize(new Dimension(6400, 480));
        this.setLocationRelativeTo(null);
        if (_isEncoding) {
            img = getCompressedImage();
            setOriginal();
        } else {
            img = getOriginaldImage();
        }
        nextHelper();
        this.setVisible(true);
    }

    private BufferedImage getBMPBuffer(byte[] data) {
        int idx, size;
        long threshold;
        int bmp_offset = getInt(data[HISTOGRAM_ADDRESS_OFFSET[0]],
                data[HISTOGRAM_ADDRESS_OFFSET[1]],
                data[HISTOGRAM_ADDRESS_OFFSET[2]],
                data[HISTOGRAM_ADDRESS_OFFSET[3]]);
        int width = getInt(data[HISTOGRAM_ADDRESS_WIDTH[0]],
                data[HISTOGRAM_ADDRESS_WIDTH[1]],
                data[HISTOGRAM_ADDRESS_WIDTH[2]],
                data[HISTOGRAM_ADDRESS_WIDTH[3]]);
        int height = getInt(data[HISTOGRAM_ADDRESS_HEIGHT[0]],
                data[HISTOGRAM_ADDRESS_HEIGHT[1]],
                data[HISTOGRAM_ADDRESS_HEIGHT[2]],
                data[HISTOGRAM_ADDRESS_HEIGHT[3]]);
        BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = ret.getGraphics();

        int importantColors = getInt(data[HISTOGRAM_ADDRESS_IMPORTANT_COLORS[0]],
                data[HISTOGRAM_ADDRESS_IMPORTANT_COLORS[1]],
                data[HISTOGRAM_ADDRESS_IMPORTANT_COLORS[2]],
                data[HISTOGRAM_ADDRESS_IMPORTANT_COLORS[3]]);

        short bpp = getShort(data[HISTOGRAM_ADDRESS_BBP[0]],
                data[HISTOGRAM_ADDRESS_BBP[1]]);

        if (importantColors <= 0 && bpp < 16) {
            importantColors = 1 << bpp;
        }

        idx = importantColors * 4 + BMP_FILE_ADDRESS_DATA;
        threshold = bmp_offset - idx;
        size = ((width * bpp + 31) / 32) * 4;
        idx += threshold;

        int raw_offset = 0;
        int offset = (height - 1) * width;
        byte[] bmp_data = new byte[size * height];
        for (int i = height - 1; i >= 0; i--) {
            for (int j = raw_offset; j < (raw_offset + size) && idx < data.length; j++) {
                bmp_data[j] = data[idx];
                idx++;
            }

            int temp_offset = offset;
            int temp_raw = raw_offset;
            for (int l = 0; l < width; l++) {
                int val = 0xff000000
                        | (int) (bmp_data[temp_raw++] & MASK)
                        | (int) (bmp_data[temp_raw++] & MASK) << 8
                        | (int) (bmp_data[temp_raw++] & MASK) << 16;
                Color clr = new Color(val);
                g.setColor(clr);
                g.drawLine(l, i, l, i);
                temp_offset++;
            }
            raw_offset += size;
            offset -= width;
        }

        g.dispose();
        return ret;
    }

    private int getInt(byte b1, byte b2, byte b3, byte b4) {
        return ((0xFF & b1) << 24) | ((0xFF & b2) << 16) | ((0xFF & b3) << 8) | (0xFF & b4);
    }

    private short getShort(byte b1, byte b2) {
        return (short) ((b1 << 8) + b2);
    }

    private void nextHelper() throws IOException {
        fileName.setText(this.imgName);
        ((BMPEditor.PanelBMP) drawPanel).setImage(this.img);
    }

    private void setOriginal() {
        this.img = getBMPBuffer(imgBuffer);
        this.imgName = "Original Image";
    }

    private BufferedImage getCompressedImage() throws IOException {
        byte[] file;
        if (isLossy) {
            file = ImageCompression.lossyCompress(getBMPBuffer(this.imgBuffer));
        } else {
            file = ImageCompression.compressToBytes(getBMPBuffer(this.imgBuffer));
        }
        BufferedImage decompressed = ImageCompression.decompress(file);
        setLZWRatio(file.length, this.imgBuffer.length);
        saveButton.setVisible(true);
        this.imgName = "Compressed Image";
        return decompressed;

    }

    private BufferedImage getOriginaldImage() throws IOException {
        BufferedImage decompressed = ImageCompression.decompress(this.imgBuffer);
        saveButton.setVisible(false);
        nextButton.setVisible(false);
        lzwLabel.setVisible(false);
        lzwVal.setVisible(false);
        return decompressed;

    }

    private void setLZWRatio(double cmpr, double org) {
        double lzwRatio = (double) cmpr / (double) org;
        lzwVal.setText(String.format("%.3f", lzwRatio));
        lzwLabel.setVisible(true);
        lzwVal.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        drawPanel = new PanelBMP();
        fileName = new javax.swing.JLabel();
        nextButton = new javax.swing.JButton();
        lzwLabel = new javax.swing.JLabel();
        lzwVal = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BMP Editor");

        javax.swing.GroupLayout drawPanelLayout = new javax.swing.GroupLayout(drawPanel);
        drawPanel.setLayout(drawPanelLayout);
        drawPanelLayout.setHorizontalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 595, Short.MAX_VALUE)
        );
        drawPanelLayout.setVerticalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );

        fileName.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        fileName.setText("fileName");

        nextButton.setText("Pro");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        lzwLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 11)); // NOI18N
        lzwLabel.setText("LZW coding compression ratio");

        lzwVal.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        lzwVal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lzwVal.setText("[value]");

        saveButton.setText("save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(drawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nextButton)
                            .addComponent(saveButton))
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fileName)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lzwVal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lzwLabel)))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton)
                    .addComponent(fileName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lzwLabel)
                    .addComponent(lzwVal)
                    .addComponent(saveButton))
                .addGap(18, 18, 18)
                .addComponent(drawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        try {
            nextHelper();
        } catch (IOException ex) {
            Logger.getLogger(BMPEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        try (FileOutputStream stream = new FileOutputStream(filepath.substring(0, filepath.length() - 4) + (isLossy ? ".in3" : ".im3"))) {
            stream.write(compressed);
        } catch (IOException ex) {

        }

    }//GEN-LAST:event_saveButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawPanel;
    private javax.swing.JLabel fileName;
    private javax.swing.JLabel lzwLabel;
    private javax.swing.JLabel lzwVal;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    private BufferedImage img;
    private String imgName;

    private final byte[] imgBuffer;

    public class PanelBMP extends JPanel {

        BufferedImage img = null;

        public PanelBMP() {
        }

        @Override
        public void paint(Graphics g) {
            super.paintComponent(g);
            if (img == null) {
                return;
            }

            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    g.setColor(new Color(img.getRGB(i, j)));
                    g.drawLine(i, j, i, j);
                }
            }
        }

        public void setImage(BufferedImage img) {
            this.img = img;
            repaint();
        }
    }
}
