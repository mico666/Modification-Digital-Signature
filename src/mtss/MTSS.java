package mtss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import block.BlockedMessage;
import cdss.CDSS;
import cdss.KeyPair;
import cff.CFF;
import cffMatrix.CFFMatrix;
import groupTesting.GroupTesting;

import org.bouncycastle.crypto.Digest;

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
 * The MTSS class implements Modification-Tolerant Digital Signature Scheme
 * (MTSS).
 */

public class MTSS {

	/**
	 * Generates a key pair (SK, PK) for MTSS.
	 *
	 * @param CDSSType the underlying cryptographic digital signature scheme (CDSS).
	 * @return a KeyPair object containing the secret key (SK) and public key (PK).
	 * @throws Exception if an error occurs during the key generation process.
	 */
	public KeyPair MTSSKeyGen(String CDSSType) throws Exception {
		try {
			// Obtain CDSS from spec and key gen
			CDSS signatureScheme = (CDSS) MTSSMethods.createCDSS(CDSSType);
			KeyPair keyPair = signatureScheme.KeyGeneration();

			// Convert the keys to AsymmetricKeyParameter type
			AsymmetricKeyParameter privKey = (AsymmetricKeyParameter) keyPair.getPrivateKey();
			AsymmetricKeyParameter publicKey = (AsymmetricKeyParameter) keyPair.getPublicKey();

			// Write publicKey to a pem.file
			if (signatureScheme.getClass().getSimpleName().equalsIgnoreCase("rsa")
					|| signatureScheme.getClass().getSimpleName().equalsIgnoreCase("ecdsa")) {
				MTSSMethods.writeClassicalPublicKeyToPemFile(publicKey);
			} else {
				MTSSMethods.writePQCPublicKeyToPemFile(publicKey); // post-quantum
			}
			return new KeyPair(privKey, publicKey);
		} catch (NullPointerException e) {
			System.err.println("Error: The key pair or public key is null.");
			throw e;
		} catch (Exception e) {
			System.err.println("An unexpected error occurred: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Signs a message using MTSS.
	 * 
	 * @param blockedMessage the BlockedMessage object containing the message
	 *                       divided into blocks.
	 * @param spec           the Specification object that holds the parameters and
	 *                       settings for MTSS.
	 * @param cff            the CFF object representing the combinatorial group
	 *                       testing configuration.
	 * @param privKey        the private key used for signing the message.
	 * @return a MTSSignature object representing the resulting MTSS signature.
	 * @throws Exception if an error occurs during the signing process.
	 */
	public MTSSignature MTSSign(BlockedMessage blockedMessage, Specification spec, CFF cff,
			AsymmetricKeyParameter privKey) throws Exception { // block separation and CFF construction are done before.

		try {

			// Step 1:
			// Hash the whole message

			String hashAlgorithmString = spec.getHashType();
			Digest hashAlgorithm = MTSSMethods.createHash(hashAlgorithmString);
			byte[] message = blockedMessage.getMessage();

			byte[] hstar = new byte[hashAlgorithm.getDigestSize()];
			hashAlgorithm.update(message, 0, message.length);
			hashAlgorithm.doFinal(hstar, 0);

			// Step 2:
			// Concatenate according to CFF
			Factory factory = new Factory();
			String CFFMatrixType = spec.getCFFMatrixType();
			CFFMatrix m = factory.createCFFMatrix(CFFMatrixType, cff);

			List<List<Integer>> lists = new ArrayList<>();
			int t = cff.getT();
			for (int i = 0; i < t; i++) {
				lists.add(m.getRow(i));
			}

			List<byte[]> blocks = blockedMessage.getBlocks();
			List<byte[]> concatenatedBlocks = MTSSMethods.concatenateBlocks(lists, blocks);

			// Step 3:
			// Hash the concatenated blocks
			List<byte[]> tuple = MTSSMethods.hashBytes(concatenatedBlocks, hashAlgorithm);

			// Step 4:
			// Signing Preparation
			StringBuilder THexString = new StringBuilder();
			for (byte[] byteArray : tuple) {
				for (byte b : byteArray) {
					THexString.append(String.format("%02X", b));
				}
				THexString.append(" "); // Add a space at the end of each byte array
			}

			for (byte b : hstar) {
				THexString.append(String.format("%02X", b));
			}
			String TString = THexString.toString();

			// From BlockedMessage
			int blockSize = blockedMessage.getBlockSize();
			int numberOfBlocks = blockedMessage.getNumberOfBlocks(); // n
			String fileType = blockedMessage.getFileType();

			// From CFF
			int d = cff.getD();

			// From Specification
			String CFFMethod = spec.getCFFMethod();
			String signatureSchemeString = spec.getCDSSType();

			// Combine all the parameters with TString as 'whole' to be signed altogether
			String wholeString = MTSSMethods.signPrep(signatureSchemeString, hashAlgorithmString, fileType, CFFMethod,
					CFFMatrixType, d, t, blockSize, numberOfBlocks, TString);
			byte[] whole = wholeString.getBytes();

			// Step 5:
			// Sign with SK
			CDSS signatureScheme = MTSSMethods.createCDSS(signatureSchemeString);
			byte[] signature = signatureScheme.Sign(whole, privKey);
			// Transfer the signature into hexadecimal format
			StringBuilder signatureHex = new StringBuilder();
			for (byte b : signature) {
				signatureHex.append(String.format("%02X", b));
			}
			String signatureString = signatureHex.toString();
			// Return the MTSignature object
			return new MTSSignature(signatureSchemeString, hashAlgorithmString, fileType, CFFMethod, CFFMatrixType,
					blockSize, numberOfBlocks, d, t, TString, signatureString);
		} catch (Exception e) {
			System.err.println("An unexpected error occurred during the signing process: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Verifies an MTSS signature using MTSS.
	 * 
	 * @param blockedMessageM the BlockedMessage object containing the message
	 *                        divided into blocks.
	 * @param cffM            the CFF object representing the combinatorial group
	 *                        testing configuration.
	 * @param mtssignature    the MTSSignature object representing the signature to
	 *                        be verified.
	 * @param GTchoice        the group testing choice, indicating whether to use
	 *                        the general or specific decoding method. Possible
	 *                        values: 0 for general, 1 for specific decoding method.
	 * @param publicKey       the public key used for verifying the signature.
	 * @return true if the signature is valid; false otherwise.
	 * @throws Exception if an error occurs during the verification process.
	 */
	public boolean MTSSVerify(BlockedMessage blockedMessageM, CFF cffM, MTSSignature mtssignature, int GTchoice,
			AsymmetricKeyParameter publicKey) throws Exception {

		try {

			// Step 1: Verify the signature with whole
			String signatureSchemeString = mtssignature.getSignatureScheme();
			CDSS signatureScheme = MTSSMethods.createCDSS(signatureSchemeString);
			String hashAlgorithmString = mtssignature.getHashAlgorithm();
			String fileType = mtssignature.getFileType();
			String CFFMethod = mtssignature.getCFFMethod();
			String CFFMatrixType = mtssignature.getCFFMatrixType();
			int d = mtssignature.getD();
			int t = mtssignature.getRows();
			int blockSize = mtssignature.getBlockSize();
			int numberOfBlocks = mtssignature.getNumberOfBlocks();
			String TString = mtssignature.getTString();

			String wholeString = MTSSMethods.signPrep(signatureSchemeString, hashAlgorithmString, fileType, CFFMethod,
					CFFMatrixType, d, t, blockSize, numberOfBlocks, TString);

			byte[] whole = wholeString.getBytes();
			String signatureString = mtssignature.getSignatureString();
			byte[] signature = MTSSMethods.hexToByteArray(signatureString);
			boolean verifyResult = signatureScheme.Verify(whole, signature, publicKey);
			if (!verifyResult) {
				System.out.println("The signature is not valid.");
				return verifyResult; // output (0,-)
				// Exit and stop
			}

			// Step 2:
			// Get hstar and tuple of hashes from the signing process to compare

			String[] tupleAndHstar = TString.split(" ");
			List<byte[]> tuple = new ArrayList<>();
			for (String h : tupleAndHstar) { // Convert each hexadecimal byte back to a byte array
				byte[] byteArray = MTSSMethods.hexToByteArray(h);
				tuple.add(byteArray);
			}
			int lastIndex = tuple.size() - 1;
			byte[] hstar = tuple.get(lastIndex); // hstar
			tuple.remove(lastIndex);
			// Calculate hstarM from message for compare
			byte[] messageM = blockedMessageM.getMessage();
			Digest hashAlgorithm = MTSSMethods.createHash(hashAlgorithmString);
			byte[] hstarM = new byte[hashAlgorithm.getDigestSize()]; // Modified message
			hashAlgorithm.update(messageM, 0, messageM.length);
			hashAlgorithm.doFinal(hstarM, 0);

			// Compare hstar with hstarM
			if (Arrays.equals(hstar, hstarM)) {
				System.out.println("The document has not been modified.");
				return true;// output (1,-)
				// Exit and stop
			}

			// Step 3:
			// a) Concatenate blocks according to CFF
			Factory factory = new Factory();
			CFFMatrix m = factory.createCFFMatrix(CFFMatrixType, cffM);
			List<List<Integer>> lists = new ArrayList<>();
			for (int i = 0; i < t; i++) {
				lists.add(m.getRow(i));
			}
			List<byte[]> blocksM = blockedMessageM.getBlocks();
			List<byte[]> concatenatedBlocksM = MTSSMethods.concatenateBlocks(lists, blocksM);
			// b) Hash the concatenated blocks
			List<byte[]> tupleM = MTSSMethods.hashBytes(concatenatedBlocksM, hashAlgorithm);

			// Step 4:
			// Locate modification: compare tuple with tupleM
			int[] y = new int[t];
			for (int i = 0; i < tuple.size(); i++) {
				byte[] hash = tuple.get(i);
				byte[] hashM = tupleM.get(i);
				if (Arrays.equals(hash, hashM)) {
					y[i] = 0;
				} else {
					y[i] = 1;
				}
			}

			// System.out.println("y: " + Arrays.toString(y));
			List<Integer> I = new ArrayList<>();
			GroupTesting gt = factory.createGroupTesting(GTchoice, cffM, CFFMethod, m);
			Boolean result = gt.findDefectives(y, I);
			System.out.println("defectives: " + I);
			return result;

		} catch (Exception e) {
			System.err.println("An unexpected error occurred during the verification process: " + e.getMessage());
			throw e;
		}

	}

	/**
	 * Signs a message using MTSS and writes the resulting signature to a file.
	 * 
	 * @param blockedMessage the BlockedMessage object containing the message
	 *                       divided into blocks.
	 * @param spec           the Specification object that holds the parameters and
	 *                       settings for MTSS.
	 * @param cff            the CFF object representing the combinatorial group
	 *                       testing configuration.
	 * @param privKey        the private key used for signing the message.
	 * @param fileName       the name of the file where the MTSS signature will be
	 *                       written.
	 * @return an MTSSignature object representing the resulting MTSS signature.
	 * @throws Exception if an error occurs during the signing process or while
	 *                   writing to the file.
	 */
	public MTSSignature MTSSignFile(BlockedMessage blockedMessage, Specification spec, CFF cff,
			AsymmetricKeyParameter privKey, String fileName) throws Exception {

		// Call the "sign" algorithm to obtain MTSSignature
		MTSSignature mtSignature = MTSSign(blockedMessage, spec, cff, privKey);

		// Specify the file path where you want to write the parameters, and write to
		// the file
		mtSignature.writeToFile(fileName);

		return mtSignature; // Return the signature string in hexadecimal
	}

	/**
	 * Verifies an MTSS signature by reading it from a file using MTSS.
	 * 
	 * @param blockedMessageM the BlockedMessage object containing the message
	 *                        divided into blocks.
	 * @param cffM            the CFF object representing the combinatorial group
	 *                        testing configuration.
	 * @param signatureFile   the name of the file from which the MTSS signature
	 *                        will be read.
	 * @param GTChoice        the group testing choice, indicating whether to use
	 *                        the general or specific decoding method. Possible
	 *                        values: 0 for general decoding, 1 for specific
	 *                        decoding method.
	 * @param publicKey       the public key used for verifying the signature.
	 * @return true if the signature is valid; false otherwise.
	 * @throws Exception if an error occurs during the verification process or while
	 *                   reading the signature from the file.
	 */
	public boolean MTSSVerifyFile(BlockedMessage blockedMessageM, CFF cffM, String signatureFile, int GTChoice,
			AsymmetricKeyParameter publicKey) throws Exception {

		// Read from MTSSignature.txt and reconstruct the MTSSignature from the file
		MTSSignature mtssignatureM = MTSSignature.readFromFile(signatureFile);

		// Call the "verify" algorithm and return
		return MTSSVerify(blockedMessageM, cffM, mtssignatureM, GTChoice, publicKey);

	}

}
