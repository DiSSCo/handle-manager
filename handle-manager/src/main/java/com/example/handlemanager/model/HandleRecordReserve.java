package com.example.handlemanager.model;

import java.util.ArrayList;

// Collection of Handles, reserved records
//
public class HandleRecordReserve extends HandleRecord{
	
	
	public HandleRecordReserve(byte[] handle) {
		super(handle);
		setEntries();
	}

	protected void setEntries() {
		entries = new ArrayList<Handles>();
		entries.add(new Handles(handle, 1, "pidStatus", "RESERVED"));
		setAdminHandle();
		// entries.get(1).setPermissions(false, false, false, false); // Set record to private -> disabled for testing
	}

}
