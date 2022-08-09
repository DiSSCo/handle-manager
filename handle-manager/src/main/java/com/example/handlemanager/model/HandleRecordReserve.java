package com.example.handlemanager.model;

import java.util.ArrayList;
import java.util.List;

// Collection of Handles, reserved records
//
public class HandleRecordReserve extends HandleRecord{
	
	public HandleRecordReserve(byte[] handle) {
		super(handle);
		pidStatus = "RESERVED";
		setEntries();
	}
	
	
}
