package mtss;

import java.io.*;

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
 * The MTSSignature class represents the result of the signing process using a
 * modification-tolerant digital signature scheme. It encapsulates various
 * parameters of the signature process, such as the signature scheme, hash
 * algorithm, file type, CFF construction method, matrix type, block size,
 * number of blocks, the number of defectives(d), the number of rows(t), a tuple
 * of hashes, the resulting signature.
 */
public class MTSSignature {

	private String signatureScheme;
	private String hashAlgorithm;
	private String fileType;
	private String CFFMethod;
	private String CFFMatrixType;
	private int actualBlockSize;
	private int actualNumberOfBlocks;
	private int d;
	private int t;
	private String TString;
	private String signatureString;

	// Constructor
	MTSSignature(String signatureScheme, String hashAlgorithm, String fileType, String CFFMethod, String CFFMatrixType,
			int actualBlockSize, int actualNumberOfBlocks, int d, int t, String TString, String signatureString) {
		this.signatureScheme = signatureScheme;
		this.hashAlgorithm = hashAlgorithm;
		this.fileType = fileType;
		this.CFFMethod = CFFMethod;
		this.CFFMatrixType = CFFMatrixType;
		this.actualBlockSize = actualBlockSize;
		this.actualNumberOfBlocks = actualNumberOfBlocks;
		this.d = d;
		this.t = t;
		this.TString = TString;
		this.signatureString = signatureString;
	}

	// Constructor: from MTSSignatureStrings
	MTSSignature(String MTSSignatureString) {
		String[] parts = MTSSignatureString.split("\n"); // Split the MTSignatureString using the newline character as
															// the delimiter
		if (parts.length != 12) {
			signatureScheme = parts[0];
			hashAlgorithm = parts[1];
			fileType = parts[2];
			CFFMethod = parts[3];
			CFFMatrixType = parts[4];
			actualBlockSize = Integer.parseInt(parts[5]);
			actualNumberOfBlocks = Integer.parseInt(parts[6]);
			d = Integer.parseInt(parts[7]);
			t = Integer.parseInt(parts[8]);
			TString = parts[9];
			signatureString = parts[10];

		} else {
			throw new IllegalArgumentException("Invaild signature."); // if the length < 11
		}
	}

	/**
	 * Returns the string representation of the MTSSignature, with fields separated
	 * by newline characters.
	 *
	 * @return the string representation of the MTSSignature
	 */
	public String toString() {
		return signatureScheme + "\n" + hashAlgorithm + "\n" + fileType + "\n" + CFFMethod + "\n" + CFFMatrixType + "\n"
				+ actualBlockSize + "\n" + actualNumberOfBlocks + "\n" + d + "\n" + t + "\n" + TString + "\n"
				+ signatureString;
	}

	/**
	 * Writes the MTSSignature to a specified file path.
	 *
	 * @param filePath the path of the file to write the MTSSignature to
	 */
	public void writeToFile(String filePath) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
			String MTSSignatureString = toString();
			bufferedWriter.write(MTSSignatureString);
			bufferedWriter.close();
			System.out.println("MTSSignature have been written to: " + filePath + ".");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads an MTSSignature from a specified file path and reconstructs the object.
	 *
	 * @param filePath the path of the file to read the MTSSignature from
	 * @return the reconstructed MTSSignature object, or null if an error occurs
	 */
	public static MTSSignature readFromFile(String filePath) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
			StringBuilder MTSSignatureStringBuilder = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				MTSSignatureStringBuilder.append(line);
				MTSSignatureStringBuilder.append("\n");
			}
			return new MTSSignature(MTSSignatureStringBuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Getter methods
	public String getSignatureScheme() {
		return signatureScheme;
	}

	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	public String getFileType() {
		return fileType;
	}

	public String getCFFMethod() {
		return CFFMethod;
	}

	public String getCFFMatrixType() {
		return CFFMatrixType;
	}

	public int getBlockSize() {
		return actualBlockSize;
	}

	public int getNumberOfBlocks() {
		return actualNumberOfBlocks;
	}

	public int getD() {
		return d;
	}

	public int getRows() {
		return t;
	}

	public String getTString() {
		return TString;
	}

	public String getSignatureString() {
		return signatureString;
	}

}
