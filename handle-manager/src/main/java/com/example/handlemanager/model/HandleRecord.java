package com.example.handlemanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

// This class is a tidy collection of Handles (i.e. a collection of the objects to be published to the server)
//
public abstract class HandleRecord {
	
	protected final byte[] handle;
	protected List<Handles> entries;
	
	// 07 f3         00 00 00 15    33 30 30 3a 30 2e 4e 41 2f 32 30 2e 35 30 30 30 2e 31 30 32 35  00 00 00 c8
	//[permisions]	 [---???---]	[ ------------------300:0.NA/20.5000.1025300--------------------]  [--- 200---]
	private String admin = "07f3000000153330303a302e4e412f32302e353030302e31303235000000c8";
	
	
	public HandleRecord(byte[] handle) {
		this.handle = handle;
	}
	
	protected void setAdminHandle() {
		entries.add(new Handles(handle, 100, "HS_ADMIN".getBytes(), decodeAdmin()));
	}
	
	
	public List<Handles> getEntries() {
		return entries;
	}
	
	public byte[] getHandle() {
		return handle;
	}
	
	public String getHandleStr() {
		String handleStr = new String (handle);
		return handleStr;
	}
	
	protected String getDate() {
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-mm-dd");
		LocalDateTime now = LocalDateTime.now();
		return dt.format(now);
	}
	
	public String toString() {
		String str = "_____Handle: " + getHandleStr() + "_____\n";
		for (Handles h : entries) {
			str = str + h.toStringData() + "\n";
		}
		
		return str;
	}
	
	public HandleRecord sortByIdx() {
		Collections.sort(entries);
		return this;
	}
	
	
	// Hex -> byte[] conversion 
	// All of this to convert one opaque string??
	private byte[] decodeAdmin() {
		byte[] adminByte = new byte[admin.length()/2];
		for (int i = 0; i < admin.length(); i += 2) {
			adminByte[i / 2] = hexToByte(admin.substring(i, i + 2));
	    }
		return adminByte;		
	}
	
	private byte hexToByte(String hexString) {
	    int firstDigit = toDigit(hexString.charAt(0));
	    int secondDigit = toDigit(hexString.charAt(1));
	    return (byte) ((firstDigit << 4) + secondDigit);
	}
	

	private int toDigit(char hexChar) {
		int digit = Character.digit(hexChar, 16);
		return digit;
	}
	
}
