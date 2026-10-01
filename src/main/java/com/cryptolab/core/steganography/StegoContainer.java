package com.cryptolab.core.steganography;

import com.cryptolab.core.security.PasswordKdf;
import com.cryptolab.core.security.SecureRandomProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

/**
 * Educational and robust binary container specification for steganographic payloads.
 * Format:
 * MAGIC (4B 'CLAB') | VER (1B) | FLAGS (1B) | TYPE (1B) | BPC (1B) | LENGTH (4B) | [SALT(16B) + IV(12B)] | DATA | CRC32 (4B)
 *
 * Developed by Abderrazak BENAROUS
 */
public class StegoContainer {

    public static final byte[] MAGIC = new byte[]{'C', 'L', 'A', 'B'};
    public static final byte VERSION = 0x01;

    public static final byte FLAG_ENCRYPTED = 0x01;
    public static final byte TYPE_TEXT = 0x01;
    public static final byte TYPE_BINARY = 0x02;

    public record ContainerPayload(
            byte payloadType,
            boolean encrypted,
            byte bitsPerChannel,
            byte[] rawData,
            String textContent
    ) {}

    public static byte[] pack(byte[] payloadData, byte payloadType, byte bitsPerChannel, String optionalPassword) throws Exception {
        boolean encrypt = optionalPassword != null && !optionalPassword.isEmpty();
        byte flags = encrypt ? FLAG_ENCRYPTED : 0x00;

        byte[] finalPayload;
        byte[] salt = new byte[16];
        byte[] iv = new byte[12];

        if (encrypt) {
            SecureRandomProvider.getSecureRandom().nextBytes(salt);
            SecureRandomProvider.getSecureRandom().nextBytes(iv);

            SecretKey key = PasswordKdf.deriveAesGcmKey(optionalPassword.toCharArray(), salt);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            finalPayload = cipher.doFinal(payloadData);
        } else {
            finalPayload = payloadData;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Header
        dos.write(MAGIC);
        dos.writeByte(VERSION);
        dos.writeByte(flags);
        dos.writeByte(payloadType);
        dos.writeByte(bitsPerChannel);
        dos.writeInt(finalPayload.length);

        if (encrypt) {
            dos.write(salt);
            dos.write(iv);
        }

        // Payload data
        dos.write(finalPayload);

        // CRC-32 Checksum
        CRC32 crc = new CRC32();
        crc.update(finalPayload);
        dos.writeInt((int) crc.getValue());

        dos.flush();
        return baos.toByteArray();
    }

    public static ContainerPayload unpack(byte[] containerBytes, String optionalPassword) throws Exception {
        if (containerBytes.length < 12) {
            throw new IllegalArgumentException("Data too short to contain a valid CipheRaz stego container.");
        }

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(containerBytes));

        // Magic check
        byte[] magic = new byte[4];
        dis.readFully(magic);
        if (magic[0] != MAGIC[0] || magic[1] != MAGIC[1] || magic[2] != MAGIC[2] || magic[3] != MAGIC[3]) {
            throw new IllegalArgumentException("No valid CipheRaz steganography header ('CLAB') detected.");
        }

        byte version = dis.readByte();
        byte flags = dis.readByte();
        byte payloadType = dis.readByte();
        byte bitsPerChannel = dis.readByte();
        int payloadLength = dis.readInt();

        if (payloadLength < 0 || payloadLength > containerBytes.length) {
            throw new IllegalArgumentException("Corrupted container: invalid payload length field (" + payloadLength + ").");
        }

        boolean encrypted = (flags & FLAG_ENCRYPTED) != 0;
        byte[] salt = new byte[16];
        byte[] iv = new byte[12];

        if (encrypted) {
            dis.readFully(salt);
            dis.readFully(iv);
        }

        byte[] payloadData = new byte[payloadLength];
        dis.readFully(payloadData);

        int expectedCrc = dis.readInt();
        CRC32 crc = new CRC32();
        crc.update(payloadData);
        if ((int) crc.getValue() != expectedCrc) {
            throw new SecurityException("CRC-32 checksum mismatch! Payload is corrupted or has been altered.");
        }

        byte[] decryptedData;
        if (encrypted) {
            if (optionalPassword == null || optionalPassword.isEmpty()) {
                throw new SecurityException("Password required: This embedded payload is encrypted with AES-256-GCM.");
            }
            SecretKey key = PasswordKdf.deriveAesGcmKey(optionalPassword.toCharArray(), salt);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
            decryptedData = cipher.doFinal(payloadData);
        } else {
            decryptedData = payloadData;
        }

        String text = (payloadType == TYPE_TEXT) ? new String(decryptedData, StandardCharsets.UTF_8) : null;
        return new ContainerPayload(payloadType, encrypted, bitsPerChannel, decryptedData, text);
    }
}
