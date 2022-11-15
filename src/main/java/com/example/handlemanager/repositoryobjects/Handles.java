package com.example.handlemanager.repositoryobjects;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

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


	// Default values posted to handle server
	// Do not make static, do not remove
	private final int ttl = 86400;
	private final boolean admin_read = true;
	private final boolean admin_write = true;
	private final boolean pub_read = true;
	private final boolean pub_write = false;
	
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

	// for sorting
	@Override
	public int compareTo(Handles h) {
		return Integer.compare(getIdx(), h.getIdx());
	}
	
	public String getHandle() {

		return new String(handle, StandardCharsets.UTF_8);
	}
	
	public byte[] getHandleBytes() {
		return handle;
	}
	
	public int getIdx() {
		return idx;
	}
	
	public String getType() {
		return new String(type, StandardCharsets.UTF_8);
	}

	public String getData() {
		return new String(data, StandardCharsets.UTF_8);
	}
	
	public byte[] getDataBytes() {
		return data;
	}
	
	public String toStringData() {
		final String FORMATTER = "%-30s";
		return String.format("%5s", getIdx()) + " | " +
				String.format(FORMATTER,shrinkString(getType().replace("\n", ""))) + " | " +
				String.format(FORMATTER,getData()).replace("\n", "") + " | " +
				String.format(FORMATTER, String.valueOf(timestamp));
	}
	
	public String toString() {
		return getHandle() +  " | " + toStringData();
	}
	
	private String shrinkString(String s) {
		if (s.length() <= 30) return s;
		return s.substring(0, 30)+"...";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Handles handles = (Handles) o;
		return idx == handles.idx && timestamp == handles.timestamp && Arrays.equals(handle, handles.handle) && Arrays.equals(type, handles.type) && Arrays.equals(data, handles.data);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(idx, timestamp);
		result = 31 * result + Arrays.hashCode(handle);
		result = 31 * result + Arrays.hashCode(type);
		result = 31 * result + Arrays.hashCode(data);
		return result;
	}
}
