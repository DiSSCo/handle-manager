package com.example.handlemanager.service;

import com.example.handlemanager.exceptions.PidResolutionException;
import com.example.handlemanager.repository.HandleRepository;
import com.example.handlemanager.repositoryobjects.Handles;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.handlemanager.utils.Resources.getDataFromType;

@Service
@RequiredArgsConstructor
public class PidTypeService {

	public HandleRepository handleRep;
	
	public String resolveTypePid(String typePid) {
		String pidRecord = "";
		try {
			pidRecord = resolveTypePidVal(typePid);
		} catch (PidResolutionException e) {
			e.printStackTrace();
		}
		return pidRecord;
	}
	
	@Cacheable
	private String resolveTypePidVal(String typePid) throws PidResolutionException {
		if (typePid == null) throw new PidResolutionException("Missing PID in request body.");

		List<Handles> typeRecord = handleRep.resolveHandle(typePid.getBytes());
		if (typeRecord.isEmpty()) {
			throw new PidResolutionException("Unable to resolve type PID");
		}

		String pid = getDataFromType("pid", typeRecord);
		String primaryNameFromPid = getDataFromType("primaryNameFromPid", typeRecord);
		String pidType;
		String registrationAgencyDoiName = "";
		String typeJson = "";
		final String NEWLINE = "\", \n";

		if (pid.contains("doi")) {
			pidType = "doi";
			registrationAgencyDoiName = getDataFromType("registrationAgencyDoiName", typeRecord);

			typeJson = "{ \n" + "\"pid\": \"" + pid + NEWLINE + "\"pidType\": \"" + pidType + NEWLINE
					+ "\"primaryNameFromPid\": \"" + primaryNameFromPid + NEWLINE + "\"registrationAgencyDoiName\": \""
					+ registrationAgencyDoiName + NEWLINE + "}";

		} else if (pid.contains("handle")) {
			pidType = "handle";
			typeJson = "{ \n" + "\"pid\": \"" + pid + NEWLINE + "\"pidType\": \"" + pidType + NEWLINE
					+ "\"primaryNameFromPid\": \"" + primaryNameFromPid + NEWLINE + "}";
		} else {
			throw new PidResolutionException(
					"One of the type PIDs provided resolves to an invalid record (reason: neither \"handle\" nor \"doi\" Check handle " + typePid
							+ " and try again");
		}

		if (pidType.equals("") || primaryNameFromPid.equals("")) { // If one of these were not resolvable
			throw new PidResolutionException(
					"One of the type PIDs provided resolves to an invalid record. reason: pid type and/or primaryNameFromPid are empty. Check handle " + typePid
							+ " and try again");
		}

		return typeJson;
	}

}
