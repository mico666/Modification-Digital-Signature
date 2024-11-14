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
 * The CDSS interface outlines three probabilistic polynomial-time methods: key
 * generation, signing, and verification.
 * 
 * @field privateKey - The secret/private key used for signing.
 * @field publicKey - The public key used for verifying signatures.
 * @field input - The message or data that needs to be signed.
 * @field signature - The generated signature for the given message.
 */
public interface CDSS {
	/**
	 * Generates a pair of keys: a private key for signing and a public key for
	 * verification.
	 * 
	 * @return a KeyPair containing the secret/private key and public key
	 * @throws Exception if key generation fails
	 */
	KeyPair KeyGeneration() throws Exception;

	/**
	 * Creates a digital signature for a given message using the private key.
	 * 
	 * @param input      the message or data that needs to be signed
	 * @param privateKey the secret/private key used for signing
	 * @return a byte array representing the generated signature for the given
	 *         message
	 * @throws Exception if signing fails
	 */
	byte[] Sign(byte[] input, AsymmetricKeyParameter privateKey) throws Exception;

	/**
	 * Verifies the authenticity of a signature for a given message using the public
	 * key.
	 * 
	 * @param input     the message or data that was signed
	 * @param signature the generated signature for the given message
	 * @param publicKey the public key used for verifying signatures
	 * @return true if the signature is valid; false otherwise
	 * @throws Exception if verification fails
	 */
	boolean Verify(byte[] input, byte[] signature, AsymmetricKeyParameter publicKey) throws Exception;
}