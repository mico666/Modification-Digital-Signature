package cffMatrix;

import java.util.ArrayList;
import java.util.LinkedList;
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
 * CFFMatrixList implements the CFFMatrix abstract class. This class represents
 * binary matrices using lists of indices where the entries are 1. Each row of
 * the matrix is represented as a list of column indices where the value is 1.
 */
public class CFFMatrixList extends CFFMatrix {

	private List<ArrayList<Integer>> tests;

	/**
	 * Initializes the matrix with the specified number of rows (t) and columns (n).
	 * Each row is represented as an empty list that will later hold the column
	 * indices where the value is 1.
	 * 
	 * @param t the number of rows in the matrix
	 * @param n the number of columns in the matrix
	 */
	@Override
	public void initialize(int t, int n) {
		this.t = t;
		this.n = n;
		tests = new ArrayList<>();
		for (int i = 0; i < t; i++) {
			tests.add(new ArrayList<>());
		}
	}

	/**
	 * Sets the entry at row i and column j to 1 by adding the column index j to the
	 * list for row i.
	 * 
	 * @param i the row index (0-based)
	 * @param j the column index (0-based)
	 */
	@Override
	public void set(int i, int j) {
		tests.get(i).add(j); // Add the indexes of 1's in each row
	}

	/**
	 * Retrieves the list of column indices where the entries are 1 in the specified
	 * row.
	 * 
	 * @param i the row index
	 * @return a list of column indices with 1s in row i
	 */
	@Override
	public List<Integer> getRow(int i) {
		ArrayList<Integer> row = tests.get(i);
		return row;
	}

	/**
	 * Finds defectives based on the result vector y. Rows corresponding to a 0 in y
	 * are merged, and the complement of the merged indices gives the defectives.
	 * 
	 * @param y the result vector, where 1 indicates a positive result
	 * @param I a list to store the indices of identified defectives
	 * @param d the maximum number of defectives (d-CFF)
	 * @return true if the number of identified defectives is less than or equal to
	 *         d; false otherwise
	 */
	@Override
	public boolean findDefectives(int[] y, List<Integer> I, int d) {

		LinkedList<ArrayList<Integer>> linked = new LinkedList<>();
		ArrayList<Integer> mergedList = new ArrayList<>();

		for (int i = 0; i < y.length; i++) {
			if (y[i] == 0) {
				linked.add(tests.get(i));
			}
		}

		while (linked.size() > 1) { // merge all the lists to two by two get all the indexes of negative ones
			ArrayList<Integer> L1 = linked.pollFirst();
			ArrayList<Integer> L2 = linked.pollFirst();
			mergedList = mergeLists(L1, L2);
			linked.offerLast(mergedList);
		}
		mergedList = linked.pollFirst();
		I.addAll(complementList(mergedList, n)); // Add all the defectives to the original list

		return I.size() <= d;

	}

	/**
	 * Converts the list-based representation of the matrix to a full binary matrix.
	 * 
	 * @return a 2D array representing the full binary matrix
	 */
	@Override
	public int[][] toIntMatrix() {
		int[][] intMatrix = new int[t][n];

		for (int i = 0; i < t; i++) {
			for (int j : tests.get(i)) {
				intMatrix[i][j] = 1;
			}
		}
		return intMatrix;

	}

	/**
	 * Merges two sorted lists into a single sorted list.
	 * 
	 * @param list1 the first sorted list
	 * @param list2 the second sorted list
	 * @return a new sorted list containing all elements from list1 and list2
	 */
	public static ArrayList<Integer> mergeLists(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		ArrayList<Integer> mergedList = new ArrayList<>();

		// two pointers
		int i = 0, j = 0;

		while (i < list1.size() && j < list2.size()) {
			int element1 = list1.get(i);
			int element2 = list2.get(j);

			if (element1 == element2) {
				mergedList.add(element1);
				i++;
				j++;
			} else if (element1 < element2) {
				mergedList.add(element1);
				i++;
			} else {
				mergedList.add(element2);
				j++;
			}
		}

		// Add remaining elements from either lists, if any
		while (i < list1.size()) {
			mergedList.add(list1.get(i));
			i++;
		}

		while (j < list2.size()) {
			mergedList.add(list2.get(j));
			j++;
		}
		return mergedList;
	}

    /**
     * Finds the complement of a sorted list.
     * 
     * @param mergedList a sorted list of indices
     * @param n the upper limit of the index range
     * @return a list of indices not present in mergedList
     */
	public static ArrayList<Integer> complementList(ArrayList<Integer> mergedList, int n) {

		ArrayList<Integer> complement = new ArrayList<>();

		if (mergedList == null) {
			for (int k = 1; k <= n; k++) {
				complement.add(k);
			}
			return complement;
		}

		// two pointers
		int i = 0, j = 0;
		while (j < mergedList.size()) {
			int element = mergedList.get(j);
			if (i == element) {
				i++;
				j++;
			} else {
				complement.add(i + 1);
				i++;
			}
		}

		// If there are some remaining elements
		while (i < n) {
			complement.add(i + 1);
			i++;
		}
		return complement;

	}

}
