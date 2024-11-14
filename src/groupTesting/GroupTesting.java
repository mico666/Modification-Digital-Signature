package groupTesting;

import java.util.List;

import cffMatrix.CFFMatrix;

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
 * The GroupTesting class represents a general group testing method for
 * identifying defectives using CFFs. It supports constructing group tests based
 * on a specified CFF construction method and matrix type.
 * 
 * @field n The number of items.
 * @field t The number of tests.
 * @field d The number of defectives (d-CFF).
 * @field m The CFFMatrix object representing the test matrix.
 * @field CFFMethod The method used for CFF construction.
 */
public class GroupTesting {

	private CFFMatrix m;
	protected String CFFMethod;
	protected int n;
	protected int d;
	protected int t;

	// constructor
	public GroupTesting(int n, int d, int t, CFFMatrix m) {
		this.n = n;
		this.d = d;
		this.t = t;
		this.m = m;
	}

	/**
	 * Finds the defectives from the given test results using general decoding
	 * method.
	 *
	 * @param y the result vector of the tests
	 * @param I a list to store the indices of identified defectives
	 * @return true if the number of identified defectives is less than or equal to
	 *         d, false otherwise
	 */
	public boolean findDefectives(int[] y, List<Integer> I) {
		m.findDefectives(y, I, d);
		return I.size() <= d;
	}

	// getter methods
	int getT() {
		return t;
	}

	String getCFFMethod() {
		return CFFMethod;
	}

	int getN() {
		return n;
	}

	int getD() {
		return d;
	}

}
