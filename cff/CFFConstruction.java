package cff;

/**
 * The CFFConstruction interface provides a framework for constructing
 * cover-free families (CFFs).
 */
public interface CFFConstruction {

	/**
	 * Builds a CFF based on the specified parameters.
	 *
	 * @param d the number of defectives
	 * @param n the number of items
	 * @return a CFF constructed with the given parameters
	 */
	CFF build(int d, int n);
}
