package com.example.handlemanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.handlemanager.exceptions.PidResolutionException;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.example.handlemanager.repository.HandleRepository;

import static com.example.handlemanager.utils.Resources.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PidTypeService {

	@Autowired
	public HandleRepository handleRep;
	
	public String resolveTypePid(String typePid) {
		String record = "";
		try {
			record = resolveTypePidVal(typePid);
		} catch (PidResolutionException e) {
			e.printStackTrace();
		}
		return record;
		
	}
	
	@Cacheable
	private String resolveTypePidVal(String typePid) throws PidResolutionException {

		List<Handles> typeRecord = handleRep.resolveHandle(typePid.getBytes());
		if (typeRecord.isEmpty()) {
			throw new PidResolutionException("Unable to resolve type PID");
		}

		String pid = getDataFromType("pid", typeRecord);
		String primaryNameFromPid = getDataFromType("primaryNameFromPid", typeRecord); // TODO this should be lower case
		String pidType;
		String registrationAgencyDoiName = "";
		String typeJson = "";

		if (pid.contains("doi")) {
			pidType = "doi";
			registrationAgencyDoiName = getDataFromType("registrationAgencyDoiName", typeRecord);

			typeJson = "{ \n" + "\"pid\": \"" + pid + "\", \n" + "\"pidType\": \"" + pidType + "\", \n"
					+ "\"primaryNameFromPid\": \"" + primaryNameFromPid + "\", \n" + "\"registrationAgencyDoiName\": \""
					+ registrationAgencyDoiName + "\" \n" + "}";

		} else if (pid.contains("handle")) {
			pidType = "handle";
			typeJson = "{ \n" + "\"pid\": \"" + pid + "\", \n" + "\"pidType\": \"" + pidType + "\", \n"
					+ "\"primaryNameFromPid\": \"" + primaryNameFromPid + "\" \n" + "}";
		}

		else {
			throw new PidResolutionException(
					"One of the type PIDs provided resolves to an invalid record (reason: neither \"handle\" nor \"doi\" Check handle " + typePid
							+ " and try again");
		}

		if (pidType == "" || primaryNameFromPid == "") { // If one of these were not resolvable
			throw new PidResolutionException(
					"One of the type PIDs provided resolves to an invalid record. reason: pid type and/or primaryNameFromPid are empty. Check handle " + typePid
							+ " and try again");
		}

		return typeJson;
	}

}
