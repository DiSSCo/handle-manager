package com.example.handlemanager.model.repositoryObjects;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.io.Serializable;
import java.util.Base64;
import java.util.Objects;

// Composite Primary Key for Handles table


@Embeddable
public class HandleIdx implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name="handle")
	@Lob
	@Type(type="org.hibernate.type.BinaryType")
	private byte[] handle;
	
	@Column(name="idx")
	private int idx;
	
	public HandleIdx() {
		
	}
	
	public HandleIdx(byte[] h, int i) {
		handle = h;
		idx = i;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HandleIdx handleIdx = (HandleIdx) o;
		return ((handle == handleIdx.handle) && (idx == handleIdx.idx)); 
	    }

	
	@Override
	public int hashCode() {
		return Objects.hash(handle, idx);
	}
	
	public String toString() {
		return Base64.getEncoder().encodeToString(handle) + " | " + String.valueOf(idx);
	}
	
	// Getters
	public int getIdx() {
		return idx;
	}	
	
	public byte[] getHandle() {
		return handle;
	}
	
	public String getHandleStr() {
		String str = new String(handle);
		return str;
	}
	
	public String getIdxStr() {
		return String.valueOf(idx);
	}
	
	// Setters
	
	public void setIdx(int idx) {
		this.idx = idx;
	}
	
	public void setHandle(byte[] handle) {
		this.handle = handle;
	}

}
