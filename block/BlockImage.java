package block;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
 * The BlockImage class implements the Blockfy interface. This class provides
 * functionality to divide image files into blocks based on a specified block
 * size or the number of desired blocks.
 */

public class BlockImage implements Blockfy { // for image file

	/**
	 * Divides an image file into blocks based on a specified strategy of either
	 * fixing the block size or fixing the number of blocks.
	 * 
	 * @param imageFileName the name of the image file to be divided
	 * @param blockChoice   the strategy choice: 0 for fixing block size, 1 for
	 *                      fixing the number of blocks
	 * @param number        the block size if blockChoice is 0, or the number of
	 *                      blocks if blockChoice is 1
	 * 
	 * @return a BlockedMessage object containing the divided blocks, block size,
	 *         number of blocks, the original message in bytes, and the file type
	 *         ("image")
	 * @throws IllegalArgumentException if blockChoice is neither 0 nor 1
	 */

	@Override
	public BlockedMessage blockSeparation(String imageFileName, int blockChoice, int number) {
		try {
			// Process the message
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			byte[][] imageArray = processImage(imageFileName, byteStream);
			byte[] message = byteStream.toByteArray();

			int rows = imageArray.length;
			int columns = imageArray[0].length;

			List<byte[]> blocks = new ArrayList<>();
			int blockSize = 0;
			if (blockChoice == 0) { // fixing block size
				blockSize = (number > rows || number > columns) ? Math.max(rows, columns) : number; // Handle the case
																									// where the block
																									// size is bigger
																									// than the
																									// dimension

			} else if (blockChoice == 1) { // fixing number of blocks
				if (number > rows * columns) { // Handle the case where the number of blocks chosen bigger than the
												// dimension of the image.
					blockSize = 1;
				} else {
					double square = (double) rows * columns / number;
					double sideDimension = Math.sqrt(square);
					blockSize = (int) (sideDimension >= Math.floor(sideDimension) + 0.5 ? Math.ceil(sideDimension)
							: Math.floor(sideDimension));
				}
			} else {
				throw new IllegalArgumentException(
						"Invalid choice. Choose '0' for fixing block size and '1' for fixing number of blocks.");
			}
			blocks = createblocks(imageArray, blockSize);
			int numberOfBlocks = blocks.size();
			return new BlockedMessage(blocks, blockSize, numberOfBlocks, message, "image");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<byte[]> createblocks(byte[][] imageArray, int blockSize) {
		List<byte[]> blocks = new ArrayList<>();
		int rows = imageArray.length;
		int columns = imageArray[0].length;

		// Calculate the number of rows and columns for the blocks
		int blockRows = (rows + blockSize - 1) / blockSize;
		int blockColumns = (columns + blockSize - 1) / blockSize;

		// Iterate over each block
		for (int i = 0; i < blockRows; i++) {
			for (int j = 0; j < blockColumns; j++) {
				// Calculate the starting and ending indices for the current block
				int startRow = i * blockSize;
				int endRow = Math.min(startRow + blockSize, rows);
				int startColumn = j * blockSize;
				int endColumn = Math.min(startColumn + blockSize, columns);

				// Create a byte array to store the bytes of the current block
				byte[] blockBytes = new byte[(endRow - startRow) * (endColumn - startColumn)];

				// Iterate over the elements in the current block and store the bytes
				int byteIndex = 0;
				for (int row = startRow; row < endRow; row++) {
					for (int column = startColumn; column < endColumn; column++) {
						blockBytes[byteIndex++] = imageArray[row][column];
					}
				}
				blocks.add(blockBytes);
			}
		}
		return blocks;
	}

	/**
	 * Reads an image file, processes its data into a 2D byte array, and outputs the data
	 * into a byte stream, while also extracting the image dimensions.
	 * 
	 * @param fileName     the name of the image file to be read and processed
	 * @param byteStream   a ByteArrayOutputStream to store the image data in a sequential byte format
	 * @return a 2D byte array representing the image's pixel values, where each byte 
	 *         represents a pixel intensity value
	 * @throws FileNotFoundException if the specified file is not found
	 * @throws IOException if an I/O error occurs during reading or writing data
	 */
	
	public static byte[][] processImage(String fileName, ByteArrayOutputStream byteStream)
			throws FileNotFoundException, IOException {

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

			// Skip the first four lines
			reader.readLine();
			reader.readLine();
			String[] dims = reader.readLine().split(" "); // Read dimensions line
			int columns = Integer.parseInt(dims[0]);
			int rows = Integer.parseInt(dims[1]);
			reader.readLine();

			// 2D image array
			byte[][] imageArray = new byte[rows][columns];
			int row = 0;
			int col = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				String[] values = line.trim().split(" ");
				for (String value : values) {
					byte pixelByte = (byte) Integer.parseInt(value);
					imageArray[row][col++] = pixelByte;
					byteStream.write(pixelByte);
					if (col == columns) {
						col = 0;
						row++;
					}
				}
			}

			return imageArray;
		}
	}
}
