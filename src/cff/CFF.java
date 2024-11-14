package cff;

import cffMatrix.CFFMatrix;

/**
 * The CFF interface defines the structure for a cover-free families, which
 * includes the values of d (d-CFF), n (the number of columns in CFF), and t
 * (the number of rows in CFF).
 */
public interface CFF {

	abstract int getD();

	abstract int getN();

	abstract int getT();

	/**
	 * Transforms the CFF into a binary matrix data structure.
	 *
	 * @param m the CFFMatrix object to which the CFF will be transferred
	 */
	abstract void toMatrix(CFFMatrix m);

}
