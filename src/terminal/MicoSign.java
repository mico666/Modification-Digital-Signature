package terminal;

import java.util.ArrayList;
import java.util.List;

import block.BlockedMessage;
import cdss.KeyPair;
import cff.CFF;
import mtss.Factory;
import mtss.MTSS;
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
 * Run MTSS-sign in terminal.
 */

public class MicoSign {

	public static void main(String[] args) throws Exception {

		// If user input == "null" or user input = "-help", display the instruction
		// manual.
		if (args.length == 0 || args[0].equals("-help")) {
			System.out.println("Usage: java -cp jarfile:./ MicoSign [options] <value>");
			System.out.println("Options:");
			System.out.println("  -a <ecdsa|rsa|sphincsplus|falcon|dilithium>");
			System.out.println("     Select the CDSS algorithm.");
			System.out.println();
			System.out.println("  -h <sha2256|sha2512|sha3256|sha3512>");
			System.out.println("     Choose the hash algorithm.");
			System.out.println();
			System.out.println("  -d <integer>");
			System.out.println("     Specify the maximum number of defectives.");
			System.out.println();
			System.out.println("  -c <sperner|sts|rs>");
			System.out.println("     The CFF Construction Method:");
			System.out.println("       - 'sperner' for d = 1");
			System.out.println("       - 'sts' or 'rs' for d = 2");
			System.out.println("       - 'rs' for any other value of d");
			System.out.println();
			System.out.println("  -f <list|compact>");
			System.out.println("     The format of CFF matrix representation:");
			System.out.println("       - 'list' or 'compact'");
			System.out.println();
			System.out.println("  -g <image> | -t <text>");
			System.out.println("     Choose the type of file to sign:");
			System.out.println("       - 'g' for image files");
			System.out.println("       - 't' for text files");
			System.out.println("     You can sign one or more documents of the same type.");
			System.out.println("     Leave a space between each file.");
			System.out.println();
			System.out.println("  -b <integer> | -z <integer>");
			System.out.println("     Choose either:");
			System.out.println("       - 'b' for fixing the number of blocks");
			System.out.println("       - 'z' for fixing block size");
			System.out.println("     You can choose different block size or number of blocks for each file.");
			System.out.println();
			System.out.println("  -s <String>");
			System.out.println("     Specify a customized extension for signature files.");

		} else if (args.length >= 16) {
			// Command line is separated by one or more spaces. Should expect at least 16
			// arguments ("-a dsa" counts 2) or more (can sign more than one file).

			// for spec
			String CDSSType = null;
			String HashType = null;
			int d = 0;
			String CFFMethod = null;
			String CFFMatrixType = null;
			String fileType = null;
			int choice = 0;
			List<Integer> fixingNumbers = new ArrayList<>();

			List<String> files = new ArrayList<>();
			String extension = null;

			// Check to see if we have all the arguments we want
			boolean hasCDSSType = false;
			boolean hasHashType = false;
			boolean hasD = false;
			boolean hasCFFMethod = false;
			boolean hasCFFMatrixType = false;
			boolean hasFile = false;
			boolean hasChoice = false;
			boolean hasExtension = false;
			boolean hasFileType = false;

			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("-")) {
					// Check for options
					String option = args[i];
					System.out.println();
					if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
						String value = args[i + 1];
						// Process the option and its value
						switch (option) {
						case "-a":
							CDSSType = value;
							if (!value.equalsIgnoreCase("ecdsa") && !value.equalsIgnoreCase("rsa")
									&& !value.equalsIgnoreCase("falcon") && !value.equalsIgnoreCase("dilithium")
									&& !value.equalsIgnoreCase("sphincsplus")) {
								System.out.println(
										"Invalid choice for the CDSS type. You can enter '-help' to get instructions.");
								return;
							}
							hasCDSSType = true;
							break;
						case "-h":
							HashType = value;
							if (!value.equalsIgnoreCase("sha2256") && !value.equalsIgnoreCase("sha2512")
									&& !value.equalsIgnoreCase("sha3256") && !value.equalsIgnoreCase("sha3512")) {
								System.out.println(
										"Invalid choice for the hash type. You can enter '-help' to get instructions.");
								return;
							}
							hasHashType = true;
							break;
						case "-d":
							d = Integer.parseInt(value);
							if (d < 1) {
								System.out.println("Invalid choice of d. You can enter '-help' to get instructions.");
								return;
							}
							hasD = true;
							break;
						case "-c":
							CFFMethod = value;
							if (!value.equalsIgnoreCase("sperner") && !value.equalsIgnoreCase("sts")
									&& !value.equalsIgnoreCase("rs")) {
								System.out.println(
										"Invalid choice for the CFF construction method. You can enter '-help' to get instructions.");
								return;
							}
							hasCFFMethod = true;
							break;
						case "-s":
							extension = value; // can be anything
							hasExtension = true;
							break;
						case "-f":
							CFFMatrixType = value;
							if (!value.equalsIgnoreCase("list") && !value.equalsIgnoreCase("compact")) {
								System.out.println(
										"Invalid choice for the CFF matrix format. You can enter '-help' to get instructions.");
								return;
							}
							hasCFFMatrixType = true;
							break;
						case "-b":
							choice = 1; // fixing number of blocks
							int l = i;
							while (l + 1 < args.length && !args[l + 1].startsWith("-")) {
								fixingNumbers.add(Integer.parseInt(args[l + 1]));
								l++; // Move to the next file name
							}
							hasChoice = true;
							break;
						case "-z":
							choice = 0; // fixing block size
							int m = i;
							while (m + 1 < args.length && !args[m + 1].startsWith("-")) {
								fixingNumbers.add(Integer.parseInt(args[m + 1]));
								m++; // Move to the next file name
							}
							hasChoice = true;
							break;
						case "-g":
							fileType = "image";
							int j = i;
							while (j + 1 < args.length && !args[j + 1].startsWith("-")) {
								files.add(args[j + 1]);
								j++; // Move to the next file name
							}
							hasFile = true;
							hasFileType = true;
							break;
						case "-t":
							fileType = "text";
							int k = i;
							while (k + 1 < args.length && !args[k + 1].startsWith("-")) {
								files.add(args[k + 1]);
								k++; // Move to the next file name
							}
							hasFile = true;
							hasFileType = true;
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

			if (!(hasCDSSType && hasHashType && hasD && hasCFFMethod && hasExtension && hasChoice && hasFile
					&& hasCFFMatrixType && hasFileType)) {
				System.out.println("Missing arugments. You can enter '-help' to get instructions.");
				return;
			}

			if (fixingNumbers.size() != files.size() && fixingNumbers.size() != 1) {
				System.out.println(
						"You need to enter the same amount of integers as your files or enter one integer for all your files.");
				return;
			}

			// Sign
			MTSS mtss = new MTSS();
			KeyPair keyPair = mtss.MTSSKeyGen(CDSSType); // keyGen
			Factory factory = new Factory();

			int k = 0;
			for (int j = 0; j < files.size(); j++) { // Loop through the files and sign each one
				int number = fixingNumbers.get(k);
				String currentFile = files.get(j);
				Specification spec = new Specification(CDSSType, HashType, d, CFFMethod, CFFMatrixType, fileType,
						choice, number);
				BlockedMessage blockedMessage = factory.createBlockedMessage(currentFile, spec); // block separation
				int n = blockedMessage.getNumberOfBlocks();
				CFF c = factory.createCFF(CFFMethod, d, n); // CFF construction
				try {
					String signatureFile = createNewFileName(currentFile, extension);
					System.out.println("The corresponding signature file is: " + signatureFile);
					mtss.MTSSignFile(blockedMessage, spec, c, keyPair.getPrivateKey(), signatureFile);
				} catch (Exception e) {
					System.out.println("An error occurred during the MTSS signing process: " + e.getMessage());
					return;
				}
				if (fixingNumbers.size() != 1)
					k++;
			}
		} else {
			System.out.println("Missing arguments or choices. You can enter '-help' to get instructions.");
		}

	}

	// Method to add file extension.
	public static String createNewFileName(String file, String extension) {
		int lastDotIndex = file.lastIndexOf(".");
		String newFileName;

		if (lastDotIndex != -1) {
			String name = file.substring(0, lastDotIndex); // Get the part before the last '.'

			// Create the new filename by adding the extension and "txt"
			newFileName = name + "_" + extension + ".txt";
		} else {
			// If there's no '.' in the filename, you can simply append the new extension
			// and "txt"
			newFileName = file + "_" + extension + ".txt";
		}

		return newFileName;
	}

}
