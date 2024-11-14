package block;

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
 * This class encapsulates the properties of a message divided into blocks,
 * including the block size, the number of blocks, the original message, and the
 * file type.
 *
 * @field blocks A list of byte arrays, each representing a block of the
 *        message.
 * @field blockSize The size of each block in bytes.
 * @field numberOfBlocks The total number of blocks.
 * @field message The original message in byte array format.
 * @field fileType The type of file the message represents (e.g., text, image).
 */

public class BlockedMessage {

	private List<byte[]> blocks;
	private int blockSize;
	private int numberOfBlocks;
	private byte[] message;
	private String fileType;

	// Constructor
	public BlockedMessage(List<byte[]> blocks, int blockSize, int numberOfBlocks, byte[] message, String fileType) {
		this.blocks = blocks;
		this.blockSize = blockSize;
		this.numberOfBlocks = numberOfBlocks;
		this.message = message;
		this.fileType = fileType;
	}

	// getter methods
	public List<byte[]> getBlocks() {
		return blocks;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public int getNumberOfBlocks() {
		return numberOfBlocks;
	}

	public byte[] getMessage() {
		return message;
	}

	public String getFileType() {
		return fileType;
	}

}
