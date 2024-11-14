package cdss;

import org.bouncycastle.pqc.crypto.DigestingMessageSigner;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusKeyPairGenerator;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusSigner;

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
 * The SPHINCSPlus class implements the CDSS interface, providing an
 * implementation of the SPHINCS+ post-quantum digital signature scheme. This
 * implementation uses the parameter set sha2_128s.
 */
public class SPHINCSPlus implements CDSS {

	/**
	 * Generates a SPHINCS+ key pair (private key and public key) using the
	 * parameter set sha2_128s.
	 *
	 * @return the generated KeyPair object containing the private and public keys
	 * @throws Exception if an error occurs during key generation
	 */
	@Override
	public KeyPair KeyGeneration() throws Exception {

		// Create a SPHINCSPlusKeyPairGenerator
		SPHINCSPlusKeyPairGenerator keyGen = new SPHINCSPlusKeyPairGenerator();
		SecureRandom random = new SecureRandom();
		SPHINCSPlusKeyGenerationParameters params = new SPHINCSPlusKeyGenerationParameters(random,
				SPHINCSPlusParameters.sha2_128s);
		keyGen.init(params);


		AsymmetricCipherKeyPair pair = keyGen.generateKeyPair();
		AsymmetricKeyParameter privKey = (AsymmetricKeyParameter) pair.getPrivate();
		AsymmetricKeyParameter publicKey = (AsymmetricKeyParameter) pair.getPublic();

		return new KeyPair(privKey, publicKey);
	}

	/**
	 * Signs the input data using the SPHINCS+ digital signature scheme.
	 *
	 * @param input   the input data to be signed
	 * @param privKey the private key used for signing
	 * @return a byte array representing the generated signature
	 * @throws Exception if an error occurs during signing
	 */
	@Override
	public byte[] Sign(byte[] input, AsymmetricKeyParameter privKey) throws Exception {
		// Create SPHINCSPlusSigner to sign
		SPHINCSPlusSigner sphincsSign = new SPHINCSPlusSigner();
		SHA256Digest digest = new SHA256Digest();
		DigestingMessageSigner digestSigner = new DigestingMessageSigner(sphincsSign, digest);

		digestSigner.init(true, privKey);
		digestSigner.update(input, 0, input.length);
		byte[] signature = digestSigner.generateSignature();

		return signature;

	}

	/**
	 * Verifies the signature of the input data using the SPHINCS+ digital signature
	 * scheme.
	 *
	 * @param input     the input data that was signed
	 * @param signature the signature to be verified
	 * @param publicKey the public key used for verification
	 * @return true if the signature is valid, false otherwise
	 * @throws Exception if the verification process fails
	 */
	@Override
	public boolean Verify(byte[] input, byte[] signature, AsymmetricKeyParameter publicKey) throws Exception {
		// Create SPHINCSplusSigner to verify
		SPHINCSPlusSigner sphincsVerify = new SPHINCSPlusSigner();
		SHA256Digest digest = new SHA256Digest();
		DigestingMessageSigner digestVerify = new DigestingMessageSigner(sphincsVerify, digest);

		digestVerify.init(false, publicKey);
		digestVerify.update(input, 0, input.length);
		boolean result = digestVerify.verifySignature(signature);

		return result;

	}
}
