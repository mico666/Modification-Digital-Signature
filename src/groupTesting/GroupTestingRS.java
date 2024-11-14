package groupTesting;

import java.util.ArrayList;
import java.util.Arrays;
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
 * The GroupTestingRS class extends GroupTesting, representing a specific group
 * testing method using Reed-Solomon codes RS(k, N, q).
 */
public class GroupTestingRS extends GroupTesting {

	static int startPos = -1; // start position of k consecutive rows
	private int k;
	private int N;
	private int q;

	// constructor
	public GroupTestingRS(int n, int d, int k, int N, int q) { // k, N, q are extra information for RS
		super(n, d, N * q, null);
		this.k = k;
		this.N = N;
		this.q = q;
	}

	/**
	 * Locates defectives from the result vector y using Reed-Solomon codes.
	 *
	 * @param y the result vector
	 * @param I a list to store the indices of defective items
	 * @return true if the number of identified defectives is less than or equal to
	 *         d, false otherwise
	 */
	@Override
	public boolean findDefectives(int[] y, List<Integer> I) {

		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < y.length; i++) {
			if (y[i] == 1) {
				result.add(i);
			}
		}
		List<List<Integer>> S = new ArrayList<>(N);

		for (int i = 0; i < N; i++) {
			S.add(new ArrayList<>());
		}
		for (int i = 0; i < result.size(); i++) {
			S.get((int) Math.floor(result.get(i) / q)).add(result.get(i) % q);
		}

		for (int j = 0; j < N; j++) {
			if (S.get(j).size() > d) {
				System.out.println("RS specific method can only detect up to " + d + " defectives.");
				return false;
			}
		}

		int[][] inverse = ModInverse.findInverse(k, q); // find the inverse matrix

		List<int[]> listPoly = findPolynomials(k, q, N, d, S);

		for (int[] poly : listPoly) {
			int[] G = new int[k];
			int[] f = new int[k];
			for (int i = 0; i < k; i++) {
				f[i] = poly[i];
			}
			G = ModInverse.matrixMultiplication(inverse, f, q);
			int r = G[0];
			for (int i = 1; i < k; i++) {
				r = r * q + G[i];
			}
			r++;
			I.add(r);
		}

		return I.size() <= d;
	}

	/**
	 * Finds a list of polynomials from a list of integer lists representing the
	 * evaluations of the polynomials.
	 *
	 * @param k the degree of the polynomial is k-1
	 * @param q a prime number
	 * @param N the number of evaluations from 0 to N-1
	 * @param d the number of defectives
	 * @param S a list of integer lists representing the evaluations of the
	 *          polynomials
	 * @return a list of evaluations from valid polynomials
	 */
	static List<int[]> findPolynomials(int k, int q, int N, int d, List<List<Integer>> S) {

		List<List<Boolean>> Unused = new ArrayList<>(); // indicates if each element in the row has been used or not
		for (List<Integer> innerList : S) { // initialize Unused (all T)
			List<Boolean> tempList = new ArrayList<>();
			for (int i = 0; i < innerList.size(); i++) {
				tempList.add(true);
			}
			Unused.add(tempList);
		}

		int[] Count = new int[N + 1]; // initialize Count
		int total = 0;
		for (int i = 0; i < N; i++) {
			int temp = S.get(i).size();
			Count[i] = temp;
			total += temp;
		}
		Count[N] = total; // total of Unused

		int[] binomial1 = new int[k]; // binomial with sign in the formula
		for (int a = 1; a <= k; a++) {
			binomial1[a - 1] = (int) Math.pow(-1, a - 1) * choose(k, a);
		}

		int[] binomial2 = new int[k];
		for (int a = 0; a < k; a++) {
			binomial2[a] = (int) Math.pow(-1, a) * choose(k, a);
		}

		List<int[]> listPoly = new ArrayList<>(); // initialize list of polynomials
		int numPolyFound = 0; // keep track of number of polynomials found

		while (Count[N] != 0) { // as long as there are some elements still unused
			int p = -1; // index showing the row in S which still has at least one unused element
			for (int i = 0; i < N; i++) { // find p
				if (Count[i] != 0) {
					p = i;
					break;
				}
			}

			if (p + k <= N) { // find startPos
				startPos = p;
			} else {
				startPos = N - k;
			}

			int[] M = new int[k]; // get the size of M for running our successor
			for (int i = 0; i < k; i++) {
				if (i + startPos == p) {
					M[i] = 1;
				} else if (Count[i + startPos] == d - numPolyFound) {
					M[i] = Count[i + startPos];
				} else {
					M[i] = S.get(i + startPos).size();
				}
			}

			List<int[]> A = new ArrayList<>(); // a list of k arrays A_i of size M[i] containing indices
			for (int i = 0; i < k; i++) { // initialize A
				int[] temp = new int[M[i]];
				for (int j = 0; j < M[i]; j++) {
					temp[j] = j;
				}
				A.add(temp);
			}

			sort(A, Unused, k, p);

			int[] T = new int[k];
			Arrays.fill(T, 0);

			int[] codeword = new int[N]; // initialize codeword

			boolean done = false;
			boolean successorEnd = true;
			while (!done && successorEnd) {
				for (int i = 0; i < k; i++) {
					codeword[startPos + i] = S.get(i + startPos).get(A.get(i)[T[i]]);
				}

				done = isValidPolynomial(k, q, N, S, binomial1, binomial2, codeword);
				successorEnd = successor(T, M, k);
			}

			if (!done) {
				throw new IllegalArgumentException("No polynomials found");
			}
			for (int i = 0; i < N; i++) { // update unused
				int index = S.get(i).indexOf(codeword[i]);
				boolean temp = Unused.get(i).get(index);
				if (temp == true) {
					Unused.get(i).set(index, false);
					Count[i]--;
					Count[N]--;
				}
			}
			numPolyFound++;
			listPoly.add(codeword);
		}
		return listPoly;
	}

	/**
	 * Checks if a polynomial is valid or not.
	 *
	 * @param k         the degree of polynomials is k-1
	 * @param q         a prime number
	 * @param N         the number of evaluations from 0 to N-1
	 * @param S         a list of integer lists representing the evaluations of the
	 *                  polynomials
	 * @param binomial1 the first set of binomial coefficients with signs
	 * @param binomial2 the second set of binomial coefficients with signs
	 * @param codeword  an array of evaluations from polynomials
	 * @return true if the polynomial is valid, false otherwise
	 */
	static boolean isValidPolynomial(int k, int q, int N, List<List<Integer>> S, int[] binomial1, int[] binomial2,
			int[] codeword) {
		int current = startPos + k;
		for (int i = current; i < N; i++) {
			int f = 0;
			for (int j = 1; j <= k; j++) {
				int previous = i - j;
				f += binomial1[j - 1] * codeword[previous];
			}
			f = (f % q + q) % q;

			if (S.get(i).contains(f)) {
				codeword[i] = f;
			} else {
				return false;
			}
		}
		current = startPos - 1;

		for (int i = current; i >= 0; i--) {
			int f = 0;
			int index = i + k;
			for (int j = 0; j < k; j++) {
				f += binomial2[j] * codeword[index];
				index--;
			}
			f = (int) (Math.pow(-1, k + 1) * f % q + q) % q;
			if (S.get(i).contains(f)) {
				codeword[i] = f;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Finds the successor of a mixed-radix k-tuple T in lexicographical order.
	 *
	 * @param T    the current mixed-radix k-tuple
	 * @param size the size array for each radix position
	 * @param k    the number of elements in the tuple
	 * @return true if a successor exists, false otherwise
	 */
	static boolean successor(int[] T, int[] size, int k) {
		int i = k - 1;
		while ((i >= 0) && T[i] == size[i] - 1) {
			T[i] = 0;
			i--;
		}
		if (i < 0) {
			return false;
		} else {
			T[i]++;
			return true;
		}

	}

	/**
	 * Rearranges the Unused elements to ensure valid combinations for polynomials.
	 *
	 * @param A      a list of integer arrays containing indices
	 * @param Unused a list indicating which elements have been used
	 * @param k      the number of rows to arrange
	 * @param p      the current row index
	 */
	static void sort(List<int[]> A, List<List<Boolean>> Unused, int k, int p) {
		for (int i = 0; i < k; i++) {
			int offset = startPos + i;
			int s = Unused.get(offset).size();
			int left = 0;
			int right = s - 1;
			if (offset == p) {
				for (int j = 0; j < s; j++) {
					if (Unused.get(offset).get(j)) {
						A.get(i)[0] = j;
						break;
					}
				}
			} else {
				for (int j = 0; j < s; j++) {
					if (Unused.get(offset).get(j)) {
						A.get(i)[left] = j;
						left++;
					} else if (right >= A.get(i).length) {
						right--;
					} else {
						A.get(i)[right] = j;
						right--;

					}
				}
			}
		}
	}

	/**
	 * Computes the binomial coefficient (n choose k).
	 *
	 * @param n the total number of items
	 * @param k the number of items to choose
	 * @return the binomial coefficient (n choose k)
	 */
	static int choose(int n, int k) {
		if (k == 0) {
			return 1;
		} else if (k > n / 2) {
			return choose(n, n - k);
		} else {
			int result = n;
			for (int i = 2; i <= k; i++) {
				result *= (n - i + 1);
				result /= i;
			}
			return result;
		}
	}

}
