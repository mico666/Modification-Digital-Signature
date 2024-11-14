package groupTesting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cff.CFFSetSystem;
import cff.STS;

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
 * The GroupTestingSTS class extends GroupTesting, providing a specific group
 * testing method for Steiner Triple Systems (STS) with d = 2.
 */
public class GroupTestingSTS extends GroupTesting {

	CFFSetSystem c;

	// constructor
	public GroupTestingSTS(int n, int t, CFFSetSystem c) {
		super(n, 2, t, null);
		this.c = c;
	}

	/**
	 * A specific method for finding defectives in STS set systems. It identifies
	 * defectives by analyzing the test results vector.
	 *
	 * @param y the result vector of the tests
	 * @param I a list to store the indices of identified defectives
	 * @return true if the defectives are found and valid, false if more than two
	 *         defectives are present
	 * @throws IllegalArgumentException if the result vector size is not valid.
	 */
	@Override
	public boolean findDefectives(int[] y, List<Integer> I) {

		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < y.length; i++) {
			if (y[i] == 1) {
				result.add(i + 1);
			}
		}

		int s = result.size();
		if (s == 0) {
			return true;
		} else if (s < 3 || s == 4) {
			throw new IllegalArgumentException("Not a valid result");
		} else {

			int[][] blocks = c.getSets();
			int v = c.getT();
			int[][] preSTS = STS.presentationSTS(blocks, v);
			int[][] rankSTS = STS.locateSTS(blocks, v);

			if (s <= 6) {
				List<Integer> triples = new ArrayList<>();
				int element1 = result.get(0); // first triple
				for (int i = 1; i < result.size(); i++) {
					int element2 = result.get(i);
					int element3 = preSTS[element1][element2];
					if (result.contains(element3)) {
						int r = rankSTS[element1][element2];
						I.add(r);
						triples.add(element1);
						triples.add(element2);
						triples.add(element3);
						result.remove(Integer.valueOf(element1));
						result.remove(Integer.valueOf(element2));
						result.remove(Integer.valueOf(element3));
					}
				}
				if (result.size() != 0) { // second triple: size 5 and 6
					int ele1 = result.get(0);
					int ele2 = result.get(1);
					int ele3 = preSTS[ele1][ele2];
					if ((result.size() == 3 && ele3 == result.get(2))
							|| (result.size() == 2 && triples.contains(ele3))) {
						int r = rankSTS[ele1][ele2];
						I.add(r);
					} else {
						throw new IllegalArgumentException("Not a valid result.");
					}
				}
				return true;
			} else { // more than 2 defectives
				List<List<Integer>> triples = new ArrayList<>();
				List<Integer> rCopy = new ArrayList<>(result); // Copy of result to track unused elements

				// Iterate over each pair of elements in result
				for (int i = 0; i < result.size(); i++) {
					for (int j = i + 1; j < result.size(); j++) {
						int first = result.get(i);
						int second = result.get(j);

						// Check if the indices are within the bounds of preSTS
						if (first < preSTS.length && second < preSTS[first].length) {
							int third = preSTS[first][second];

							// Check if the third element is also in the subset S and forms a valid triple
							if (result.contains(third)) {
								List<Integer> triple = new ArrayList<>();
								triple.add(first);
								triple.add(second);
								triple.add(third);
								Collections.sort(triple);

								if (!triples.contains(triple)) { // Directly check if the list contains the triple
									triples.add(triple);
									rCopy.remove(Integer.valueOf(first));
									rCopy.remove(Integer.valueOf(second));
									rCopy.remove(Integer.valueOf(third));
								}
							}
						} else {
							throw new IllegalArgumentException("Not a valid result.");
						}
					}

				}
				if (rCopy.size() != 0) {
					throw new IllegalArgumentException("Not a valid result.");
				} else {
					for (List<Integer> t : triples) {
						int r = rankSTS[t.get(0)][t.get(1)];
						I.add(r);
					}
					return false;
				}

			}
		}
	}

}
