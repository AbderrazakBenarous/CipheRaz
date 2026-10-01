package com.cryptolab.core.steganography;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * High-performance, educational LSB (Least Significant Bit) Image Steganography Engine.
 * Supports configurable bits-per-channel (1 to 4 bits), bit-level pixel tracing,
 * MSE/PSNR image fidelity calculation, and difference map visualization.
 *
 * Developed by Abderrazak BENAROUS
 */
public class LsbStegoEngine {

    public record StegoResult(
            BufferedImage stegoImage,
            BufferedImage differenceMap,
            int payloadBytes,
            int maxCapacityBytes,
            double capacityUsedPercentage,
            int changedPixelsCount,
            double changedPixelsPercentage,
            double mse,
            double psnrDb,
            List<String> bitTraces
    ) {}

    public static int calculateCapacityBytes(int width, int height, int bitsPerChannel, boolean useAlpha) {
        int channels = useAlpha ? 4 : 3;
        long totalBits = (long) width * height * channels * bitsPerChannel;
        return (int) (totalBits / 8);
    }

    public StegoResult embed(BufferedImage coverImage, byte[] containerBytes, int bitsPerChannel, boolean useAlpha) {
        if (bitsPerChannel < 1 || bitsPerChannel > 4) {
            throw new IllegalArgumentException("Bits per channel must be between 1 and 4.");
        }

        int width = coverImage.getWidth();
        int height = coverImage.getHeight();
        int maxCapacity = calculateCapacityBytes(width, height, bitsPerChannel, useAlpha);

        if (containerBytes.length > maxCapacity) {
            throw new IllegalArgumentException(String.format(
                    "Payload size (%d bytes) exceeds image embedding capacity (%d bytes) for %dx%d image at %d bit(s)/channel.",
                    containerBytes.length, maxCapacity, width, height, bitsPerChannel));
        }

        BufferedImage stego = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage diffMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int totalBitsToEmbed = containerBytes.length * 8;
        int bitCursor = 0;
        int changedPixels = 0;
        double sumSquaredError = 0.0;
        List<String> traces = new ArrayList<>();

        int bitMask = (1 << bitsPerChannel) - 1;
        int clearMask = ~bitMask & 0xFF;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int origArgb = coverImage.getRGB(x, y);
                int a = (origArgb >> 24) & 0xFF;
                int r = (origArgb >> 16) & 0xFF;
                int g = (origArgb >> 8) & 0xFF;
                int b = origArgb & 0xFF;

                int origR = r, origG = g, origB = b;

                // Process Red
                if (bitCursor < totalBitsToEmbed) {
                    int val = getNextBits(containerBytes, bitCursor, bitsPerChannel);
                    r = (r & clearMask) | val;
                    bitCursor += bitsPerChannel;
                }
                // Process Green
                if (bitCursor < totalBitsToEmbed) {
                    int val = getNextBits(containerBytes, bitCursor, bitsPerChannel);
                    g = (g & clearMask) | val;
                    bitCursor += bitsPerChannel;
                }
                // Process Blue
                if (bitCursor < totalBitsToEmbed) {
                    int val = getNextBits(containerBytes, bitCursor, bitsPerChannel);
                    b = (b & clearMask) | val;
                    bitCursor += bitsPerChannel;
                }
                // Optional Alpha
                if (useAlpha && bitCursor < totalBitsToEmbed) {
                    int val = getNextBits(containerBytes, bitCursor, bitsPerChannel);
                    a = (a & clearMask) | val;
                    bitCursor += bitsPerChannel;
                }

                int newArgb = (a << 24) | (r << 16) | (g << 8) | b;
                stego.setRGB(x, y, newArgb);

                // Pixel difference calculation
                int dr = Math.abs(r - origR);
                int dg = Math.abs(g - origG);
                int db = Math.abs(b - origB);

                if (dr != 0 || dg != 0 || db != 0) {
                    changedPixels++;
                    sumSquaredError += (dr * dr + dg * dg + db * db) / 3.0;

                    // Diff map: amplified 50x to make subtle LSB variations visibly prominent
                    int ampR = Math.min(255, dr * 60);
                    int ampG = Math.min(255, dg * 60);
                    int ampB = Math.min(255, db * 60);
                    diffMap.setRGB(x, y, (255 << 24) | (ampR << 16) | (ampG << 8) | ampB);
                } else {
                    diffMap.setRGB(x, y, 0xFF0B0F19); // dark background
                }

                // Educational bit-level trace for first 5 modified pixels
                if (traces.size() < 5 && (dr != 0 || dg != 0 || db != 0)) {
                    traces.add(String.format("Pixel (%d,%d):\n  Orig: R=%s G=%s B=%s\n  Steg: R=%s G=%s B=%s (Bits changed: R:%d, G:%d, B:%d)",
                            x, y,
                            toBinary8(origR), toBinary8(origG), toBinary8(origB),
                            toBinary8(r), toBinary8(g), toBinary8(b),
                            Integer.bitCount(origR ^ r), Integer.bitCount(origG ^ g), Integer.bitCount(origB ^ b)));
                }
            }
        }

        int totalPixels = width * height;
        double changedPercentage = (changedPixels * 100.0) / totalPixels;
        double capacityPercentage = (containerBytes.length * 100.0) / maxCapacity;
        double mse = sumSquaredError / totalPixels;
        double psnr = (mse > 0) ? 10.0 * Math.log10((255.0 * 255.0) / mse) : 99.99; // 99.99 dB if identical

        return new StegoResult(
                stego, diffMap, containerBytes.length, maxCapacity, capacityPercentage,
                changedPixels, changedPercentage, mse, psnr, traces
        );
    }

    public byte[] extract(BufferedImage stegoImage, int bitsPerChannel, boolean useAlpha, int maxBytesToRead) {
        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();

        int maxPossible = calculateCapacityBytes(width, height, bitsPerChannel, useAlpha);
        int targetBytes = Math.min(maxBytesToRead > 0 ? maxBytesToRead : maxPossible, maxPossible);

        byte[] extracted = new byte[targetBytes];
        int bitMask = (1 << bitsPerChannel) - 1;

        int totalBitsNeeded = targetBytes * 8;
        int bitCursor = 0;

        for (int y = 0; y < height && bitCursor < totalBitsNeeded; y++) {
            for (int x = 0; x < width && bitCursor < totalBitsNeeded; x++) {
                int argb = stegoImage.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // Red
                if (bitCursor < totalBitsNeeded) {
                    appendBits(extracted, bitCursor, r & bitMask, bitsPerChannel);
                    bitCursor += bitsPerChannel;
                }
                // Green
                if (bitCursor < totalBitsNeeded) {
                    appendBits(extracted, bitCursor, g & bitMask, bitsPerChannel);
                    bitCursor += bitsPerChannel;
                }
                // Blue
                if (bitCursor < totalBitsNeeded) {
                    appendBits(extracted, bitCursor, b & bitMask, bitsPerChannel);
                    bitCursor += bitsPerChannel;
                }
                // Alpha
                if (useAlpha && bitCursor < totalBitsNeeded) {
                    appendBits(extracted, bitCursor, a & bitMask, bitsPerChannel);
                    bitCursor += bitsPerChannel;
                }
            }
        }

        return extracted;
    }

    private static int getNextBits(byte[] data, int bitOffset, int bitCount) {
        int byteIdx = bitOffset / 8;
        int bitInByte = bitOffset % 8; // 0 to 7

        int val = 0;
        for (int i = 0; i < bitCount; i++) {
            int currentBitOffset = bitOffset + i;
            int bIdx = currentBitOffset / 8;
            int bBit = 7 - (currentBitOffset % 8); // read MSB to LSB
            int bit = (data[bIdx] >> bBit) & 0x01;
            val = (val << 1) | bit;
        }
        return val;
    }

    private static void appendBits(byte[] data, int bitOffset, int bitsValue, int bitCount) {
        for (int i = 0; i < bitCount; i++) {
            int currentBitOffset = bitOffset + i;
            int bIdx = currentBitOffset / 8;
            int bBit = 7 - (currentBitOffset % 8);
            int bit = (bitsValue >> (bitCount - 1 - i)) & 0x01;
            if (bit != 0) {
                data[bIdx] |= (1 << bBit);
            }
        }
    }

    private static String toBinary8(int val) {
        String s = Integer.toBinaryString(val & 0xFF);
        return "0".repeat(Math.max(0, 8 - s.length())) + s;
    }
}
