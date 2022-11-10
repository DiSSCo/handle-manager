package com.example.handlemanager.repository;

import org.springframework.stereotype.Repository;

import com.example.handlemanager.model.DigitalSpecimen;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.CreateHandleRequest;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.PublicKeyAuthenticationInfo;
import net.handle.hdllib.ResolutionRequest;
import net.handle.hdllib.ResolutionResponse;
import net.handle.hdllib.Util;



@Repository
public class HandleObjectRepositorySDK extends HandleObjectRepository {
	private String userId= "300:0.NA/20.5000.1025"; //Index at which the Pubkey is stored on the admin handle record
	
	
	public void createHandle(byte[] handle, HandleValue[] values) {
		byte[] userIdHandle = (userId.getBytes());
		int userIDIndex = 300; // Index at which 
		
		
	//PublicKeyAuthenticationInfo authInfo = new PublicKeyAuthenticationInfo();
		
		CreateHandleRequest request = new CreateHandleRequest(handle, values, null);
		
	}
	
	public DigitalSpecimen resolveHandle(String handle, byte[][] types, int[] idxs) throws HandleException, JsonMappingException, JsonProcessingException {
		//HandleValue[] values = new HandleResolver().resolveHandle(handle, types, idxs);
	
		byte handleByte[] = Util.encodeString(handle);
		
		// Objects to interface with handle server
		ResolutionRequest request = new ResolutionRequest(handleByte, types, idxs, null);
		HandleResolver resolver = new HandleResolver();
		AbstractResponse response = resolver.processRequest(request);

//		AbstractRequestProcessor processor = new AbstractRequestProcessor();
		
		// 
		String hdlStr = "";
		DigitalSpecimen digRecord = new DigitalSpecimen();
		
		if(response.responseCode == AbstractMessage.RC_SUCCESS) {
			HandleValue values[] = ((ResolutionResponse)response).getHandleValues();
			hdlStr = getRecordString(values);
			digRecord = mapToDS(hdlStr);	
		}
		return digRecord;
	}
	
	protected String getRecordString(HandleValue[] values) {
		// Takes list of handle values and converts it into a json-consumable string
		
		String hdlStr = "{";

		for (int i=0; i<values.length; i++) {
			hdlStr += "\"" + values[i].getTypeAsString() + "\" : \"" + values[i].getDataAsString() + "\", ";
		}
		
		hdlStr = hdlStr.substring(0, hdlStr.length()-2);// Remove trailing comma
		hdlStr += " }"; // close string
		
		return hdlStr;
	}

	@Override
	DigitalSpecimen resolveHandle(String handle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	DigitalSpecimen resolveHandle(String handle, String[] types, int[] indexes) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	String createHandle(String handle, String record) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	// Using handle client?
	
	
	
	
	
	
	/**

	public HandleObjectRepository() {
		// admin params = get admin privledges
		hsClient = HSAdapterFactory.newInstance(); // this should eventually return with appropriate admin rights
	}

	// Create handle record
	public void createHandle(String handle, HandleValue[] values) throws HandleException{  // This method doesn't even have the courtesy to return a response smh
		hsClient.createHandle(handle, values);
	}

	// Resolve handle: supply handle (and maybe types/indexes), returns handle record
	public HandleValue[] resolveHandle(String handle, String[] types, int[] indexes, boolean auth) throws HandleException {
		return hsClient.resolveHandle(handle, types, indexes, auth);
	}

	// Update Handle record
	public void updateHandle(String handle, HandleValue[] values) throws HandleException{
		hsClient.updateHandleValues(handle, values);
	}


	// Delete Handle Record
	public void deleteHandle(String handle) throws HandleException{
		hsClient.deleteHandle(handle);
	}**/


}
