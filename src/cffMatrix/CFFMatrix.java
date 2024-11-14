package cffMatrix;

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
 * Abstract class CFFMatrix provides a blueprint for working with binary
 * matrices in the context of combinatorial group testing. It defines the
 * essential methods required for initializing, manipulating, and analyzing
 * binary matrices. There are two data structures that can implement this
 * abstract class: MatrixList and MatrixCompact.
 */
public abstract class CFFMatrix {

	protected int t;
	protected int n;

	/**
	 * Initializes the binary matrix with the specified number of rows and columns.
	 * 
	 * @param t the number of rows in the matrix
	 * @param n the number of columns in the matrix
	 */
	public abstract void initialize(int t, int n);

	/**
	 * Sets the entry at row i and column j to 1 in the matrix.
	 * 
	 * @param i the row index (0-based)
	 * @param j the column index (0-based)
	 */
	public abstract void set(int i, int j);

	/**
	 * Retrieves a list of column indices from the specified row where the entries
	 * are set to 1.
	 * 
	 * @param i the row index
	 * @return a list of column indices with 1s in row i
	 */
	public abstract List<Integer> getRow(int i); // from 0 to t-1

	/**
	 * Finds defectives based on the result vector y and stores the indices of
	 * defectives in list I.
	 * 
	 * @param y the result vector, where 1 indicates a positive result
	 * @param I a list to store the indices of identified defectives
	 * @param d the maximum number of defectives (d-CFF)
	 * @return true if the number of identified defectives is less than or equal to
	 *         d; false otherwise
	 */
	public abstract boolean findDefectives(int[] y, List<Integer> I, int d);

	/**
	 * Converts the matrix to a full binary matrix representation as a 2D array.
	 * 
	 * @return a 2D array representing the full binary matrix
	 */
	public abstract int[][] toIntMatrix();

	/**
	 * Returns a string representation of the binary matrix, with each row of the
	 * matrix represented on a new line.
	 * 
	 * @return a string representing the binary matrix
	 */
	public String toString() {
		int[][] matrix = toIntMatrix();
		String arrString = "";
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				arrString += matrix[i][j] + " ";
			}
			arrString += "\n";
		}
		return arrString;
	}

}
