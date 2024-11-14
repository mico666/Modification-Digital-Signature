package cdss;

import java.security.SecureRandom;
import java.math.BigInteger;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.signers.PSSSigner;

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
 * The RSA class implements the CDSS interface, providing an implementation of
 * the RSA digital signature scheme. This implementation uses RSA-2048 with the
 * RSA-PSS padding scheme.
 */
public class RSA implements CDSS {

	/**
	 * Generates an RSA key pair (private key and public key) using RSA-2048 with a
	 * public exponent of 65537.
	 *
	 * @return the generated KeyPair object containing the private and public keys
	 * @throws Exception if an error occurs during key generation
	 */
	@Override
	public KeyPair KeyGeneration() throws Exception {

		SecureRandom random = new SecureRandom();
		RSAKeyPairGenerator keyGen = new RSAKeyPairGenerator();
		RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(BigInteger.valueOf(0x010001), random, 2048,
				80);
		keyGen.init(params);

		// Generate the RSA key pair
		AsymmetricCipherKeyPair pair = keyGen.generateKeyPair();
		AsymmetricKeyParameter privKey = (AsymmetricKeyParameter) pair.getPrivate();
		AsymmetricKeyParameter publicKey = (AsymmetricKeyParameter) pair.getPublic();

		return new KeyPair(privKey, publicKey);
	}

	/**
	 * Signs the input data using the private key and RSA-PSS with SHA-256.
	 *
	 * @param input   the input data to be signed
	 * @param privKey the private key used for signing
	 * @return a byte array representing the generated signature
	 * @throws Exception if an error occurs during signing
	 */
	@Override
	public byte[] Sign(byte[] bytes, AsymmetricKeyParameter privKey) throws Exception {

		SHA256Digest digest = new SHA256Digest();
		RSAEngine engine = new RSAEngine();
		PSSSigner signer = new PSSSigner(engine, digest, digest.getDigestSize());

		signer.init(true, privKey);
		signer.update(bytes, 0, bytes.length);
		byte[] signature = signer.generateSignature();

		return signature;

	}

	/**
	 * Verifies the signature of the input data using the public key and RSA-PSS
	 * with SHA-256.
	 *
	 * @param input     the input data that was signed
	 * @param signature the signature to be verified
	 * @param publicKey the public key used for verification
	 * @return true if the signature is valid, false otherwise
	 * @throws Exception if the verification process fails
	 */
	@Override
	public boolean Verify(byte[] bytes, byte[] signature, AsymmetricKeyParameter publicKey) throws Exception {

		SHA256Digest digest = new SHA256Digest();
		RSAEngine engine = new RSAEngine();
		PSSSigner verifier = new PSSSigner(engine, digest, digest.getDigestSize());

		verifier.init(false, publicKey);
		verifier.update(bytes, 0, bytes.length);
		boolean result = verifier.verifySignature(signature);
		return result;

	}
}