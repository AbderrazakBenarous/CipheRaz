package com.cryptolab.core.steganography;

import com.cryptolab.core.steganalysis.SteganalysisEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive test suite validating LSB steganography, CLAB container protocol,
 * AES-GCM password encryption layer, and steganalysis anomaly detection.
 *
 * Developed by Abderrazak BENAROUS
 */
class SteganographyTest {

    private BufferedImage createSampleImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = (x * 255) / width;
                int gr = (y * 255) / height;
                int b = (x + y) % 256;
                image.setRGB(x, y, (r << 16) | (gr << 8) | b);
            }
        }
        g.dispose();
        return image;
    }

    @Test
    @DisplayName("StegoContainer: packing and unpacking unencrypted text with CRC-32 validation")
    void testStegoContainerPlain() throws Exception {
        String secret = "CYBERSECURITY STEGANOGRAPHY LABORATORY DISPATCH 2026";
        byte[] packed = StegoContainer.pack(
                secret.getBytes(StandardCharsets.UTF_8),
                StegoContainer.TYPE_TEXT,
                (byte) 1,
                null
        );

        assertThat(packed).startsWith('C', 'L', 'A', 'B');

        StegoContainer.ContainerPayload unpacked = StegoContainer.unpack(packed, null);
        assertThat(unpacked.encrypted()).isFalse();
        assertThat(unpacked.textContent()).isEqualTo(secret);
    }

    @Test
    @DisplayName("StegoContainer: AES-256-GCM password encryption and tamper protection")
    void testStegoContainerEncrypted() throws Exception {
        String secret = "HIGHLY CONFIDENTIAL PAYLOAD UNDER AES-GCM";
        String password = "CorrectUniversityPassword!2026";

        byte[] packed = StegoContainer.pack(
                secret.getBytes(StandardCharsets.UTF_8),
                StegoContainer.TYPE_TEXT,
                (byte) 2,
                password
        );

        // Unpack with correct password
        StegoContainer.ContainerPayload unpacked = StegoContainer.unpack(packed, password);
        assertThat(unpacked.encrypted()).isTrue();
        assertThat(unpacked.textContent()).isEqualTo(secret);

        // Fail with wrong password
        assertThatThrownBy(() -> StegoContainer.unpack(packed, "WrongPassword"))
                .isInstanceOf(Exception.class);

        // Tamper test: Corrupt CRC32 check
        byte[] tampered = packed.clone();
        tampered[tampered.length - 2] ^= 0xFF;
        assertThatThrownBy(() -> StegoContainer.unpack(tampered, password))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("CRC-32 checksum mismatch");
    }

    @Test
    @DisplayName("LsbStegoEngine: embed and extract round trip across 1, 2, and 4 bits per channel")
    void testLsbRoundTrip() throws Exception {
        BufferedImage cover = createSampleImage(100, 100); // 10,000 pixels
        LsbStegoEngine engine = new LsbStegoEngine();
        String secretMessage = "University lab test message embedded via LSB raster manipulation.";

        for (int bpc = 1; bpc <= 4; bpc++) {
            byte[] container = StegoContainer.pack(
                    secretMessage.getBytes(StandardCharsets.UTF_8),
                    StegoContainer.TYPE_TEXT,
                    (byte) bpc,
                    null
            );

            LsbStegoEngine.StegoResult result = engine.embed(cover, container, bpc, false);
            assertThat(result.stegoImage()).isNotNull();
            assertThat(result.differenceMap()).isNotNull();
            assertThat(result.changedPixelsCount()).isGreaterThan(0);
            assertThat(result.psnrDb()).isGreaterThan(40.0); // High fidelity image

            // Extract
            byte[] extractedRaw = engine.extract(result.stegoImage(), bpc, false, container.length);
            StegoContainer.ContainerPayload recovered = StegoContainer.unpack(extractedRaw, null);
            assertThat(recovered.textContent()).isEqualTo(secretMessage);
        }
    }

    @Test
    @DisplayName("LsbStegoEngine: oversized payload throws IllegalArgumentException")
    void testCapacityOverflow() {
        BufferedImage smallCover = createSampleImage(10, 10); // 100 pixels = 300 channel bits = 37 bytes capacity at 1 bpc
        LsbStegoEngine engine = new LsbStegoEngine();
        byte[] hugePayload = new byte[1000];

        assertThatThrownBy(() -> engine.embed(smallCover, hugePayload, 1, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds image embedding capacity");
    }

    @Test
    @DisplayName("SteganalysisEngine: extracts bit plane and evaluates anomaly indicators")
    void testSteganalysis() {
        BufferedImage image = createSampleImage(80, 80);

        BufferedImage redLsb = SteganalysisEngine.extractBitPlane(image, SteganalysisEngine.Channel.RED, 0);
        assertThat(redLsb.getWidth()).isEqualTo(80);
        assertThat(redLsb.getHeight()).isEqualTo(80);

        var anomaly = SteganalysisEngine.evaluateAnomaly(image);
        assertThat(anomaly.totalEvaluatedPixels()).isEqualTo(6400);
        assertThat(anomaly.anomalyScore()).isBetween(0.0, 100.0);
        assertThat(anomaly.assessment()).isNotBlank();
    }
}
