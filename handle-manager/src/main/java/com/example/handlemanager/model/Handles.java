package com.example.handlemanager.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

// This class is used to interface with the Handle Server Database

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
	
	private int ttl = 86400;
	
	// Default permissions
	private boolean admin_read = true;
	private boolean admin_write = true;
	private boolean pub_read = true;
	private boolean pub_write = false;
	
	public Handles() {
	}
	
	// String constructor
	public Handles(byte[] handle, int idx, String type, String data) {
		this.handle = handle;
		this.idx = idx;
		this.type = type.getBytes();
		this.data = data.getBytes();
	}
	
	// Bytes constructor
	public Handles(byte[] handle, int idx, byte[] type, byte[] data) {
		this.handle = handle;
		this.idx = idx;
		this.type = type;
		this.data = data;
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
	
	public String toStringData() {
		return String.format("%5s", getIdx()) + " | " +
				String.format("%-30s",shrinkString(getType().replace("\n", ""))) + " | " +
				String.format("%-30s",getData()).replace("\n", "") ;
	}
	
	public String toString() {
		return getHandle() +  " | " + toStringData();
	}
	
	private String shrinkString(String s) {
		if (s.length() <= 30) return s;
		return s.substring(0, 30)+"...";
	}
}
