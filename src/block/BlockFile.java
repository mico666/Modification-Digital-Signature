package block;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
 * The BlockFile class implements the Blockfy interface, providing functionality
 * to divide text files into blocks based on either a specified block size or
 * the desired number of blocks.
 */

public class BlockFile implements Blockfy { // for text files

	/**
	 * Divides a text file message into blocks based on a specified block size or
	 * the desired number of blocks.
	 * 
	 * @param textFileName the name of the text file to be divided
	 * @param blockChoice  the method of division: 0 for fixing block size, 1 for
	 *                     fixing the number of blocks
	 * @param number       the block size if blockChoice is 0, or the number of
	 *                     blocks if blockChoice is 1
	 * 
	 * @return a BlockedMessage object containing the blocks, block size, number of
	 *         blocks, the original message, and the file type
	 * @throws IllegalArgumentException if blockChoice is neither 0 nor 1
	 */

	@Override
	public BlockedMessage blockSeparation(String textFileName, int blockChoice, int number) {
		try {
			List<byte[]> blocks = new ArrayList<>();
			int blockSize = 0;

			byte[] message = Files.readAllBytes(Paths.get(textFileName));
			int totalLines = calculateTotalLines(message);

			if (blockChoice == 0) { // fixing block size
				blockSize = number;
			} else if (blockChoice == 1) { // fixing number of blocks
				blockSize = (int) Math.round((double) totalLines / number); // round up the block size
			} else {
				throw new IllegalArgumentException(
						"Invalid choice. Choose '0' for fixing block size and '1' for fixing number of blocks.");
			}
			blocks = createBlocks(message, blockSize);
			int numberOfBlocks = blocks.size();
			return new BlockedMessage(blocks, blockSize, numberOfBlocks, message, "text");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Divides a message into blocks based on the specified block size.
	 * 
	 * @param message   the byte array of the text file to be divided
	 * @param blockSize the size of each block in lines
	 * @return a list of byte arrays, each representing a block of the message
	 */

	private static List<byte[]> createBlocks(byte[] message, int blockSize) {
		int lineCount = 0;
		List<Byte> singleBlock = new ArrayList<>();
		List<byte[]> blocks = new ArrayList<>();

		for (byte b : message) {
			singleBlock.add(b);

			if (b == 10) { // Check for newline character
				lineCount++;
				if (lineCount % blockSize == 0) {
					byte[] blockBytes = new byte[singleBlock.size()];
					for (int i = 0; i < singleBlock.size(); i++) {
						blockBytes[i] = singleBlock.get(i);
					}
					blocks.add(blockBytes);
					singleBlock.clear();
				}
			}
		}

		// If there are remaining lines that don't fill a complete block
		if (!singleBlock.isEmpty()) {
			byte[] remainingBytes = new byte[singleBlock.size()];
			for (int i = 0; i < singleBlock.size(); i++) {
				remainingBytes[i] = singleBlock.get(i);
			}
			blocks.add(remainingBytes);
		}
		return blocks;
	}

	/**
	 * Calculates the total number of lines in a message.
	 * 
	 * @param message the byte array of the message
	 * @return the total number of lines in the message
	 */
	public static int calculateTotalLines(byte[] message) {
		int totalLineNumber = 0;
		for (byte b : message) {
			if (b == 10) { // Check for newline character
				totalLineNumber++;
			}
		}

		// Add the last lines
		if (message.length > 0 && message[message.length - 1] != 10) {
			totalLineNumber++;
		}
		return totalLineNumber;
	}

}
