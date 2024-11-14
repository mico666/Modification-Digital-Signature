package cdss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

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
 * This class represents a generic key pair consisting of a private key and a
 * public key. All key pairs used for digital signatures in Bouncy Castle have
 * the common superclass: AsymmetricKeyParameter.
 *
 * @field privateKey The private key of the key pair.
 * @field publicKey The public key of the key pair.
 */

public class KeyPair {
	private AsymmetricKeyParameter privateKey;
	private AsymmetricKeyParameter publicKey;

	// constructor
	public KeyPair(AsymmetricKeyParameter privateKey, AsymmetricKeyParameter publicKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	// getter methods
	public AsymmetricKeyParameter getPrivateKey() {
		return privateKey;
	}

	public AsymmetricKeyParameter getPublicKey() {
		return publicKey;
	}
}
