package com.beatplaylist.utilities.security;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileCipher {

	private static FileCipher instance = new FileCipher();

	public static FileCipher getInstance() {
		return instance;
	}

	public String decode(String enc, int offset) {
		return encode(enc, 26 - offset);
	}

	public String encode(String enc, int offset) {
		offset = offset % 26 + 26;
		StringBuilder encoded = new StringBuilder();
		for (char i : enc.toCharArray()) {
			if (Character.isLetter(i)) {
				if (Character.isUpperCase(i))
					encoded.append((char) ('A' + (i - 'A' + offset) % 26));
				else
					encoded.append((char) ('a' + (i - 'a' + offset) % 26));
			} else
				encoded.append(i);
		}
		return encoded.toString();
	}

	public String encrypt(String value) {
		try {
			SecureRandom secureRandom = new SecureRandom();
			byte[] key = new byte[16];
			secureRandom.nextBytes(key);
			SecretKey secretKey = new SecretKeySpec(key, "AES");

			byte[] iv = new byte[12]; // NEVER REUSE THIS IV WITH SAME KEY
			secureRandom.nextBytes(iv);

			final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); // 128 bit auth tag length
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
			byte[] cipherText = cipher.doFinal("hello world".getBytes());

			ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
			byteBuffer.putInt(iv.length);
			byteBuffer.put(iv);
			byteBuffer.put(cipherText);
			byte[] cipherMessage = byteBuffer.array();

			String base64CipherMessage = Base64.getEncoder().encodeToString(cipherMessage);
			System.out.println(base64CipherMessage);

			return base64CipherMessage;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public String decrypt(byte[] key, String encrypted) {
		try {
			byte[] cipherMessage = Base64.getDecoder().decode(encrypted);
			ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
			int ivLength = byteBuffer.getInt();
			if (ivLength < 12 || ivLength >= 16) { // check input parameter
				throw new IllegalArgumentException("invalid iv length");
			}
			byte[] iv = new byte[ivLength];
			byteBuffer.get(iv);
			byte[] cipherText = new byte[byteBuffer.remaining()];
			byteBuffer.get(cipherText);

			final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));
			byte[] plainText = cipher.doFinal(cipherText);
			return new String(plainText);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	private SecretKeySpec getKeySpec(String passphrase) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return new SecretKeySpec(digest.digest(passphrase.getBytes("UTF-8")), "AES");
	}

	private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
		return Cipher.getInstance("AES/CBC/PKCS5PADDING");
	}

	public String encrypt(String passphrase, String value) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] initVector = new byte[16];
		SecureRandom.getInstanceStrong().nextBytes(initVector);
		Cipher cipher = getCipher();
		cipher.init(Cipher.ENCRYPT_MODE, getKeySpec(passphrase), new IvParameterSpec(initVector));
		byte[] encrypted = cipher.doFinal(value.getBytes());
		return Base64.getEncoder().encodeToString(initVector) + Base64.getEncoder().encodeToString(encrypted);
	}

	public String decrypt(String passphrase, String encrypted) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] initVector = Base64.getDecoder().decode(encrypted.substring(0, 24));
		Cipher cipher = getCipher();
		cipher.init(Cipher.DECRYPT_MODE, getKeySpec(passphrase), new IvParameterSpec(initVector));
		byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted.substring(24)));
		return new String(original);
	}
}