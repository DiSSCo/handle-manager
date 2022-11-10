package com.example.handlemanager.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.handlemanager.exceptions.PidResolutionException;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.example.handlemanager.repository.HandleRepository;

import lombok.RequiredArgsConstructor;
import static com.example.handlemanager.utils.Resources.*;

@Service
@RequiredArgsConstructor
public class PidTypeService {
	
	@Autowired
	private HandleRepository handleRep;
	
	Logger logger =  Logger.getLogger(PidTypeService.class.getName());	
	
	
	@Cacheable("pidType")
	public String resolveTypePid(String typePid) {
		String pidData = "";
		try{
			pidData = resolveTypePidVal(typePid);
		} catch (PidResolutionException e){
			e.printStackTrace();
		}
		return pidData;
	}
	
	private String resolveTypePidVal(String typePid) throws PidResolutionException {
		
		List<Handles> typeRecord = handleRep.resolveHandle(typePid.getBytes());
		if (typeRecord.isEmpty()){
			throw new PidResolutionException("Unable to resolve type PID");
		}
		
		String pid = getDataFromType("pid", typeRecord);
		String primaryNameFromPid = getDataFromType("primaryNameFromPid", typeRecord);
		String pidType;
		String registrationAgencyDoiName = "";
		String typeJson = "";
		
		if (pid.contains("doi")) {
			pidType = "DOI";
			registrationAgencyDoiName = getDataFromType("registrationAgencyDoiName", typeRecord);
			
			typeJson = "{ \n"
			+ "\"pid\": \"" + pid + "\", \n"
			+ "\"pidType\": \"" + pidType + "\", \n"
			+ "\"primaryNameFromPid\": \"" + primaryNameFromPid + "\", \n"
			+ "\"registrationAgencyDoiName\": \"" + registrationAgencyDoiName + "\" \n"
			+ "}";	
		}
		else if (pid.contains("handle")) {
			pidType = "handle";
			typeJson =  "{ \n"
			+ "\"pid\": \"" + pid + "\", \n"
			+ "\"pidType\": \"" + pidType + "\", \n"
			+ "\"primaryNameFromPid\": \"" + primaryNameFromPid + "\" \n"
			+ "}";
		}
		
		else {
			throw new PidResolutionException("One of the type PIDs provided resolves to an invalid record. Reason: Pid Type in record invalid. Check handle "+typePid+" and try again");
		}
		
		if (pidType == ""|| primaryNameFromPid == ""){ // If one of these were not resolvable
			throw new PidResolutionException("Exception B: One of the type PIDs provided resolves to an invalid record. Reason: pidType and/or primaryNameFromPid not set. Likely cause: invalid PID. Check handle "+typePid+" and try again");
		} 
		//logger.info("this should be cached: " + typePid);
		return typeJson;
	}

}
