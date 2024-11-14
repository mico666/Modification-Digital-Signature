package cff;

import cffMatrix.CFFMatrix;

/**
 * The CFFCode class implements the CFF interface, providing a representation of
 * constructing a CFF from codes.
 * 
 * @field d The value of d in d-CFF.
 * @field n The number of columns in the CFF.
 * @field alphabet The number of elements in the code (size of the alphabet).
 * @field codes The codes stored in a 2D array, representing the CFF.
 */
public class CFFCode implements CFF {

	private int alphabet; // |Q| = q
	private int[][] codes;
	private int d;
	private int n;

	// constructor
	public CFFCode(int d, int n, int alphabet, int[][] codes) {
		this.d = d;
		this.n = n;
		this.alphabet = alphabet;
		this.codes = codes;
	}

	/**
	 * Transforms the CFF into a matrix data structure.
	 *
	 * @param m the CFFMatrix object to which the CFF will be transferred
	 */
	public void toMatrix(CFFMatrix m) {
		int length = getCodeLength();
		int t = getT();
		m.initialize(t, n);
		for (int i = 0; i < n; i++) { // rows in OA
			for (int j = 0; j < length; j++) { // columns in OA
				int index = j * alphabet + codes[i][j];
				m.set(index, i);
			}
		}
	}

	// getter methods
	public int getAlphabet() {
		return alphabet;
	}

	public int getCodeLength() { // N
		return codes[0].length;
	}

	public int[][] getCodes() {
		return codes;
	}

	@Override
	public int getD() {
		return d;
	}

	@Override
	public int getN() {
		return n;
	}

	@Override
	public int getT() {
		return getCodeLength() * alphabet;
	}

}
