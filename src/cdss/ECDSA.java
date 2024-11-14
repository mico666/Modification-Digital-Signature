package cdss;

import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.DSADigestSigner;

import java.security.SecureRandom;

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
 * The ECDSA class implements the CDSS interface, providing an implementation of
 * the ECDSA (Elliptic Curve Digital Signature Algorithm) digital signature
 * scheme. This implementation uses the secp256r1 curve (also known as NIST
 * P-256).
 */

public class ECDSA implements CDSS {

	/**
	 * Generates an ECDSA key pair consisting of a private key and a public key
	 * using the secp256r1 curve.
	 * 
	 * @return a KeyPair object containing the private key and public key
	 * @throws Exception if an error occurs during key generation
	 */
	@Override
	public KeyPair KeyGeneration() throws Exception {

		SecureRandom random = new SecureRandom();
		ECKeyPairGenerator keyGen = new ECKeyPairGenerator();
		X9ECParameters secp256 = CustomNamedCurves.getByName("secp256r1");
		ECDomainParameters domainParameters = new ECDomainParameters(secp256.getCurve(), secp256.getG(), secp256.getN(),
				secp256.getH());
		ECKeyGenerationParameters params = new ECKeyGenerationParameters(domainParameters, random);
		keyGen.init(params);

		// Generate the ECDSA key pair
		AsymmetricCipherKeyPair keyPair = keyGen.generateKeyPair();
		AsymmetricKeyParameter privateKey = (AsymmetricKeyParameter) keyPair.getPrivate();
		AsymmetricKeyParameter publicKey = (AsymmetricKeyParameter) keyPair.getPublic();

		return new KeyPair(privateKey, publicKey);
	}

	/**
	 * Performs ECDSA signing on the provided input message using the private key.
	 * 
	 * @param input      the input message to be signed
	 * @param privateKey the private/secret key used for signing
	 * @return a byte array representing the generated signature for the input
	 *         message
	 * @throws Exception if an error occurs during signing
	 */
	@Override
	public byte[] Sign(byte[] input, AsymmetricKeyParameter privKey) throws Exception {
		// Create an ECDSASigner instance for signing
		ECDSASigner signer = new ECDSASigner();
		SHA256Digest digest = new SHA256Digest();
		DSADigestSigner digestSigner = new DSADigestSigner(signer, digest);

		digestSigner.init(true, privKey);
		digestSigner.update(input, 0, input.length);
		byte[] signature = digestSigner.generateSignature();

		return signature;
	}

	/**
	 * Verifies the authenticity of a signature for the given input message using
	 * the provided public key.
	 * 
	 * @param input     the input message to be verified
	 * @param signature the signature to be verified
	 * @param publicKey the public key used for verification
	 * @return true if the signature is valid, false otherwise
	 * @throws Exception if an error occurs during verification
	 */
	@Override
	public boolean Verify(byte[] input, byte[] signature, AsymmetricKeyParameter publicKey) throws Exception {
		// Create an ECDSASigner instance for verification
		ECDSASigner verifier = new ECDSASigner();
		SHA256Digest digest = new SHA256Digest();
		DSADigestSigner digestVerifier = new DSADigestSigner(verifier, digest);

		digestVerifier.init(false, publicKey);
		digestVerifier.update(input, 0, input.length);
		boolean result = digestVerifier.verifySignature(signature);
		
		return result;
	}
}
