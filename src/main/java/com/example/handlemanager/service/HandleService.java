package com.example.handlemanager.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.handlemanager.HandleFactory;
import com.example.handlemanager.model.DigitalSpecimen;
import com.example.handlemanager.repository.HandleObjectRepository;
import com.example.handlemanager.repository.HandleObjectRepositorySDK;
import com.example.handlemanager.repository.HandleObjectRepositoryWeb;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import net.handle.hdllib.HandleException;
//import net.handle.hdllib.HandleValue;
import lombok.RequiredArgsConstructor;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleValue;



@Service
@RequiredArgsConstructor
public class HandleService {
	
	//Ugh this is hardcoded but I'll deal with it later
	private List<String> typeList = Arrays.asList(new String[] {
			"URL",
			"OBJECT_TYPE",
			"CURATED_OBJECT_ID",
			"CURATED_OBJECT_ID_TYPE",
			"SPECIMEN_NAME",
			"ORGANIZATION_CODE",
			"RECORD_ID"
	});
	
	@Autowired
	public HandleObjectRepositoryWeb repositoryWeb;
	public HandleObjectRepositorySDK repositorySDK;
	public HandleFactory handleFactory = new HandleFactory();
	
	
	// API Methods
	
	public String resolveHandleWeb(String handle) throws JsonMappingException, JsonProcessingException {
		//return ("Handle = " + handle);
		return repositoryWeb.resolveHandleWeb(handle);		
		
	}
	
	public String createHandleWeb() {
		
		//String handle = handleFactory.newHandle();
		String handle = "20.5000.1025/test-abcd";
		
		return repositoryWeb.createHandle(handle, "");
		
	}
	
	
	
	/*
	
	public String createHandle() throws IOException, InterruptedException, URISyntaxException {
		//HandleValue [] values = toHandleValueList(digSpeg);
		String handle = handleFactory.newHandle();		
		return repository.createHandle(handle);

	}*/
	
	// SDK Methods
	
	// Resolve handle: supply handle (and maybe types/indexes), returns handle record
	
	public DigitalSpecimen resolveHandle(String handle, String[] types, int[] indexes) throws IOException, InterruptedException, HandleException {	
		if (types!= null && !checkTypes(Arrays.asList(types))) {
			//return("Error: Invalid type parameters");  //TODO Should identify which values are not found here
		}
		byte[][] typeBs = (types == null ? null: typeBytes(types)); // Convert to list of bytes[] so we can run this through the resolver (if types is null don't do this)
		
		return repositorySDK.resolveHandle(handle, typeBs, indexes);
	}
	

	public String createHandle(HandleValue[] hvArr) {
		
		for (int i=0;i<hvArr.length;i++) {
			System.out.println(hvArr[i].getTypeAsString() + ":" + hvArr[i].getDataAsString());
		}
		
		
		return null;
	}
	
	private boolean checkTypes(List<String> types) { // returns false if there is an incorrect type included
		if (types == null) return true;
		return types.stream().allMatch(element -> typeList.contains(element));
	}
	
	public byte[][] typeBytes(String[] types){
		byte typeByte[][] = new byte[types.length][];
		for (int i = 0; i<types.length; i++) {
			typeByte[i] = types[i].getBytes();	
		}
		return typeByte;
	}
 

	/**
	
	private ObjectMapper mapper;
	private HandleFactory handleFactory = new HandleFactory();

	// Create Handle Record
	public void createHandle(DigitalSpecimen digSpeg) throws HandleException {
		HandleValue [] values = toHandleValueList(digSpeg);
		String handle = handleFactory.newHandle();
		repository.createHandle(handle, values);

	}

	//Update Handle Record (Full)
	public void updateHandle(String handle, DigitalSpecimen digSpec) throws HandleException{
		HandleValue[] values = toHandleValueList(digSpec);
		repository.updateHandle(handle, values);

	}




	public void deleteHandle(String handle) throws HandleException{
		repository.deleteHandle(handle);
	} */






}
