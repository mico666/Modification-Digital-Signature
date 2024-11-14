package groupTesting;

import java.util.ArrayList;
import java.util.List;

import cff.Sperner;

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
 * The GroupTestingSperner class extends GroupTesting, providing a specific
 * group testing method for Sperner sets with d = 1.
 */
public class GroupTestingSperner extends GroupTesting {

	// constructor
	public GroupTestingSperner(int n, int t) {
		super(n, 1, t, null);
	}

	/**
	 * A specific method for finding defectives in Sperner set systems. It
	 * identifies defectives by analyzing the test results vector.
	 *
	 * @param y the result vector of the tests
	 * @param I a list to store the indices of identified defectives
	 * @return true if the number of identified defectives is equal to half of the
	 *         rows (t/2), false otherwise
	 */
	@Override
	public boolean findDefectives(int[] y, List<Integer> I) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < y.length; i++) {
			if (y[i] == 1) {
				result.add(i);
			}
		}

		if ((result.size() == t / 2)) { // Check if the positive results = half of the rows

			int[] pos = new int[t / 2];
			for (int i = 0; i < result.size(); i++) {
				pos[i] = result.get(i) + 1;
			}
			int r = rankSubset(pos, t / 2, t); // rank
			I.add(r);
			return true;

		} else {
			long combination = Sperner.binomial(result.size(), t / 2); // see how many combinations of possible
																		// defectives are

			int[] firstIndex = new int[t / 2]; // Calculate the first subset index in the combination
			for (int k = 1; k <= firstIndex.length; k++) {
				firstIndex[k - 1] = k;
			}

			int[] first = new int[t / 2]; // Compute the first subset
			for (int i = 0; i < first.length; i++) {
				first[i] = result.get(firstIndex[i] - 1) + 1;
			}
			int r = rankSubset(first, t / 2, t); // rank
			I.add(r);

			// calculate the next subset index
			for (int j = 0; j < combination - 1; j++) {
				firstIndex = Sperner.subsetLexSucessor(firstIndex, t / 2, result.size());
				for (int i = 0; i < first.length; i++) {
					first[i] = result.get(firstIndex[i] - 1) + 1;
				}
				r = rankSubset(first, t / 2, t); // rank
				if (r <= n) {
					I.add(r);
				} else {
					break;
				}
			}
			return false;
		}

	}

	/**
	 * Ranks a subset in lexicographic order, starting from 1. This method
	 * implements Algorithm 2.7 from Stinson's Combinatorial Algorithms.
	 *
	 * @param array the subset array to be ranked
	 * @param t     the size of the subset
	 * @param n     the total number of items
	 * @return the rank of the subset, starting from 1
	 */
	private static int rankSubset(int[] array, int t, int n) {
		int r = 1; // initial rank = 1
		int currentPos = 0; // current position in the array

		for (int i = 1; i <= t; i++) {
			if (currentPos + 1 <= array[i - 1] - 1) {
				for (int j = currentPos + 1; j <= array[i - 1] - 1; j++) {
					r += Sperner.binomial((n - j), (t - i));
				}
			}
			currentPos = array[i - 1];
		}

		return r;
	}

}
