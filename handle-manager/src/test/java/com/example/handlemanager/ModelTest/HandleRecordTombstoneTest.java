package com.example.handlemanager.ModelTest;

import org.junit.jupiter.api.Test;

import com.example.handlemanager.model.HandleRecordSpecimen;
import com.example.handlemanager.model.HandleRecordTombstone;

public class HandleRecordTombstoneTest {

	HandleRecordTombstone tombstone;
	
	private void genTombstone() {
		byte[] handle = "20.5000.1025/123-ABC-456".getBytes();
		String tombstoneText = "This was deleted";
		
		tombstone = new HandleRecordTombstone(handle, tombstoneText);
	}
	
	@Test
	public void testInverseRecordCreation() {
		genTombstone();
		HandleRecordTombstone tombstone2 = new HandleRecordTombstone(tombstone.getHandle(), tombstone.getEntries());		
		assert(tombstone.equals(tombstone2));
	}
	
}
