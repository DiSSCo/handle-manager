package com.example.handlemanager.model.repositoryObjects;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

// This class is used to interface with the Handle Server Database
//
@Entity
@Table(name="handles")
@IdClass(HandleIdx.class)
public class Handles implements Serializable, Comparable<Handles>{
	private static final long serialVersionUID = 1L;
	
	@Id
	private byte[] handle;
	
	@Id
	private int idx;
	
	@Lob
	@Type(type="org.hibernate.type.BinaryType")
	private byte[] type;
	
	@Lob
	@Type(type="org.hibernate.type.BinaryType")
	private byte[] data;
	
	private long timestamp;
	private final int ttl = 86400;
	
	// Default permissions
	private boolean admin_read = true;
	private boolean admin_write = true;
	private boolean pub_read = true;
	private boolean pub_write = false;
	
	public Handles() {
	}
	
	// String constructor
	public Handles(byte[] handle, int idx, String type, String data, long timestamp) {
		this.handle = handle;
		this.idx = idx;
		this.type = type.getBytes();
		this.data = data.getBytes();
		this.timestamp = timestamp;
	}
	
	// Bytes constructor
	public Handles(byte[] handle, int idx, byte[] type, byte[] data, long timestamp) {
		this.handle = handle;
		this.idx = idx;
		this.type = type;
		this.data = data;
		this.timestamp = timestamp;
	}
	
	//String type, byte data constructor
	public Handles(byte[] handle, int idx, String type, byte[] data, long timestamp) {
		this.handle = handle;
		this.idx = idx;
		this.type = type.getBytes();
		this.data = data;
		this.timestamp = timestamp;
	}
	
	public void setPermissions(boolean ar, boolean aw, boolean pr, boolean pw) {
		admin_read = ar;
		admin_write = aw;
		pub_read = pr;
		pub_write = pw;
	}
	
	// for sorting
	@Override
	public int compareTo(Handles h) {
		return Integer.compare(getIdx(), h.getIdx());
	}
	
	// ToString

	
	public String getHandle() {
		
		String str = new String(handle, StandardCharsets.UTF_8);
		return str;
	}
	
	public byte[] getHandleBytes() {
		return handle;
	}
	
	public int getIdx() {
		return idx;
	}
	
	public String getType() {
		String str = new String(type, StandardCharsets.UTF_8);
		return str;
	}
	
	
	public String getData() {
		String str = new String(data, StandardCharsets.UTF_8);
		return str;
	}
	
	public byte[] getDataBytes() {
		return data;
	}
	
	public String toStringData() {
		return String.format("%5s", getIdx()) + " | " +
				String.format("%-30s",shrinkString(getType().replace("\n", ""))) + " | " +
				String.format("%-30s",getData()).replace("\n", "") + " | " +
				String.format("%-30s", String.valueOf(timestamp));
	}
	
	public String toString() {
		return getHandle() +  " | " + toStringData();
	}
	
	private String shrinkString(String s) {
		if (s.length() <= 30) return s;
		return s.substring(0, 30)+"...";
	}

	@Override
	public boolean equals(Object h){
		if (h == this){ return true; }
		return this.toString().equals(h.toString()); // This is a bit of a cheat here...

	}
	
}
