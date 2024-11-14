package cff;

import cffMatrix.CFFMatrix;

/**
 * The CFFSetSystem class implements the CFF interface, providing a
 * representation of constructing a CFF from set systems.
 * 
 * @field d The value of d in d-CFF.
 * @field n The number of columns in the CFF.
 * @field t The number of rows in the CFF.
 * @field sets The sets stored in a 2D array, representing the CFF.
 */
public class CFFSetSystem implements CFF {

	private int[][] sets;
	private int d;
	private int n;
	private int t;

	// constructor
	CFFSetSystem(int d, int n, int t, int[][] sets) {
		this.d = d;
		this.n = n;
		this.t = t;
		this.sets = sets;
	}

	/**
	 * Transforms the CFF into a matrix data structure.
	 *
	 * @param m the CFFMatrix object to which the CFF will be transferred
	 */
	public void toMatrix(CFFMatrix m) {
		m.initialize(t, n);
		for (int i = 0; i < n; i++) { // columns
			for (int j = 0; j < sets[0].length; j++) { // rows
				int index = sets[i][j] - 1;
				m.set(index, i);
			}
		}
	}

	// getter methods
	public int getSetSize() {
		return sets[0].length;
	}

	public int[][] getSets() {
		return sets;
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
		return t;
	}

}
