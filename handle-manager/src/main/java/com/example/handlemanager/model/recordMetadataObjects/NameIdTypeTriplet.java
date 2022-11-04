package com.example.handlemanager.model.recordMetadataObjects;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.handlemanager.model.HandleRecordSpecimen.HandleRecordKernel;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.example.handlemanager.repository.HandleRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

// Postconstruct called in service -> 
// Immutable java record
// Looking into @Cachable annotation, uses result from previous time
// Public method, public class
// @enablecaching
// https://spring.io/guides/gs/caching/
// Set to check every day, every few hours

@Data
@JsonInclude(Include.NON_NULL)
public class NameIdTypeTriplet {
	@Autowired
	HandleRepository handleRep;
	
	String pid;
	String pidType;
	String primaryNameFromPid;
	String registrationAgencyDoiName;
	
	@JsonIgnore
	String typePid;	
	
	@JsonIgnore
	private static final Logger logger = Logger.getLogger(NameIdTypeTriplet.class.getName() );
	
	// Constructor for DOIs
		public NameIdTypeTriplet(String id, String type, String name, String ra) {
			pid = id;
			pidType = type;
			primaryNameFromPid = name;
			registrationAgencyDoiName = ra;
		}
	
	public NameIdTypeTriplet(String id, String type, String name) {
		pid = id;
		pidType = type;
		primaryNameFromPid = name;
	}
	
	
	public NameIdTypeTriplet(String id, String name) {
		pid = id;
		primaryNameFromPid = name;
	}
	
	public NameIdTypeTriplet(String id) {
		logger.info("constructor called");
		this.typePid = id;
	}
	
	public NameIdTypeTriplet() {
		pid = "";
		pidType="";
		primaryNameFromPid="";
	}
	
	@Override
	public String toString() {
		return "{ \n"
				+ "\"pid\": \"" + pid + "\", \n"
				+ "\"pidType\": \"" + pidType + "\", \n"
				+ "\"primaryNameFromPid\": \"" + primaryNameFromPid + "\" \n"
				+ "}";
	}
	

	public boolean equals(NameIdTypeTriplet obj2) {
		return (pid.equals(obj2.getPid()) &&
				pidType.equals(obj2.getPidType()) &&
				primaryNameFromPid.equals(obj2.getPrimaryNameFromPid()));
		
	}
	
	@JsonIgnore
	public boolean isNull() {
		return (pid == null &&
				pidType == null &&
				primaryNameFromPid == null);
	}

	
	
	
}
