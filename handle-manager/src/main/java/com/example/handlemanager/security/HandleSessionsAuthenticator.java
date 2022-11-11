package com.example.handlemanager.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;

import lombok.RequiredArgsConstructor;

public class HandleSessionsAuthenticator {
	
	// Here's how authentication of a session works:
	/** 
	 * Client sends unauthenticated request (create, update, delete)
	 * Server returns a sessionId (must be kept secret) and a nonce used once to verify client's identity
	 * Client generates a 16-byte random string (cnonce), uses the private key to sign that cnonce with server's nonce
	 * Re-submits that request
	 * That sessionId can be used to then authenticate subsequent requests
	*/ 
	// These should be pulled from a 401 unauthorized response (json?)
	private final String sessionId; 
	private final String nonceStr; 
	
	
	private SecureRandom secRand = new SecureRandom();
	private byte [] cnonceBytes;
	private String cnonceStr;
	
	private Signature sig = Signature.getInstance("SHA256withRSA"); 
	private String signatureString;
	private String authStr;
	
	private HandleAdmin admin; // Holds admin and key info
	
	public HandleSessionsAuthenticator(String sId, String nStr) throws IOException, GeneralSecurityException{
		sessionId = sId;
		nonceStr = nStr;
		admin = new HandleAdmin();
		createAuthHeader();
	}
	
	
	// HandleSessionInterceptor will call this function to authorize a session
	private void createAuthHeader() throws IOException, GeneralSecurityException {
		// Decode server-assigned nonce
		byte[] nonceBytes = Base64.getDecoder().decode(nonceStr);
		
		// Generate client-side cnonce to sign the nonce with
		cnonceBytes = generateCnonceBytes();
		cnonceStr = encodeToString(cnonceBytes);
		
		// Signature is done with a combination of nonce + cnonce bytes
		byte[] nonceTotalBytes = concatBytes(nonceBytes, cnonceBytes);
		
		byte[] signatureBytes = signNonce(nonceTotalBytes);
		signatureString = encodeToString(signatureBytes);
		
		authStr = buildAuthHeader();
	}
	
	// Generate random 16 bytes with which to sign the server nonce
	private byte[] generateCnonceBytes() {
		final byte[] cNonce = new byte[16];
		secRand.nextBytes(cNonce);
		return cNonce;
	}
	
	// shortcut for byte->string conversion
	private String encodeToString(byte[] bytearr) {
		return Base64.getEncoder().encodeToString(bytearr);
	}
	
	private byte[] concatBytes(byte [] a, byte[] b) throws IOException {
		// Concatenates two byte arrays 
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		
		return c;
		
	}
	
	private byte[] signNonce(byte[] nonce) throws IOException, GeneralSecurityException {
		// get the security objects
		
		PrivateKey pkey = admin.getPrivKey();
		
		
		// Sign the nonce
		sig.initSign(pkey);
		sig.update(nonce);
		byte[] sigBytes = sig.sign();
		
		/* Check that the signature is valid
		System.out.println("Signature:" + Base64.getEncoder().encodeToString(sigBytes));
		
		sig.initVerify(admin.getPubKey());
        sig.update(nonce);

        System.out.println(sig.verify(sigBytes));
		*/
		return sigBytes;
	}
	
	private String buildAuthHeader() {
		String authString = ""
				+ "\"Handle "
				+ "\"version\"=\"0\","
				+ "\"sessionId=\"" + sessionId + ","
				+ "\"cnonce\"=" + cnonceStr + ","
				+ "\"id\"=" + admin.getAdminId() + ","
				+ "\"type\"=\"HS_PUBKEY\","
				+ "\"alg\"=\"SHA256\","
				+ "\"signature\"=" + signatureString +",";
		
		return authString;
	}
	
	// Getters
	public String getSessionId() {
		return sessionId;
	}
	
	public String getAUthHeader() {
		return authStr;
	}
	
}
