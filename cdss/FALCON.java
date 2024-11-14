package cdss;

import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.DigestingMessageSigner;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyPairGenerator;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconSigner;

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
 * The FALCON class implements the CDSS interface, providing an implementation
 * of the FALCON post-quantum digital signature scheme. This implementation uses
 * FALCON-512.
 * 
 */
public class FALCON implements CDSS {
	/**
	 * Generates a FALCON key pair consisting of a private key and a public key
	 * using the FALCON-512 parameters.
	 * 
	 * @return a KeyPair object containing the private key and public key
	 * @throws Exception if an error occurs during key generation
	 */
	@Override
	public KeyPair KeyGeneration() throws Exception {
		
		SecureRandom random = new SecureRandom();
		FalconKeyGenerationParameters params = new FalconKeyGenerationParameters(random, FalconParameters.falcon_512);
		FalconKeyPairGenerator keyGen = new FalconKeyPairGenerator();
		keyGen.init(params);

		// Generate the key pair
		AsymmetricCipherKeyPair pair = keyGen.generateKeyPair();
		AsymmetricKeyParameter privKey = (AsymmetricKeyParameter) pair.getPrivate();
		AsymmetricKeyParameter publicKey = (AsymmetricKeyParameter) pair.getPublic();

		return new KeyPair(privKey, publicKey);
	}

	/**
	 * Performs FALCON signing on the provided input message using the private key.
	 * 
	 * @param input      the input message to be signed
	 * @param privateKey the private/secret key used for signing
	 * @return a byte array representing the generated signature for the input
	 *         message
	 * @throws Exception if an error occurs during signing
	 */
	@Override
	public byte[] Sign(byte[] input, AsymmetricKeyParameter privateKey) throws Exception {
		SHA256Digest digest = new SHA256Digest();
		FalconSigner falconSign = new FalconSigner();
		DigestingMessageSigner digestSigner = new DigestingMessageSigner(falconSign, digest);

		digestSigner.init(true, privateKey);
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
		SHA256Digest digest = new SHA256Digest();
		FalconSigner falconVerify = new FalconSigner();
		DigestingMessageSigner digestVerify = new DigestingMessageSigner(falconVerify, digest);

		digestVerify.init(false, publicKey);
		digestVerify.update(input, 0, input.length);
		boolean result = digestVerify.verifySignature(signature);

		return result;
	}

}
