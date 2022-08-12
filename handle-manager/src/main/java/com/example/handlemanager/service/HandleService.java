package com.example.handlemanager.service;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.handlemanager.HandleFactory;
import com.example.handlemanager.model.HandleIdx;
import com.example.handlemanager.model.HandleRecord;
import com.example.handlemanager.model.HandleRecordReserve;
import com.example.handlemanager.model.HandleRecordSpecimen;
import com.example.handlemanager.model.HandleRecordSpecimenMerged;
import com.example.handlemanager.model.HandleRecordSpecimenSplit;
import com.example.handlemanager.model.HandleRecordTombstone;
import com.example.handlemanager.model.Handles;
import com.example.handlemanager.repository.HandleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.logging.*;

//import net.handle.hdllib.HandleException;
//import net.handle.hdllib.HandleValue;
import lombok.RequiredArgsConstructor;

// Postgres value in (value1, value2)..
// Generate all handles before posting...

@Service
@RequiredArgsConstructor
public class HandleService {
	
	@Autowired
	public HandleRepository handleRep;
	private HandleFactory hf = new HandleFactory();
	Logger logger =  Logger.getLogger(HandleService.class.getName());	
	
	
	// Return all handle identifiers
	
	public List<String> getHandles(){
		return getStrList(handleRep.getHandles());
	}
	
	public List<String> getHandles(String pidStatus){
		return getStrList(handleRep.getHandles(pidStatus.getBytes()));
	}
	
	// Create Handle
	public HandleRecord createHandleSpecimen(String url, String digType, String institute) throws JsonProcessingException {
		byte[] h = genHandleList(1).get(0); // TODO fix this? Make an individual function for single handles?
		
		//byte[] h = "20.5000.1025/123-abc".getBytes();
		HandleRecordSpecimen newRecord = new HandleRecordSpecimen(h, url, digType, institute);
		
		// add new handle record to local list of handles
		HandleRecordSpecimen postedRecord = new HandleRecordSpecimen(handleRep.saveAll(newRecord.sortByIdx().getEntries()), h);
		logger.info("New Handle Posted: " + postedRecord.getHandleStr());
		
		return postedRecord;
	}
	
	public List<Handles> createTestHandle(String handle) throws JsonProcessingException {
		/*String handle = "20.5000.1025/123-abc";
		byte[] h = handle.getBytes(); */
		handle = handle.toUpperCase();
		deleteHandleSafe(handle);
		
		String url = hf.newHandle(); // this is to show things change
		url = url.substring(url.length()-9, url.length());
		
		HandleRecord testRecord = new HandleRecordSpecimen(handle.getBytes(), url, "AstronomySpecimen", "NASA");
		handleRep.saveAll(testRecord.getEntries());
		return resolveHandle(handle);
		
		//return resolveHandle(handle);		
	}
	
	
	// Reserve handles
	
	public HashSet<String>reserveHandle(int reserves) {
		List<Handles> reservedRecords = new ArrayList<Handles>();  
		List<byte[]> handleList = genHandleList(reserves); // Mint new handles
		
		// Create list of records to be posted
		for (byte[] h : handleList) {
			reservedRecords.addAll(newReservedHandle(h));
		}
		
		// post the list of handle entries
		handleRep.saveAll(reservedRecords);
		HashSet<String> savedStr = new HashSet<String>();
		for (byte[] h : handleList) {
			savedStr.add(byteToString(h));
		}
		
		return savedStr;
	}
	
	private List<Handles> newReservedHandle(byte[] h) {
		HandleRecord reserveRecord = new HandleRecordReserve(h);		
		//Add new handle string to our record	
		return reserveRecord.sortByIdx().getEntries();
	}

	// Resolve handle

	public HandleRecord resolveHandleRecord(String handle) throws JsonMappingException, JsonProcessingException {
		List<Handles> hList = resolveHandle(handle); // Need to catch this if the timestamp is null
		if (hList.isEmpty()) {
			return new HandleRecord(handle.getBytes());
		}
		
		
		return getRecord(handle.getBytes(), hList);
	}
	
	private List<Handles> resolveHandle(String handle){					
		return handleRep.resolveHandle(handle.getBytes()); 
	}
	
	// Given a list of Handles (of unknown pidStatus), return HandleRecord
	
	private HandleRecord getRecord(byte[] handle, List<Handles> hList) throws JsonMappingException, JsonProcessingException {	
		logger.info("Determining handle record type");
		
		// Get PID status and relation status
		
		String pidStatus = getDataFromType("pidStatus", hList);
		String relationStatus = getDataFromType("pidRelation", hList);
		
		// Use pid Status and relation to extrapolate 
		
		if (pidStatus.equals("RESERVED")) {
			return new HandleRecordReserve(handle);
		}
		if (pidStatus.equals("OBSOLETE")) {
			return new HandleRecordTombstone(handle, hList);
		}
		if ((pidStatus.equals("TEST") | pidStatus.equals("ACTIVE")) && relationStatus.equals("")){
			return new HandleRecordSpecimen(hList, handle);
		}
		if ((pidStatus.equals("TEST") | pidStatus.equals("ACTIVE")) && relationStatus.equals("MERGED")){
			return new HandleRecordSpecimenMerged(hList, handle);
		}
		
		
		logger.warning("Handle pidStatus not been set. Returning best-guess");
		return new HandleRecord(handle, hList); // In case no PID status has been set
	}
	
	// Search list of handle records for the appropriate value
	private String getDataFromType(String type, List<Handles> hList) {
		String data = "";
		for (Handles h: hList) {
			if (h.getType().equals(type)) {
				data = h.getData();
			}
		}
		return data;
	}
	
	
	//Update Handle
	
	public void updateHandle(String handle, int[] idxs, String[] newData) {
		long timestamp = Instant.now().getEpochSecond();
		for (int i = 0; i<idxs.length; i++) {
			
			handleRep.updateHandleRecordData(newData[i].getBytes(), timestamp, handle.getBytes(), idxs[i]);
		}
	}
	
	// Delete Handle	
	public void deleteHandleSafe(String handle) {
		handleRep.deleteAll(resolveHandle(handle));
	}
	
	public HandleRecordTombstone createTombstone(byte[] handleB, String tombstone) {
		//handleRep.flush();
		HandleRecordTombstone tombstoneRecord = new HandleRecordTombstone(handleB, tombstone);
		handleRep.saveAll(tombstoneRecord.sortByIdx().getEntries());		
		return tombstoneRecord;
	}
	
	public HandleRecordTombstone createTombstoneMerged(byte[] handleB, String tombstone, String relatedPid) {
		HandleRecordTombstone tombstoneRecord = new HandleRecordTombstone(handleB, tombstone);
		tombstoneRecord.setRelationStatusMerged(relatedPid);
		handleRep.saveAll(tombstoneRecord.sortByIdx().getEntries());
		return tombstoneRecord;
	}
	
	private List<String> getStrList(List<byte[]> byteList){
		// It's crazy that ArrayList doesn't have a native type converter (especially from byte[] to str?
		// If there's a better way to do this that would be cool
		
		int l = byteList.size();
		List<String> strList = new ArrayList<>(l);
		String s;
		for(byte[] b : byteList){
			s = byteToString(b);
			strList.add(s);
		}
		return strList;
	}
	
	private String byteToString(byte[] b) {
		String str = new String(b, Charset.forName("UTF-8"));
		return str;
	}
	
	// Merge Handle
	
	public HandleRecordSpecimenMerged mergeHandle(List<String> handles, String url, String digitalObjectType, String institute) throws JsonProcessingException {
		List<HandleRecordSpecimen> legacyRecord = new ArrayList<HandleRecordSpecimen>();
		String mergedTombstoneText = "This record was merged with other records.";
		
		byte[] handle = genHandleList(1).get(0);

		
		// Resolve handles to be merged, create tombstone records for them. 
		for (String h : handles) {
			try {
				legacyRecord.add((HandleRecordSpecimen) resolveHandleRecord(h));
			}
			catch(java.lang.ClassCastException e) {
				logger.severe("One of the digital objects to be merged is not viable");
				return null;
			}

			deleteHandleSafe(h);
			createTombstoneMerged(h.getBytes(), mergedTombstoneText, byteToString(handle)); // this might be a problem...
		}
		
		HandleRecordSpecimenMerged mergedRecord = new HandleRecordSpecimenMerged(handle, url, digitalObjectType, institute, legacyRecord);
		handleRep.saveAll(mergedRecord.sortByIdx().getEntries());
		return mergedRecord;
		
	}
	
	// Split Handle
	
	
	public List<HandleRecordSpecimenSplit> splitHandle(String handle, 
			String urlA, String urlB,
			String digTypeA, String digTypeB) throws JsonProcessingException{
		HandleRecordSpecimen parent = (HandleRecordSpecimen) resolveHandleRecord(handle);
		logger.info("Service started");
		String parentHandle = parent.getHandleStr();
		List<byte[]> handleList = genHandleList(2);
		byte[] childHandleA = handleList.get(0);
		byte[] childHandleB = handleList.get(1);
		
		List<String> siblingHandles = getStrList(handleList);
		
		
		digTypeA = setDigType(parent, digTypeA);
		digTypeB = setDigType(parent, digTypeB);
		
		HandleRecordSpecimenSplit childA = new HandleRecordSpecimenSplit(
				childHandleA,
				urlA,
				digTypeA,
				parent.getInstitute(),
				siblingHandles);
		
		HandleRecordSpecimenSplit childB = new HandleRecordSpecimenSplit(
				childHandleB,
				urlB,
				digTypeB,
				parent.getInstitute(),
				siblingHandles);
		
		List<HandleRecordSpecimenSplit> splitHandles = new ArrayList<HandleRecordSpecimenSplit>();
		splitHandles.add(childA);
		splitHandles.add(childB);
		
		saveChildren(splitHandles);
		
		
		// Kill the parent (...That's kind of dark)
		deleteHandleSafe(parentHandle);
		HandleRecordTombstone parentTombstone = createTombstone(parentHandle.getBytes(), "The handle was split.");
		parentTombstone.setRelationStatusSplit(siblingHandles);
		
		return splitHandles;
	}
	
	private void saveChildren(List<HandleRecordSpecimenSplit> children) {
		List<Handles> recordsToSave = new ArrayList<>();
		for (HandleRecordSpecimenSplit c : children) {
			recordsToSave.addAll(c.getEntries());
		}
		
		handleRep.saveAll(recordsToSave);
	}
	
	
	private String setDigType(HandleRecordSpecimen parent, String digType) {
		if (digType.equals("")) return parent.getDigitalObjectSubtype();
		if (!(parent.getDigTypeList().contains(digType))) {
			logger.warning("Invalid digital object type provided. Using parent digital object type");
			return parent.getDigitalObjectSubtype();
		}
		
		return digType;
	}
	
	
	
	
	// Minting Handles
	
	// Mint a single handle
	private byte[] genHandle() {
		byte[] newHandle = hf.newHandleBytes();
		
		return newHandle;
	}
	
	// Mint a list of handles
	private List<byte[]> genHandleList(int h){
		return unwrapBytes(genHandleHash(h));
	}
	
	
	private HashSet<ByteBuffer> genHandleHash(int h){	
		/* Generates a HashSet of minted handles of size h
		 * Calls the handlefactory object for random strings (8 alphanum characters with a dash in the middle)
		 * Checks list of random strings against database, replaces duplicates with new strings
		 * Finally, checks for collisions within the list 
		 * */
		
		// Generate h number of bytes and wrap it into a HashSet<ByteBuffer>
		List<byte[]> handleList = hf.newHandle(h); 
		HashSet<ByteBuffer> handleHash = wrapBytes(handleList); 		
		
		// Check for duplicates from repository and wrap the duplicates
		HashSet<ByteBuffer> duplicates = wrapBytes(handleRep.checkDuplicateHandles(handleList)); 		
		
		// If a duplicate was found, recursively call this function
		// Generate new handles for every duplicate found and add it to our hash list
		if (!duplicates.isEmpty()) { 
			boolean b = handleHash.removeAll(duplicates);						
			b = handleHash.addAll(genHandleHash(duplicates.size())); 
		}
		
		/* It's possible we have a collision within our list now
		 * i.e. on two different recursive cal)ls to this function, we generate the same
		 * If this occurs, we will not have our expected number of handles 
		 * */
		while (h>handleHash.size()) {
			handleHash.addAll(genHandleHash(h-handleHash.size()));
		}
	
		return handleHash;
	}	
	
	// For testing
	private void logHandles(HashSet<ByteBuffer> handles) {
		logger.log(Level.INFO,"Logging "+ String.valueOf(handles.size())+" handles");
		String byteStr;
		
		Iterator<ByteBuffer> itr = handles.iterator();
		
		while(itr.hasNext()){
			byteStr = StandardCharsets.UTF_8.decode(itr.next()).toString();
			logger.log(Level.INFO, "\t" + byteStr);
		}
	}
	
	
	/* List<byte[]> <----> HashSet<ByteBuffer>
	 * HashSets are useful for preventing collisions within the list
	 * List<byte[]> is used to interface with repository layer
	*/
	
	//Converts List<byte[]> --> HashSet<ByteBuffer>
	private HashSet<ByteBuffer> wrapBytes(List<byte[]> byteList){
		HashSet<ByteBuffer> byteHash = new HashSet<ByteBuffer>();
		for(Iterator<byte[]> itr = byteList.iterator(); itr.hasNext();) {
			byteHash.add(ByteBuffer.wrap(itr.next()));
		}	
		return byteHash;
	}
	
	//HashSet<ByteBuffer> --> List<byte[]>
	 private List<byte[]> unwrapBytes(HashSet<ByteBuffer> handleHash){
		 List<byte[]> handleList = new ArrayList<byte[]>();
		 Iterator<ByteBuffer> itr = handleHash.iterator();
		 while(itr.hasNext()) {
			 handleList.add(itr.next().array());	 
		 }
		 return handleList;
		}
	 
}
