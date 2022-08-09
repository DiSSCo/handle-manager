package com.example.handlemanager.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.example.handlemanager.HandleFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class HandleRecordSpecimen extends HandleRecord {
	
	// DiSSCo PID Kernel
	protected String url;
	protected String pidIssuer;
	protected String digitalObjectType;
	protected String loc = "";
	protected String issueDate;
	protected String institute;
	protected String issueNumber = "1";
	protected  final String pidKernelMetadataLicense = "Creative Commons Zero";
	protected String referent;

	// Other
	@JsonIgnore
	private Logger logger = Logger.getLogger(HandleFactory.class.getName());
	

	@JsonIgnore
	public final List<String> digTypes = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add("AnthropologySpecimen");
			add("MedicalHumanTissueSpecimen");
			add("BotanySpecimen");
			add("MycologySpecimen");
			add("GeneticResourcePlantSpecimen");
			add("GeneticResourceAnimalSpecimen");
			add("MicrobiologyuMicroOrganismSpecimen");
			add("ZoologyVertebrateSpecimen");
			add("ZoologyInvertebrateSpecimen");
			add("PaleontologySpecimen");
			add("GeologyRockSpecimen");
			add("GeologyMineralSpecimen");
			add("GeologyMixedSolidMatterSpecimen");
			add("GeologyWaterIceSpecimen");
			add("GeologyLiquidOrGaseousMatterSpecimen");
			add("AstronomySpecimen");
			add("ExperimentalMaterialSpecimen");
		}
	};
	
	public HandleRecordSpecimen(byte[] handle, String url, String digitalObjectType, String institute) {
		super(handle);
		this.pidStatus = "TEST";
		this.url = url;
		this.institute = institute;
		if (this.digTypes.contains(digitalObjectType)) {
			this.digitalObjectType = digitalObjectType;
		}
		else {
			this.digitalObjectType="";
			logger.warning("Digital object type invalid");
		}
		setDate();
		setReferent(institute);
		setPidIssuer(); 
		setEntries();
	}	
	
	public HandleRecordSpecimen(List<Handles> entries, byte[] handle) {
		super(handle);
		this.entries = entries;
		
		String type;
		
		for (Handles h: entries) {
			type = h.getType();
			switch(type) {
				case "URL":
					this.url = h.getData();
					break;
				case "pidIssuer":
					this.pidIssuer = h.getData();
					break;
				case "digitalObjectType":
					this.digitalObjectType = h.getData();
					break;
				case "issueDate":
					this.issueDate = h.getData();
					break;
				case "issueNumber":
					this.issueNumber = h.getData();
					break;
				case "pidStatus":
					this.pidStatus = h.getData();
					break;
				case "referent":
					this.referent = h.getData();
					break;				
				default:
					
				}	
		}
	}
	
	@Override
	protected void setEntries() {
		entries = new ArrayList<Handles>();
		
		entries.add(new Handles(handle, 1, "URL", url, timestamp));
		entries.add(new Handles(handle, 2, "pidIssuer", pidIssuer, timestamp));  // Subject to change
		entries.add(new Handles(handle, 3, "digitalObjectType", digitalObjectType, timestamp));  // TODO: Verify digType
		entries.add(new Handles(handle, 4, "issueDate", issueDate, timestamp));
		entries.add(new Handles(handle, 5, "issueNumber", "1", timestamp));
		entries.add(new Handles(handle, 6, "pidStatus", "TEST", timestamp));
		entries.add(new Handles(handle, 7, "pidKernelMetadataLicense", "Creative Commons Zero", timestamp));
		entries.add(new Handles(handle, 8, "referent", referent, timestamp));
		setAdminHandle();
	}
	
	
	private void setDate() {
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-YYYY");
		issueDate = dateFormat.format(d);
		
	}
	
	private void setReferent(String institute) {
		// This is going to change when we develop a new DOI schema!!!
		referent = "{\"referentIdentifiers\": \"\", "
				+ "\"primaryReferentType\": \"creation\", "
				+ "\"creationName\": \"\", "
				+ "\"structuralType\": \"\", "
				+ "\"mode\": \"tangible\", "
				+ "\"character\": \"other\", "
				+ "\"principalAgent\": \"principalAgent\": "
					+ "{ \"name\": { "
						+ "\"value\": \"" + institute + "\","
						+ "\"type\": \"PrincipalName\"}"
					+ "\"identifier\": "
						+ "[{\"value\": \""+ getRor() + "\", \"type\": \"ROR\"}],"
						+ "\"role\": \"Creator\" }";		
	}
	
	private String getRor() {
		// TODO: Lookup ror
		// This might be obtained through Keycloak
		return "";
	}
	
	public String getDigType(){
		return digitalObjectType;
	}
	
	
	private void setPidIssuer() {
		this.pidIssuer = "{\"pid\":\"20.5000.1025/\", \"nameFromPid\":\"DiSSCo\" }";
	}
	
	// For future use?
	private void setPidIssuer(String pid, String name) {
		this.pidIssuer = "{\"pid\":\""+ pid +"\", \"nameFromPid\":\""+name+"\" }";
	}
	
	@JsonIgnore
	public String getInstitute() {
		return this.institute;
	}
	
	@JsonIgnore
	public List<String> getDigTypeList(){
		return digTypes;
	}


	
	
/*
 * private String url;
	private String pidIssuer;
	private String digitalObjectType;
	private String loc = "";
	private String issueDate;
	private String issueNumber = "1";
	private  String pidStatus = "TEST"; 
	private final String pidKernelMetadataLicense = "Creative Commons Zero";
	private String referent;
 */
	
	public boolean equals(HandleRecordSpecimen spec2) {
		// Not sure if I should also test times
		
		return (getHandleStr().equals(spec2.getHandleStr()) &&
				url.equals(spec2.getUrl()) &&
				pidIssuer.equals(spec2.getPidIssuer()) &&
				digitalObjectType.equals(spec2.getDigitalObjectType())&&
				loc.equals(spec2.getLoc())&&
				issueDate.equals(spec2.getIssueDate())&&
				issueNumber.equals(spec2.getIssueNumber())&&
				pidStatus.equals(spec2.getPidStatus())&&
				referent.equals(spec2.getReferent()));
	}
}
