package com.example.handlemanager.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.example.handlemanager.HandleFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

// This class is a tidy collection of Handles (i.e. a collection of the objects to be published to the server)
//
public class HandleRecord {
	
	@JsonIgnore
	protected final byte[] handle;
	@JsonIgnore
	protected List<Handles> entries;
	@JsonIgnore
	protected long timestamp;
	
	protected String pidStatus;
	
	protected Logger logger = Logger.getLogger(HandleFactory.class.getName());
	
	// 07 f3         00 00 00 15    33 30 30 3a 30 2e 4e 41 2f 32 30 2e 35 30 30 30 2e 31 30 32 35  00 00 00 c8
	//[permisions]	 [---???---]	[ ------------------300:0.NA/20.5000.1025300--------------------]  [--- 200---]
	//private String admin = "07f3000000153330303a302e4e412f32302e353030302e31303235000000c8";
	@JsonIgnore
	private String admin = "0fff000000153330303a302e4e412f32302e353030302e31303235000000c8";
	
	public HandleRecord(byte[] handle) {
		this.handle = handle;
		this.timestamp = Instant.now().getEpochSecond();
		entries = new ArrayList<Handles>();
	}
	
	public HandleRecord(byte[] handle, List<Handles> entries) {
		this.handle = handle;
		this.entries = entries;
	}
	
	protected void setAdminHandle() {
		entries.add(new Handles(handle, 100, "HS_ADMIN".getBytes(), decodeAdmin(), timestamp));
	}
	
	
	public List<Handles> getEntries() {
		return entries;
	}
	
	// TODO: Security risk here
	public void setPermissions(boolean ar, boolean aw, boolean pr, boolean pw) {
		for (Handles h: entries) {
			h.setPermissions(ar, aw, pr, pw);
		}
	}
	
	protected void setEntries() {
		entries = new ArrayList<Handles>();
		entries.add(new Handles(handle, 1, "pidStatus", pidStatus, timestamp));
		setAdminHandle();
	}
	
	public byte[] getHandle() {
		return handle;
	}
	
	@JsonProperty("Handle")
	public String getHandleStr() {
		String handleStr = new String (handle);
		return handleStr;
	}
	
	protected String getDate() {
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-mm-dd");
		LocalDateTime now = LocalDateTime.now();
		return dt.format(now);
	}
	
	@JsonIgnore
	public String getRecordString() {
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
	
	@JsonIgnore
	public boolean isEmpty() {
		return this.entries.isEmpty();
	}
	
	@JsonProperty("PID Status")
	public String getPidStatus() {
		return pidStatus;
	}
	
	public void clearEntries() {
		entries.clear();
	}
	
	
	
}
