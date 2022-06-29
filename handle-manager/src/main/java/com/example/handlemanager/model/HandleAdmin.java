package com.example.handlemanager.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import net.handle.hdllib.AdminRecord;

@Data
public class HandleAdmin implements PrivateKey {
	private static final long serialVersionUID = 1L;
	
	private String adminHandle = "300:0.NA/20.5000.1025";
	private int index = 300;
	private String keyFileName = "/home/soulaine/Documents/Handle txt/privkey.txt";
	private String[] permissions; // Maybe it's one string? sequence of bits indicating permission?
	private String keyStr;
	
	private PrivateKey key;

	public AdminRecord adminRecord;
	
	
	@Override
	public String getAlgorithm() {
		// Returns Name of algorithm, in this case SHA-1 if I'm correct
		return "SHA-1";
	}

	@Override
	public byte[] getEncoded() {
		return null; //return key.getBytes(); // Returns byte-encoded key string...i think
	}

	@Override
	public String getFormat() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public HandleAdmin() throws IOException, GeneralSecurityException {
		readKeyString();
	}
	
	private void readKeyString() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// Read key string from file
		keyStr = new String(Files.readAllBytes(Paths.get(keyFileName)));
		keyStr = keyStr
				.replace("-----BEGIN PRIVATE KEY-----","")
				.replaceAll(System.lineSeparator(), "")
				.replace("-----END PRIVATE KEY-----","");
		
		byte[] encoded = Base64.decodeBase64(keyStr); // Decode b64 encoded string into binary format
		
		////Load encoded string into a key specification class
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(encoded);
		key = keyFactory.generatePrivate(keyspec);
		
	}
	
	public String getKeyStr() {
		return keyStr;
	}
	
	
	 public PrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
		 return key;
	 }
	 
	 
	
	

}
