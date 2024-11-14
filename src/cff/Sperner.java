package cff;

import java.util.Arrays;

/**
 * The Sperner class implements the CFFConstruction interface, providing a
 * method to construct CFFs from Sperner sets. This implementation specifically
 * requires d = 1.
 */
public class Sperner implements CFFConstruction {

	/**
	 * Constructs a CFF from Sperner sets based on the specified parameters. This
	 * method requires that d = 1.
	 *
	 * @param d the number of defectives (must be 1)
	 * @param n the number of items
	 * @return a CFF constructed from Sperner sets
	 * @throws IllegalArgumentException if d is not equal to 1
	 */
	@Override
	public CFF build(int d, int n) {

		if (d != 1) {
			throw new IllegalArgumentException("Sperner's construction requires d = 1");
		} else {
			int t = 1; // Calculate the corresponding rows, which is 't'
			while (binomial(t, t / 2) < n) {
				t++;
			}

			int[] subset = new int[t / 2]; // Calculate the first t-subset of n
			for (int k = 1; k <= subset.length; k++) {
				subset[k - 1] = k;
			}

			int[][] sets = new int[n][t / 2];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < subset.length; j++) {
					sets[i][j] = subset[j];
				}
				subset = subsetLexSucessor(subset, t / 2, t);
			}
			return new CFFSetSystem(d, n, t, sets);
		}
	}

	/**
	 * Computes the next subset in lexicographic order. This method is based on
	 * Algorithm 2.6 from Stinson's Combinatorial Algorithms.
	 *
	 * @param array the current subset
	 * @param t     the size of the subset
	 * @param n     the total number of elements in the set
	 * @return the next subset in lexicographic order, or null if the end is reached
	 */
	public static int[] subsetLexSucessor(int[] array, int t, int n) {

		int[] next = Arrays.copyOf(array, array.length); // a copy of the original array
		int i = t;

		while (i >= 1 && array[i - 1] == n - t + i) {
			i--;
		}
		if (i == 0) {
			return null;
		} else {
			for (int j = i; j <= t; j++) {
				next[j - 1] = array[i - 1] + 1 + j - i;
			}
			return next;
		}

	}

	/**
	 * Computes the binomial coefficient (n choose k).
	 *
	 * @param n the total number of items
	 * @param k the number of items to choose
	 * @return the binomial coefficient (n choose k)
	 */
	public static long binomial(int n, int k) {
		if (k > n - k)
			k = n - k;
		long b = 1;
		for (int i = 1, m = n; i <= k; i++, m--)
			b = b * m / i;
		return b;
	}

}