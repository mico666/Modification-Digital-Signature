package block;

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
 * The Blockfy interface provides a framework for dividing messages into blocks,
 * applicable to both text files and images.
 */

public interface Blockfy {

	/**
	 * Divides a message into blocks based on a specific strategy, which is
	 * determined by either fixing the block size or the number of blocks.
	 *
	 * @param fileName    the name of the file containing the message to be divided
	 * @param blockChoice the strategy choice: 0 for fixing block size, 1 for fixing
	 *                    number of blocks
	 * @param number      the block size if blockChoice is 0, or the number of
	 *                    blocks if blockChoice is 1
	 * 
	 * @return a BlockedMessage object containing the divided blocks, block size,
	 *         number of blocks, the original message, and the file type
	 */
	public BlockedMessage blockSeparation(String fileName, int blockChoice, int number);

}
