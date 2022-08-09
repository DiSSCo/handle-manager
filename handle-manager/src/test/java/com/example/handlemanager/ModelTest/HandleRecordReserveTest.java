package com.example.handlemanager.ModelTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.example.handlemanager.model.HandleRecord;
import com.example.handlemanager.model.HandleRecordReserve;

public class HandleRecordReserveTest {
	HandleRecordReserve reserve;
	
	private void genReserveRecord() {
		byte[] handle = "20.5000.1025/123-ABC-456".getBytes();
		reserve = new HandleRecordReserve(handle);
	}
	
	@Test
	public void testRecordCreation() {
		genReserveRecord();
		assertEquals(reserve.getPidStatus(), "RESERVED");
	}
	
	

}
