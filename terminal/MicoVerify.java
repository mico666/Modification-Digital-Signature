package terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import block.BlockedMessage;
import cff.CFF;
import mtss.Factory;
import mtss.MTSS;
import mtss.MTSSMethods;
import mtss.MTSSignature;
import mtss.Specification;

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
 * Run MTSS-verify in terminal.
 */
public class MicoVerify {

	public static void main(String[] args) throws IOException {

		// If user input == "null" or user input = "-help", display the instruction
		// manual.
		if (args.length == 0 || args[0].equals("-help")) {
			System.out.println("Usage: java -cp jarfile:./ MicoVerify [options] <value>");
			System.out.println("Options:");
			System.out.println("  -k <pem file>");
			System.out.println("     The PEM file storing the public key.");
			System.out.println();
			System.out.println("  -gt <general|specific>");
			System.out.println("     The group testing method:");
			System.out.println("       - 'general' for the general decoding method");
			System.out.println("       - 'specific' for the specific decoding method");
			System.out.println();
			System.out.println("  -gp <file>,<signature>");
			System.out.println("     A file and its corresponding signature.txt (one or more).");
			System.out.println("     Each file and its signature should be separated by a comma.");
			System.out.println("     Leave a space between pairs.");

			// MTSS
		} else if (args.length >= 6) {
			int GTMethod = 0;
			String publicKeyFile = null;
			List<String> files = new ArrayList<>();

			// Checking for options and processing accordingly
			boolean hasPublicKey = false;
			boolean hasFilePair = false;
			boolean hasGTMethod = false;

			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("-")) {
					// Check for options
					String option = args[i];
					if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
						String value = args[i + 1];
						// Process the option and its value
						switch (option) {
						case "-k":
							publicKeyFile = value;
							hasPublicKey = true;
							break;
						case "-gp":
							hasFilePair = true;
							int j = i;
							while (j + 1 < args.length && !args[j + 1].startsWith("-")) {
								files.add(args[j + 1]);
								j++; // Move to the next file name
							}
							break;
						case "-gt":
							hasGTMethod = true;
							if (value.equalsIgnoreCase("general")) {
								GTMethod = 0;
							} else if (value.equalsIgnoreCase("specific")) {
								GTMethod = 1;
							} else {
								System.out.println(
										"Invalid choice of decoding method. You can enter '-help' to get instructions.");
								return;
							}
							break;
						default:
							System.out.println("Invalid option: " + option);
							return;
						}
						i++; // Move to the next argument
					} else {
						System.out.println(
								"Missing choices for the arguments. You can enter '-help' to get instructions.");
						return;
					}
				}
			}

			if (!hasPublicKey || !hasFilePair || !hasGTMethod) {
				System.out.println("Missing arguments. You can enter '-help' to get instructions.");
				return;
			}

			// Verify
			MTSS mtss = new MTSS();// Call mtss
			Factory factory = new Factory();

			for (String pair : files) {
				String[] filePair = pair.split(",");
				if (filePair.length == 2) {
					String file = filePair[0].trim();
					String signatureFile = filePair[1].trim();
					// create spec from MTSSignature
					MTSSignature mtssignature = MTSSignature.readFromFile(signatureFile);
					Specification specM = new Specification(mtssignature);
					// CFF construction
					BlockedMessage blockedMessageM = factory.createBlockedMessage(file, specM);
					String CFFMethodM = specM.getCFFMethod();
					int nM = blockedMessageM.getNumberOfBlocks();
					int dM = specM.getD();
					CFF cM = factory.createCFF(CFFMethodM, dM, nM);

					String signatureScheme = specM.getCDSSType();
					AsymmetricKeyParameter publicKey = MTSSMethods.retrievePublicKey(signatureScheme, publicKeyFile);
					try {
						boolean verifyResult = mtss.MTSSVerifyFile(blockedMessageM, cM, signatureFile, GTMethod,
								publicKey);
						System.out.println("The verification result is: " + verifyResult);
					} catch (Exception e) {
						System.out.println("An error occurred during the MTSS verification process: " + e.getMessage());
						return;
					}
				} else {
					System.out.println("Invalid arguments. You can enter '-help' to get instructions.");
				}
			}
			// If not reaching the length >= 4 (for sure there are missing parts)
		} else {
			System.out.println("Missing arguments or choices. You can enter '-help' to get instructions.");
		}

	}

}
