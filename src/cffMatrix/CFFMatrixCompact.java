package cffMatrix;

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
 * CFFMatrixCompact implements the CFFMatrix abstract class. This class
 * represents binary matrices using integer-based bit arrays (compact
 * representation). Each row of the matrix is stored as an array of long
 * integers, where each bit position indicates the presence (1) or absence (0)
 * of an entry in the matrix.
 */

public class CFFMatrixCompact extends CFFMatrix {

	private long[][] compactMatrix;
	private int b = 64;

	/**
	 * Initializes the binary matrix with the specified number of rows (t) and
	 * columns (n). The matrix is stored using long integers, where each long can
	 * store up to 64 bits.
	 * 
	 * @param t the number of rows in the matrix
	 * @param n the number of columns in the matrix
	 */
	@Override
	public void initialize(int t, int n) {
		this.t = t;
		this.n = n;
		int compactColumns = (int) Math.ceil((double) n / b); // Calculate the number of long integers in each row
		compactMatrix = new long[t][compactColumns];
	}

	/**
	 * Sets the entry at row i and column j to 1 using bitwise operations.
	 * 
	 * @param i the row index
	 * @param j the column index
	 */
	@Override
	public void set(int i, int j) {
		int compactPosition = j / b;
		int shift = j % b;
		compactMatrix[i][compactPosition] |= (long) 1 << shift; // bit wise operation
	}

	/**
	 * Retrieves a list of column indices where the entries are 1 in the specified
	 * row.
	 * 
	 * @param i the row index
	 * @return a list of column indices with 1s in row i
	 */
	@Override
	public List<Integer> getRow(int i) {
		ArrayList<Integer> row = new ArrayList<>();

		for (int j = 0; j < compactMatrix[i].length; j++) {
			long value = compactMatrix[i][j];
			int maxBits = b;

			if (j == compactMatrix[i].length - 1) { // if the last integer < b
				maxBits = (n % b == 0) ? b : n % b; // Handle full b bits or remainder bits
			}

			for (int shift = 0; shift < maxBits; shift++) {
				if ((value & 1) == 1) {
					row.add(j * b + shift);
				}
				value >>= 1; // Right shift to check the next bit
			}
		}
		return row;
	}

	/**
	 * Finds defectives based on the result vector y. Rows corresponding to a 0 in y
	 * are merged using bitwise OR, and the complement of the merged indices gives
	 * the defectives.
	 * 
	 * @param y the result vector, where 1 indicates a positive result
	 * @param I a list to store the indices of identified defectives
	 * @param d the maximum number of defectives (d-CFF)
	 * @return true if the number of identified defectives is less than or equal to
	 *         d; false otherwise
	 */
	@Override
	public boolean findDefectives(int[] y, List<Integer> I, int d) {
		int numCols = compactMatrix[0].length;
		long[] resultRow = new long[numCols];

		for (int i = 0; i < y.length; i++) {
			if (y[i] == 0) {
				for (int j = 0; j < numCols; j++) {
					resultRow[j] |= compactMatrix[i][j];
				}
			}
		}

		// The last column may not fill the full base
		// Pad 1's at the beginning to complete the base of the last column
		long lastColValue = resultRow[numCols - 1];
		int totalBits = n % b;
		if (totalBits != 0) { // Check if the last column needs padding
			int bitsToPad = b - totalBits;
			resultRow[numCols - 1] = padOnes(lastColValue, bitsToPad, b);
		}

		long value = (b == 64) ? -Long.MAX_VALUE : (1L << b) - 1;
		for (int i = 0; i < numCols; i++) {
			long x = resultRow[i];
			int element = i * b + 1;
			if (x == value) {
				continue;
			}
			for (int j = 0; j < b; j++) {
				if ((x & 1) == 0) {
					I.add(element);
				}
				x >>= 1;
				element++;
			}
		}

		return I.size() <= d;
	}

	/**
	 * Converts the compact representation of the matrix to a full binary matrix.
	 * 
	 * @return a 2D array representing the full binary matrix
	 */
	@Override
	public int[][] toIntMatrix() {

		int[][] intMatrix = new int[t][n];
		for (int i = 0; i < t; i++) {
			for (int j = 0; j < n; j++) {
				int compactPosition = j / b;
				int shift = j % b;
				intMatrix[i][j] = (int) ((compactMatrix[i][compactPosition] >> shift) & 1);
			}
		}
		return intMatrix;
	}

	/**
	 * Pads a number with 1s at the beginning to complete the required bits.
	 * 
	 * @param number    the original number
	 * @param bitsToPad the number of bits to pad with 1s
	 * @param b         the total bit length of the number
	 * @return the number after padding with 1s
	 */
	private static long padOnes(long number, int bitsToPad, int b) {
		for (int i = 0; i < bitsToPad; i++) {
			number |= 1L << (b - 1 - i);
		}

		return number;
	}

}
