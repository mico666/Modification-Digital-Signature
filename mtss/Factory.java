package mtss;

import block.BlockFile;
import block.BlockImage;
import block.BlockedMessage;
import block.Blockfy;
import cff.CFF;
import cff.CFFCode;
import cff.CFFConstruction;
import cff.CFFSetSystem;
import cff.RS;
import cff.STS;
import cff.Sperner;
import cffMatrix.CFFMatrix;
import cffMatrix.CFFMatrixCompact;
import cffMatrix.CFFMatrixList;
import groupTesting.GroupTesting;
import groupTesting.GroupTestingRS;
import groupTesting.GroupTestingSTS;
import groupTesting.GroupTestingSperner;

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
 * The Factory class provides methods to create various objects related to
 * cover-free-families (CFF), input message block separation, and group testing.
 * It uses factory methods to encapsulate the instantiation logic based on
 * specified parameters and types.
 */

public class Factory {

	/**
	 * Creates a CFFMatrix object based on the specified type.
	 *
	 * @param CFFMatrixType the type of the matrix, either "list" or "compact"
	 * @param c             the CFF object used to populate the matrix
	 * @return a CFFMatrix object of the specified type
	 * @throws IllegalArgumentException if the matrix type is not recognized
	 */
	public CFFMatrix createCFFMatrix(String CFFMatrixType, CFF c) {
		CFFMatrix m;
		if (CFFMatrixType.equalsIgnoreCase("list")) { // List
			m = new CFFMatrixList();
		} else if (CFFMatrixType.equalsIgnoreCase("compact")) { // Compact
			m = new CFFMatrixCompact();
		} else {
			throw new IllegalArgumentException("Error: wrong matrix type. Choose either list or compact.");
		}
		c.toMatrix(m);
		return m;
	}

	/**
	 * Performs block separation for a message file based on the specified
	 * specification.
	 *
	 * @param file the name of the file to be processed
	 * @param spec the specification for block separation, including choice, number,
	 *             and file type
	 * @return a BlockedMessage object containing the blocks of the message
	 * @throws IllegalArgumentException if the file type is not recognized
	 */
	public BlockedMessage createBlockedMessage(String file, Specification spec) {
		// From spec
		int choice = spec.getChoice();
		int number = spec.getNumber();
		String fileType = spec.getFileType();

		// Perform block separation
		Blockfy blockfy;
		if (fileType.equalsIgnoreCase("text")) {
			blockfy = new BlockFile();
		} else if (fileType.equalsIgnoreCase("image")) {
			blockfy = new BlockImage();
		} else {
			throw new IllegalArgumentException("Invalid choice. Either text or image");
		}
		BlockedMessage blockedMessage = blockfy.blockSeparation(file, choice, number);
		return blockedMessage;
	}

	/**
	 * Creates a CFF object based on the specified construction method.
	 *
	 * @param CFFMethod the construction method for the CFF ("sperner", "sts", or
	 *                  "rs")
	 * @param d         the number of defectives
	 * @param n         the number of items
	 * @return a CFF object constructed using the specified method
	 * @throws IllegalArgumentException if the construction method is not recognized
	 */
	public CFF createCFF(String CFFMethod, int d, int n) {
		CFFConstruction con = createCFFConstruction(CFFMethod);
		if (CFFMethod.equalsIgnoreCase("sperner") || CFFMethod.equalsIgnoreCase("sts")) {
			CFFSetSystem cff = (CFFSetSystem) con.build(d, n);
			return cff;
		} else if (CFFMethod.equalsIgnoreCase("rs")) {
			CFFCode cff = (CFFCode) con.build(d, n);
			return cff;
		} else {
			throw new IllegalArgumentException("Invalid CFF construction method. Choose sperner, sts, or rs.");
		}
	}

	/**
	 * Creates a CFFConstruction object based on the specified method.
	 *
	 * @param CFFMethod the method for CFF construction ("sperner", "sts", or "rs")
	 * @return a CFFConstruction object corresponding to the specified method
	 */
	CFFConstruction createCFFConstruction(String CFFMethod) {
		switch (CFFMethod.toLowerCase()) {
		case "sperner":
			return new Sperner();
		case "sts":
			return new STS();
		case "rs":
			return new RS();
		default:
			return null;
		}
	}

	/**
	 * Creates a GroupTesting object based on the specified parameters.
	 *
	 * @param GTChoice  the choice of group testing method (0 for general, 1 for
	 *                  specific)
	 * @param c         the CFF object
	 * @param CFFMethod the CFF construction method used
	 * @param m         the CFFMatrix object
	 * @return a GroupTesting object of the specified type
	 * @throws IllegalArgumentException if the group testing choice or method is not
	 *                                  recognized
	 */
	public GroupTesting createGroupTesting(int GTChoice, CFF c, String CFFMethod, CFFMatrix m) {

		GroupTesting gt = null;
		int d = c.getD();
		int t = c.getT();
		int n = c.getN();
		if (GTChoice == 0) {
			gt = new GroupTesting(n, d, t, m);
			return gt;
		} else if (GTChoice == 1) {
			if (CFFMethod.equalsIgnoreCase("sperner")) {
				gt = new GroupTestingSperner(n, t);
			} else if (CFFMethod.equalsIgnoreCase("sts")) {
				CFFSetSystem cff = (CFFSetSystem) c;
				gt = new GroupTestingSTS(n, t, cff);
			} else {
				CFFCode cff = (CFFCode) c;
				int N = cff.getCodeLength();
				int q = cff.getAlphabet();
				int k = (N - 1) / d + 1;
				gt = new GroupTestingRS(n, d, k, N, q);
			}
			return gt;
		} else {
			throw new IllegalArgumentException(
					"Invalid choice. Choose '0' for general decoding method and '1' for specific decoding method.");
		}

	}
}
