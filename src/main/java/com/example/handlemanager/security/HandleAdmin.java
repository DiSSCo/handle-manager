package com.example.handlemanager.security;

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
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import net.handle.hdllib.AdminRecord;

// This class holds admin handle information (e.g. adminHandle id) as well as public and private keys.

public class HandleAdmin {	
	protected String adminId = "300:0.NA/20.5000.1025";
	protected int index = 300;
	
	private final String privKeyFileName;
	private String privKeyStr;
	private PrivateKey privKey;
	
	private final String pubKeyFileName;
	private String pubKeyStr;
	private PublicKey pubKey;
	
	public HandleAdmin() throws IOException, GeneralSecurityException {
		privKeyFileName = "/home/soulaine/Documents/Handle txt/privkey.txt";
		pubKeyFileName = "/home/soulaine/Documents/Handle txt/pubkey.txt";
		setKeys();
	}
	
	
	
	private void setKeys() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		setPrivKeyStr();
		setPubKeyStr();
		readPrivateKey();
		readPublicKey();
	}
	
	//Read private key from file
	private void setPrivKeyStr() throws IOException {	
		privKeyStr = new String(Files.readAllBytes(Paths.get(privKeyFileName)));
		privKeyStr = privKeyStr
				.replace("-----BEGIN PRIVATE KEY-----","")
				.replaceAll(System.lineSeparator(), "")
				.replace("-----END PRIVATE KEY-----","");
	}
	
	private void setPubKeyStr() throws IOException {
		// Read key string from file
		pubKeyStr = new String(Files.readAllBytes(Paths.get(pubKeyFileName)));
		pubKeyStr = pubKeyStr
				.replace("-----BEGIN PUBLIC KEY-----","")
				.replaceAll(System.lineSeparator(), "")
				.replace("-----END PUBLIC KEY-----","");
	}
	
	// Read key string and write it to security object
	private void readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {		
		byte[] encoded = Base64.decodeBase64(privKeyStr); // Decode b64 encoded string into binary format
		
		////Load encoded string into a key specification class
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");		
		PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(encoded);
		privKey = keyFactory.generatePrivate(keyspec);
	}
	
	// Read key string and write it to security object
	private void readPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {		
		byte[] encoded = Base64.decodeBase64(pubKeyStr); // Decode b64 encoded string into binary format
		
		////Load encoded string into a key specification class
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keyspec = new X509EncodedKeySpec(encoded);
		pubKey = keyFactory.generatePublic(keyspec);
	}
	
	// Getters
	protected String getPrivKeyStr() {
		return privKeyStr;
	}
	
	
	 protected  PrivateKey getPrivKey() throws IOException, GeneralSecurityException {
		 return privKey;
	 }
	 
	 
	 protected String getPubKeyStr() {
			return pubKeyStr;
		}
		
	 
	 protected  PublicKey getPubKey() throws IOException, GeneralSecurityException {
		 return pubKey;	 
	 } 
	 
	 
	 protected String getAdminId() {
		 return adminId;
	 }
	
	

}
