package com.example.handlemanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

// This class is a tidy collection of Handles (i.e. a collection of the objects to be published to the server)

public abstract class HandleRecord {
	
	protected final byte[] handle;
	protected List<Handles> entries;
	
	
	public HandleRecord(byte[] handle) {
		this.handle = handle;
	}
	
	abstract void setEntries();
	
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
	
}
