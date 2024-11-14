package cff;

/**
 * The RS class implements the CFFConstruction interface, providing a method to
 * construct CFFS from Reed-Solomon codes. This implementation ensures that d >=
 * 2.
 */
public class RS implements CFFConstruction {

	/**
	 * Constructs a CFF from Reed-Solomon codes based on the specified parameters.
	 *
	 * @param d the number of defectives (must be >= 2)
	 * @param n the number of items
	 * @return a CFF constructed from Reed-Solomon codes
	 */
	@Override
	public CFF build(int d, int n) {

		int[] params = { 2, 2, 2 };
		findParameters(n, d, params);
		int k = params[0];
		int N = params[1];
		int q = params[2];

		int[][] OA = generateOA(k, N, q);
		return new CFFCode(d, n, q, OA);
	}

	/**
	 * Finds the optimal values of k, N, and q based on the provided number of items
	 * (n) and defectives (d).
	 *
	 * @param n      the number of items
	 * @param d      the number of defectives
	 * @param params an array to store the calculated values of k, N, and q
	 */
	 void findParameters(int n, int d, int[] params) {

		int k = params[0];
		int N = params[1];
		int q = params[2];

		// Find the optimal values of t, q, k based on d and n.
		// q is a prime number
		// N <= q
		// q^k >= n
		// d <= Math.floor ((N-1)/(q-1))
		// k, q, N are positive integers
		// k>=2 (t-1>0)
		// Goal: N*q be as small as possible
		long product = Long.MAX_VALUE; // Make it the largest at the initial.
		int maxK = (int) Math.ceil(Math.log(n) / Math.log(2));
		if (maxK < 2) {
			maxK = 2;
		}
		for (int interK = 2; interK <= maxK; interK++) {
			int interN = d * (interK - 1) + 1;
			int interValue = (int) Math.ceil(Math.pow(n, 1.0 / interK));
			int interQ = smallestPrimeBiggerThan(Math.max(interN, interValue));
			long interProduct = (long) interN * (long) interQ;
			if (interProduct < product) {
				q = interQ;
				N = interN;
				k = interK;
				product = interProduct;
			}
		}

		params[0] = k;
		params[1] = N;
		params[2] = q;

	}

	/**
	 * Generates Reed-Solomon codes.
	 *
	 * @param t the degree is t-1
	 * @param k the length of codeword
	 * @param q the size of the alphabet (must be a prime number)
	 * @return a 2D array representing the codes
	 */
	int[][] generateOA(int t, int k, int q) {
		// Determine the dimensions of OA: q^t x k
		int numRows = (int) Math.pow(q, t);
		int[][] OA = new int[numRows][k];

		// Determine the coefficients of first polynomial, 0 for every coefficient
		int[] polynomialCoefficients = new int[t];
		for (int i = 0; i < t; i++) {
			polynomialCoefficients[i] = 0;
		}

		// Find the value from the polynomial p(x) for 'x' being {0,1,2,...k-1}
		// The coefficient of the polynomial can choose from 0 to q-1
		// The length of the polynomial is determined by t
		int currRow = 0;
		int polynomialSolution;
		do {
			for (int x = 0; x < k; x++) { // for each row
				polynomialSolution = 0;
				for (int coefficientIndex = 0; coefficientIndex < t; coefficientIndex++) {
					// polynomialSolution += polynomialCoefficients[coefficientIndex]
					// * (int) Math.pow(x, t - coefficientIndex - 1);
					polynomialSolution = polynomialSolution * x + polynomialCoefficients[coefficientIndex]; // horner's
																											// method
				}
				OA[currRow][x] = polynomialSolution % q;
				if (k == q + 1) {
					OA[currRow][q] = polynomialCoefficients[0]; // Set the * column (last column)
				}
			}
			currRow++;

		} while (nextPermutation(q, polynomialCoefficients)); // Method to determine the coefficients for next //
																// polynomial
		return OA;
	}

	/**
	 * Finds the smallest prime number greater than or equal to the specified
	 * number.
	 *
	 * @param num the starting number to search for the next prime
	 * @return the smallest prime number greater than or equal to num
	 */
	public static int smallestPrimeBiggerThan(int num) {
		while (!isPrime(num)) {
			++num;
		}
		return num;
	}

	/**
	 * Checks if a number is prime.
	 *
	 * @param num the number to check
	 * @return true if the number is prime, false otherwise
	 */

	static boolean isPrime(int num) {
		if (num <= 1) {
			return false;
		}
		for (int i = 2; i <= num / 2; i++) {
			if ((num % i) == 0)
				return false;
		}
		return true;
	}

	/**
	 * Generates the next permutation of coefficients in lexicographic order.
	 *
	 * @param n      the upper limit for coefficient values
	 * @param buffer the array of coefficients
	 * @return true if the next permutation exists, false otherwise
	 */
	private static boolean nextPermutation(int n, int[] buffer) {
		int k = buffer.length;
		for (int i = k - 1; i > -1; i--) {
			if (buffer[i] < n - 1) {
				buffer[i]++;
				for (int x = i + 1; x < k; x++) {
					buffer[x] = 0;
				}
				return true;
			}
		}
		return false;
	}

}
