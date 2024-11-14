package cff;

/**
 * The STS class implements the CFFConstruction interface, providing a method to
 * construct CFFs from Steiner Triple Systems. This implementation specifically
 * requires d = 2.
 */

public class STS implements CFFConstruction {

	/**
	 * Constructs a CFF from Steiner Triple Systems based on the specified
	 * parameters. This method requires that d = 2.
	 *
	 * @param d the number of defectives (must be 2)
	 * @param b the number of blocks
	 * @return a CFF constructed from Steiner Triple Systems
	 * @throws IllegalArgumentException if d is not equal to 2 or if b is less than
	 *                                  7
	 */
	@Override
	public CFF build(int d, int b) { // d = 2 for STS
		if (d != 2) {
			throw new IllegalArgumentException("STS's construction requires d = 2");
		} else if (b < 7) {
			throw new IllegalArgumentException("STS is only applicable for n bigger or equal to 7");
		} else {
			// Calculate v based on b
			// If v is not congruent to 1 or 3 modulo 6, increment
			int v = (int) Math.ceil((1 + Math.sqrt(1 + 24 * b)) / 2);
			switch (v % 6) {
			case 0:
			case 2: {
				v++;
				break;
			}
			case 4:
			case 5: {
				v = v + 7 - (v % 6);
				break;
			}
			default:
				break;
			}

			int[][] STS = generateSTS(v); // generate STS
			return new CFFSetSystem(d, b, v, STS);
		}
	}

	/**
	 * Generates Steiner Triple Systems (STS) using either Bose or Skolem
	 * construction methods based on the value of v.
	 *
	 * @param v the order of the Steiner Triple System
	 * @return a 2D array representing the blocks of the STS
	 */
	int[][] generateSTS(int v) { // generate STS, from either Bose or Skolem

		int b = v * (v - 1) / 6;
		int[][] blocks = new int[b][3];

		if (v % 6 == 3) { // Bose Constrcution
			int Q = v / 3; // order for quasigroup

			int[][] quasiGroup = new int[Q][Q]; // Calculate the symmetric idempotent quasigroup
			for (int x = 0; x < Q; x++) {
				for (int y = 0; y < Q; y++) {
					quasiGroup[x][y] = (((Q + 1) / 2) * (x + y)) % Q; // the Latin square
				}
			}

			int blockNum = 0;
			for (int x = 0; x <= Q - 1; x++) { // type 1
				blocks[blockNum][0] = 3 * x + 1;
				blocks[blockNum][1] = 3 * x + 2;
				blocks[blockNum][2] = 3 * x + 3;
				blockNum++;
			}

			for (int x = 0; x <= Q - 1; x++) { // type 2
				for (int y = x + 1; y <= Q - 1; y++) {
					for (int i = 0; i < 3; i++) {
						blocks[blockNum][0] = 3 * x + i + 1;
						blocks[blockNum][1] = 3 * y + i + 1;
						blocks[blockNum][2] = 3 * quasiGroup[x][y] + (i + 1) % 3 + 1;
						blockNum++;
					}
				}
			}
		} else if (v % 6 == 1) { // Skolem Construction
			int n = (v - 1) / 6;
			int Q = 2 * n; // order for quasigroup

			int[][] quasiGroup = new int[Q][Q]; // Calculate the symmetric half-idempotent quasigroup
			for (int x = 0; x < Q; x++) {
				for (int y = 0; y < Q; y++) {
					int t = (x + y) % Q;
					if (t % 2 == 0) { // even
						quasiGroup[x][y] = t / 2;
					} else { // odd
						quasiGroup[x][y] = (t + Q - 1) / 2;
					}
				}
			}

			int blockNum = 0;
			int inf = v; // the infinity symbol, which is the largest number in v
			for (int x = 0; x <= n - 1; x++) { // Type 1
				blocks[blockNum][0] = 3 * x + 1;
				blocks[blockNum][1] = 3 * x + 2;
				blocks[blockNum][2] = 3 * x + 3;
				blockNum++;
				for (int i = 0; i < 3; i++) { // Type 2
					blocks[blockNum][0] = inf;
					blocks[blockNum][1] = 3 * (x + n) + i + 1;
					blocks[blockNum][2] = 3 * x + (i + 1) % 3 + 1;
					blockNum++;
				}
			}

			for (int x = 0; x <= Q - 1; x++) { // Type 3
				for (int y = x + 1; y <= Q - 1; y++) {
					for (int i = 0; i < 3; i++) {
						blocks[blockNum][0] = 3 * x + i + 1;
						blocks[blockNum][1] = 3 * y + i + 1;
						blocks[blockNum][2] = 3 * quasiGroup[x][y] + (i + 1) % 3 + 1;
						blockNum++;
					}
				}
			}
		}

		return blocks;
	}

	/**
	 * Stores the third element in each pair.
	 *
	 * @param blocks the blocks of the Steiner Triple System
	 * @param v      the order of the system
	 * @return a 2D array presenting the STS in an alternative format
	 */
	public static int[][] presentationSTS(int[][] blocks, int v) {

		int[][] preSTS = new int[v + 1][v + 1];

		for (int i = 0; i < blocks.length; i++) {
			preSTS[blocks[i][0]][blocks[i][1]] = blocks[i][2];
			preSTS[blocks[i][1]][blocks[i][0]] = blocks[i][2];
			preSTS[blocks[i][0]][blocks[i][2]] = blocks[i][1];
			preSTS[blocks[i][2]][blocks[i][0]] = blocks[i][1];
			preSTS[blocks[i][1]][blocks[i][2]] = blocks[i][0];
			preSTS[blocks[i][2]][blocks[i][1]] = blocks[i][0];
		}
		return preSTS;

	}

	/**
	 * Stores the rank of each pair.
	 *
	 * @param blocks the blocks of the Steiner Triple System
	 * @param v      the order of the system
	 * @return a 2D array ranking the STS blocks
	 */
	public static int[][] locateSTS(int[][] blocks, int v) { // store for ranking, starting with 1 instead of 0

		int[][] rankSTS = new int[v + 1][v + 1];

		for (int i = 0; i < blocks.length; i++) {
			rankSTS[blocks[i][0]][blocks[i][1]] = i + 1;
			rankSTS[blocks[i][1]][blocks[i][0]] = i + 1;
			rankSTS[blocks[i][0]][blocks[i][2]] = i + 1;
			rankSTS[blocks[i][2]][blocks[i][0]] = i + 1;
			rankSTS[blocks[i][1]][blocks[i][2]] = i + 1;
			rankSTS[blocks[i][2]][blocks[i][1]] = i + 1;
		}

		return rankSTS;
	}

}
