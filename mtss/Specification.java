package mtss;

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
 * The Specification class encapsulates various parameters and settings required
 * for the Modification-Tolerant Digital Signature Scheme (MTSS). It includes
 * information about the signature scheme, hash algorithm, CFF construction
 * method, matrix type, file type, and block separation specifications.
 *
 * 
 * @field CDSSType the digital signature scheme. Possible values: "ECDSA",
 *        "RSA", "SPHINCSPlus", "FALCON", "Dilithium".
 * @field HashType the hash algorithm. Possible values: "SHA2256", "SHA2512",
 *        "SHA3256", "SHA3512".
 * @field d the number of defectives (d-CFF).
 * @field CFFMethod the CFF construction method. Possible values: "Sperner",
 *        "STS", "RS".
 * @field CFFMatrixType the CFF matrix data structure type. Possible values:
 *        "List", "Compact".
 * @field fileType the type of the file being processed. Possible values:
 *        "text", "image".
 * @field choice the choice for block separation. Possible values: 0 (fixing
 *        block size), 1 (fixing number of blocks).
 * @field number the fixing number (either block size or number of blocks,
 *        depending on the choice).
 */
public class Specification {

	private String CDSSType;
	private String HashType;
	private int d;
	private String CFFMethod;
	private String CFFMatrixType;
	private String fileType;
	private int choice;
	private int number;

	// Constructor
	public Specification(String CDSSType, String HashType, int d, String CFFMethod, String CFFMatrixType,
			String fileType, int choice, int number) {
		this.CDSSType = CDSSType;
		this.HashType = HashType;
		this.d = d;
		this.CFFMethod = CFFMethod;
		this.CFFMatrixType = CFFMatrixType;
		this.fileType = fileType;
		this.choice = choice;
		this.number = number;
	}

	// Constructor: from MTSSignature
	public Specification(MTSSignature mtssignature) {
		CDSSType = mtssignature.getSignatureScheme();
		HashType = mtssignature.getHashAlgorithm();
		d = mtssignature.getD();
		CFFMethod = mtssignature.getCFFMethod();
		CFFMatrixType = mtssignature.getCFFMatrixType();
		fileType = mtssignature.getFileType();
		choice = 0;
		number = mtssignature.getBlockSize();
	}

	// getter methods
	public String getCDSSType() {
		return CDSSType;
	}

	public String getHashType() {
		return HashType;
	}

	public int getD() {
		return d;
	}

	public String getCFFMethod() {
		return CFFMethod;
	}

	public String getCFFMatrixType() {
		return CFFMatrixType;
	}

	public String getFileType() {
		return fileType;
	}

	public int getChoice() {
		return choice;
	}

	public int getNumber() {
		return number;
	}

}
