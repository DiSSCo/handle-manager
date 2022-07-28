package com.example.handlemanager.service;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
import com.example.handlemanager.model.Handles;
import com.example.handlemanager.repository.HandleRepository;

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
	
	
	// Create Handle
	
	public String createHandle(String url, String digType, String institute) {
		byte[] h = genHandleList(1).get(0); // TODO fix this? Make an individual function for single handles?
		
		HandleRecord newRecord = new HandleRecordSpecimen(h, url, digType, institute);
		
		// add new handle record to local list of handles
		HandleRecordSpecimen postedRecord = new HandleRecordSpecimen(handleRep.saveAll(newRecord.getEntries()), h);
		return postedRecord.toString();
		
	}
	
	
	// Reserve handles
	
	public List<String> reserveHandle(int reserves) {
		List<Handles> reservedRecords = new ArrayList<Handles>();  
		List<byte[]> handleList = genHandleList(reserves); // Mint new handles
		
		// Create list of records to be posted
		for (byte[] h : handleList) {
			reservedRecords.addAll(newReservedHandle(h));
		}
		
		// post the list of handle entries
		List<Handles> saved = handleRep.saveAll(reservedRecords);
		List<String> savedStr = new ArrayList<String>();
		for (Handles h : saved) {
			savedStr.add(h.getHandle());
		}
		
		return savedStr;
	}
	
	private List<Handles> newReservedHandle(byte[] h) {
		HandleRecord reserveRecord = new HandleRecordReserve(h);		
		//Add new handle string to our record	
		return reserveRecord.sortByIdx().getEntries();
	}

	
	
	// Resolve handle
	
	public String resolveHandle(String handle){		
		
		List<Handles> hList = handleRep.resolveHandle(handle.getBytes()); 
		HandleRecordSpecimen record = new HandleRecordSpecimen(hList, handle.getBytes());
		
		return record.sortByIdx().toString();
	}	
	
	//Update Handle
	
	public void updateHandle(String handle, int[] idxs, String[] newData) {
		for (int i = 0; i<idxs.length; i++) {
			handleRep.updateHandleRecordData(newData[i].getBytes(), handle.getBytes(), idxs[i]);
		}
	}
	
	
	// Delete Handle
	
	public void deleteHandle(String handle) {
		handleRep.deleteHandleRecord(handle.getBytes());
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
		 * i.e. on two different recursive calls to this function, we generate the same
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
