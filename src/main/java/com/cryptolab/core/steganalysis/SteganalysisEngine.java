package com.cryptolab.core.steganalysis;

import java.awt.image.BufferedImage;

/**
 * Educational Steganalysis Engine for detecting potential hidden payloads.
 * Provides LSB bit-plane visual filtering and statistical parity anomaly estimation.
 *
 * Developed by Abderrazak BENAROUS
 */
public class SteganalysisEngine {

    public enum Channel {
        RED(16),
        GREEN(8),
        BLUE(0);

        private final int shift;

        Channel(int shift) {
            this.shift = shift;
        }

        public int getShift() {
            return shift;
        }
    }

    public record SteganalysisResult(
            BufferedImage bitPlaneImage,
            double anomalyScore,
            String assessment,
            int totalEvaluatedPixels,
            double lsbZeroRatio
    ) {}

    public static BufferedImage extractBitPlane(BufferedImage image, Channel channel, int bitPosition) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage bitPlane = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int mask = 1 << bitPosition;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int channelVal = (rgb >> channel.getShift()) & 0xFF;
                int bit = (channelVal & mask) != 0 ? 255 : 0;
                int bwColor = (255 << 24) | (bit << 16) | (bit << 8) | bit;
                bitPlane.setRGB(x, y, bwColor);
            }
        }

        return bitPlane;
    }

    public static SteganalysisResult evaluateAnomaly(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixels = width * height;

        int lsbZeros = 0;
        int lsbOnes = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                // Check Red LSB
                int rBit = ((rgb >> 16) & 0x01);
                if (rBit == 0) lsbZeros++;
                else lsbOnes++;
            }
        }

        double ratioZeros = (double) lsbZeros / (double) totalPixels;
        // In natural images, LSB distribution can have subtle natural bias; random stego bits flatten to ~0.500
        double devFromEven = Math.abs(ratioZeros - 0.50);

        String assessment;
        double anomalyScore;

        if (devFromEven < 0.015) {
            anomalyScore = 75.0;
            assessment = "SUSPICIOUS: LSB distribution is unusually uniform (~50.0% parity), characteristic of encrypted or compressed hidden payload.";
        } else if (devFromEven < 0.05) {
            anomalyScore = 40.0;
            assessment = "MODERATE / INCONCLUSIVE: Natural photo texture or light embedding.";
        } else {
            anomalyScore = 10.0;
            assessment = "NORMAL: Natural image characteristics observed. No obvious high-density LSB anomaly detected.";
        }

        BufferedImage samplePlane = extractBitPlane(image, Channel.RED, 0);

        return new SteganalysisResult(samplePlane, anomalyScore, assessment, totalPixels, ratioZeros);
    }
}
