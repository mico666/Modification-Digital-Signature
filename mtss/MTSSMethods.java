package mtss;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import cdss.CDSS;
import cdss.Dilithium;
import cdss.ECDSA;
import cdss.FALCON;
import cdss.RSA;
import cdss.SPHINCSPlus;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.asn1.ASN1InputStream;

import org.bouncycastle.util.io.pem.PemObject;

/**
 * Copyright 2024, Dongxia (Mico) Luo
 *
 * Developed for use with the thesis:
 *
 *    Modification-Tolerant Digital Signatures using Combinatorial Group Testing: Theory, Algorithms, and Implementation
 *    Dongxia (Mico) Luo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License as published by
 * the Massachusetts Institute of Technology.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * MIT License for more details.
 *
 * You should have received a copy of the MIT License
 * along with this program. If not, see <https://opensource.org/licenses/MIT>.
 */


/**
 * The MTSSMethods class contains various methods used in the
 * Modification-Tolerant Digital Signature Scheme (MTSS).
 */
public class MTSSMethods {

	/**
	 * Concatenates blocks based on the provided lists of indices.
	 *
	 * @param lists  the list of index lists specifying which blocks to concatenate
	 * @param blocks the list of byte arrays representing the blocks
	 * @return a list of concatenated byte arrays
	 */
	public static List<byte[]> concatenateBlocks(List<List<Integer>> lists, List<byte[]> blocks) {
		List<byte[]> concatenatedBlocks = new ArrayList<>();

		for (List<Integer> list : lists) {
			List<byte[]> byteArray = new ArrayList<>();
			for (int i : list) {
				byteArray.add(blocks.get(i));
			}
			concatenatedBlocks.add(toByteArray(byteArray));
		}
		return concatenatedBlocks;
	}

	/**
	 * Converts a list of byte arrays into a single concatenated byte array. From
	 * https://stackoverflow.com/questions/4827622/copy-several-byte-arrays-to-one-big-byte-array
	 * 
	 * @param bytesList the list of byte arrays to concatenate
	 * @return a single concatenated byte array
	 */
	public static byte[] toByteArray(List<byte[]> bytesList) {
		int size = 0;

		for (byte[] bytes : bytesList) {
			size += bytes.length;
		}

		ByteBuffer byteBuffer = ByteBuffer.allocate(size);

		for (byte[] bytes : bytesList) {
			byteBuffer.put(bytes);
		}

		return byteBuffer.array();
	}

	/**
	 * Hashes a list of byte arrays using the specified digest.
	 *
	 * @param blocks the list of byte arrays to hash
	 * @param digest the digest algorithm to use
	 * @return a list of hashed byte arrays
	 */
	public static List<byte[]> hashBytes(List<byte[]> blocks, Digest digest) {
		List<byte[]> hashedResults = new ArrayList<>();
		for (byte[] b : blocks) {

			byte[] hashedBlocks = new byte[digest.getDigestSize()];
			digest.update(b, 0, b.length);
			digest.doFinal(hashedBlocks, 0);

			hashedResults.add(hashedBlocks);
		}
		return hashedResults;
	}

	/**
	 * Prepares a string for signing by concatenating all input parameters.
	 *
	 * @param signatureScheme the digital signature scheme
	 * @param hashAlgorithm   the hash algorithm used
	 * @param fileType        the type of the file (e.g., text, image)
	 * @param CFFMethod       the CFF construction method
	 * @param CFFMatrixType   the CFF matrix data structure type
	 * @param d               the number of defectives
	 * @param t               the number of rows in CFF
	 * @param blockSize       the block size
	 * @param numberOfBlocks  the number of blocks
	 * @param TString         the tuple string
	 * @return the concatenated string representation of all inputs
	 */
	public static String signPrep(String signatureScheme, String hashAlgorithm, String fileType, String CFFMethod,
			String CFFMatrixType, int d, int t, int blockSize, int numberOfBlocks, String TString) throws Exception {

		// Convert all the integers to strings
		String blockSizeString = Integer.toString(blockSize);
		String numberOfBlocksString = Integer.toString(numberOfBlocks);
		String dString = Integer.toString(d);
		String tString = Integer.toString(t);

		String delimiter = " "; // a delimiter to separate strings
		StringBuilder wholeString = new StringBuilder();
		wholeString.append(signatureScheme).append(delimiter).append(hashAlgorithm).append(delimiter).append(fileType)
				.append(delimiter).append(CFFMethod).append(delimiter).append(CFFMatrixType).append(delimiter)
				.append(blockSizeString).append(delimiter).append(numberOfBlocksString).append(delimiter)
				.append(dString).append(delimiter).append(tString).append(delimiter).append(TString);

		return wholeString.toString();
	}

	/**
	 * Converts a hexadecimal string to a byte array.
	 *
	 * @param hexString the hexadecimal string to convert
	 * @return the corresponding byte array
	 */
	public static byte[] hexToByteArray(String hexString) {
		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Reads a classical public key from a PEM file.
	 *
	 * @param pemFilePath the path to the PEM file
	 * @return the reconstructed AsymmetricKeyParameter for the public key
	 * @throws IOException if an error occurs while reading the file
	 */
	public static AsymmetricKeyParameter readClaasicalPublicKeyFromPemFile(String pemFilePath) throws IOException {
		FileReader reader = new FileReader(pemFilePath);
		PemReader pemReader = new PemReader(reader);
		PemObject pemObject = pemReader.readPemObject();
		byte[] content = pemObject.getContent();
		reader.close();

		try {
			ASN1InputStream asn1InputStream = new ASN1InputStream(content);
			SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(asn1InputStream.readObject());
			asn1InputStream.close();
			return org.bouncycastle.crypto.util.PublicKeyFactory.createKey(publicKeyInfo);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads a post-quantum public key from a PEM file.
	 *
	 * @param pemFilePath the path to the PEM file
	 * @return the reconstructed AsymmetricKeyParameter for the public key
	 * @throws IOException if an error occurs while reading the file
	 */
	public static AsymmetricKeyParameter readPQCPublicKeyFromPemFile(String pemFilePath) throws IOException {
		FileReader reader = new FileReader(pemFilePath);
		PemReader pemReader = new PemReader(reader);
		PemObject pemObject = pemReader.readPemObject();
		byte[] content = pemObject.getContent();
		reader.close();

		try {
			ASN1InputStream asn1InputStream = new ASN1InputStream(content);
			SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(asn1InputStream.readObject());
			asn1InputStream.close();

			return org.bouncycastle.pqc.crypto.util.PublicKeyFactory.createKey(publicKeyInfo);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the public key based on the signature scheme and public key file.
	 *
	 * @param signatureScheme the digital signature scheme
	 * @param publicKeyFile   the path to the public key PEM file
	 * @return the AsymmetricKeyParameter for the public key
	 */
	public static AsymmetricKeyParameter retrievePublicKey(String signatureScheme, String publicKeyFile) {
		AsymmetricKeyParameter publicKey = null;
		try {
			if (signatureScheme.equalsIgnoreCase("rsa")) {
				publicKey = MTSSMethods.readClaasicalPublicKeyFromPemFile(publicKeyFile);
			} else if (signatureScheme.equalsIgnoreCase("ecdsa")) {
				publicKey = MTSSMethods.readClaasicalPublicKeyFromPemFile(publicKeyFile);
			} else if (signatureScheme.equalsIgnoreCase("sphincsplus")) {
				publicKey = MTSSMethods.readPQCPublicKeyFromPemFile(publicKeyFile);
			} else if (signatureScheme.equalsIgnoreCase("falcon")) {
				publicKey = MTSSMethods.readPQCPublicKeyFromPemFile(publicKeyFile);
			} else if (signatureScheme.equalsIgnoreCase("dilithium")) {
				publicKey = MTSSMethods.readPQCPublicKeyFromPemFile(publicKeyFile);
			} else {
				throw new IllegalArgumentException("Invalid signature scheme or public key file!");
			}
			return publicKey;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Constructs a CDSS object based on the signature scheme name.
	 *
	 * @param signatureScheme the name of the signature scheme
	 * @return the constructed CDSS object
	 */
	static CDSS createCDSS(String signatureScheme) {
		switch (signatureScheme.toLowerCase()) {
		case "ecdsa":
			return new ECDSA();
		case "rsa":
			return new RSA();
		case "sphincsplus":
			return new SPHINCSPlus();
		case "falcon":
			return new FALCON();
		case "dilithium":
			return new Dilithium();
		default:
			throw new IllegalArgumentException("Invalid signature scheme!");
		}
	}

	/**
	 * Writes a classical public key to a PEM file.
	 *
	 * @param publicKey the AsymmetricKeyParameter for the public key
	 * @throws IOException if an error occurs while writing the file
	 */
	static void writeClassicalPublicKeyToPemFile(AsymmetricKeyParameter publicKey) throws IOException {
		SubjectPublicKeyInfo publicKeyInfo = org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory
				.createSubjectPublicKeyInfo(publicKey);
		byte[] publicKeyBytes = publicKeyInfo.getEncoded();
		FileWriter writer = new FileWriter("publicKey.pem");
		PemWriter pemWriter = new PemWriter(writer);

		try {
			PemObject pemObject = new PemObject("PUBLIC KEY", publicKeyBytes);
			pemWriter.writeObject(pemObject);
		} finally {
			pemWriter.close();
			writer.close();
		}
	}

	/**
	 * Writes a post-quantum public key to a PEM file.
	 *
	 * @param publicKey the AsymmetricKeyParameter for the post-quantum public key
	 * @throws IOException if an error occurs while writing the file
	 */
	static void writePQCPublicKeyToPemFile(AsymmetricKeyParameter publicKey) throws IOException {
		SubjectPublicKeyInfo publicKeyInfo = org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory
				.createSubjectPublicKeyInfo(publicKey);
		byte[] publicKeyBytes = publicKeyInfo.getEncoded();

		FileWriter writer = new FileWriter("publicKey.pem");
		PemWriter pemWriter = new PemWriter(writer);

		try {
			PemObject pemObject = new PemObject("PUBLIC KEY", publicKeyBytes);
			pemWriter.writeObject(pemObject);
		} finally {
			pemWriter.close();
			writer.close();
		}
	}

	/**
	 * Creates a Digest object based on the specified hash algorithm.
	 *
	 * @param hashAlgorithm the name of the hash algorithm
	 * @return the corresponding Digest object
	 * @throws IllegalArgumentException if the hash algorithm is not recognized
	 */
	public static Digest createHash(String hashAlgorithm) {
		switch (hashAlgorithm.toUpperCase()) {
		case "SHA2256":
			return new SHA256Digest();
		case "SHA2512":
			return new SHA512Digest();
		case "SHA3256":
			return new SHA3Digest(256);
		case "SHA3512":
			return new SHA3Digest(512);
		default:
			throw new IllegalArgumentException("Invalid hash algorithm!");
		}
	}

}
