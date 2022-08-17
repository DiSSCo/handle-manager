package com.example.handlemanager.model.HandleRecordSpecimen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.example.handlemanager.HandleFactory;
import com.example.handlemanager.model.recordMetadataObjects.MaterialSampleName;
import com.example.handlemanager.model.recordMetadataObjects.NameIdTypeTriplet;
import com.example.handlemanager.model.recordMetadataObjects.Referent;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HandleRecordSpecimen extends HandleRecord {
	
	// From input
	protected String url;
	protected NameIdTypeTriplet pidIssuer;
	protected NameIdTypeTriplet digitalObjectSubtype;
	protected NameIdTypeTriplet digitalObjectType;
	protected String digitalOrPhysical;
	protected NameIdTypeTriplet specimenHost;
	protected NameIdTypeTriplet inCollectionFacility; 
	
	// From input - for Referent
	private MaterialSampleName materialSampleName;
	private NameIdTypeTriplet principalAgent;
	private List<NameIdTypeTriplet> identifier;
	

	//Generated within the class	
	protected String issueDate;
	protected String issueNumber = "1";
	protected Referent referent;
	protected String referentStr;
	protected final String pidKernelMetadataLicense = "CreativeCommonsZero";
	protected String pidStatus = "TEST";

	
	@JsonIgnore
	ObjectMapper objectMapper = new ObjectMapper();

	// Other
	@JsonIgnore
	private Logger logger = Logger.getLogger(HandleFactory.class.getName());
	

	@JsonIgnore
	public final List<String> digSubtypes = new ArrayList<String>() {
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
	
	
	// Constructor from JSON input
	public HandleRecordSpecimen(String url,
			String digitalOrPhysical,
			NameIdTypeTriplet digitalObjectType,
			NameIdTypeTriplet pidIssuer, 
			NameIdTypeTriplet digitalObjectSubtype, 
			// for referent
			MaterialSampleName materialSampleName,
			NameIdTypeTriplet principalAgent,
			List<NameIdTypeTriplet> identifier) throws JsonProcessingException {
		super();
		this.url = url;
		this.digitalObjectType = digitalObjectType;
		this.digitalObjectSubtype = digitalObjectSubtype;
		this.pidIssuer = pidIssuer;
		this.referent = new Referent
				(materialSampleName, 
				digitalOrPhysical, 
				principalAgent, 
				identifier); 
		
		this.referentStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(referent);
		
		setDigitalObjectSubtype(digitalObjectSubtype);
		setDate();
		initializeEntries();
	}
	
	public void setDigitalSpecimenRecord(byte[] handle) throws JsonProcessingException {
		
		this.handle = handle;
		
		this.referent = new Referent
				(materialSampleName, 
				digitalOrPhysical, 
				principalAgent, 
				identifier); 
		
		this.referentStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(referent);
		
		setDigitalObjectSubtype(digitalObjectSubtype);
		setDate();
		initializeEntries();
	}
		
	
	private void initializeEntries() throws JsonProcessingException {
		entries = new ArrayList<Handles>();
		int i = 1;
		
		entries.add(new Handles(handle, i++, "URL", url, timestamp));
		entries.add(new Handles(handle, i++, "pidIssuer", pidIssuer.toString(), timestamp)); 
		entries.add(new Handles(handle, i++, "digitalObjectType", digitalObjectType.toString(), timestamp)); 
		entries.add(new Handles(handle, i++, "digitalObjectSubtype", digitalObjectSubtype.toString(), timestamp)); 
		entries.add(new Handles(handle, i++, "issueDate", issueDate, timestamp));
		entries.add(new Handles(handle, i++, "issueNumber", issueNumber, timestamp));
		entries.add(new Handles(handle, i++, "pidStatus", pidStatus, timestamp));
		entries.add(new Handles(handle, i++, "pidKernelMetadataLicense", pidKernelMetadataLicense, timestamp));
		entries.add(new Handles(handle, i++, "referent", referentStr, timestamp));
		entries.add(new Handles(handle, i++, "digitalOrPhysical", digitalOrPhysical, timestamp));
		entries.add(new Handles(handle, i++, "specimenHost", specimenHost.toString(), timestamp));
		entries.add(new Handles(handle, i++, "inCollectionFacility", inCollectionFacility.toString(), timestamp));
		setAdminHandle();
	}
	
	// For updating?
	public void initializeEntriesNotNull() {
		entries = new ArrayList<Handles>();
		int i = 1;
		if (url != null) {
			entries.add(new Handles(handle, i++, "URL", url, timestamp));
		}
		if (!pidIssuer.isNull()) {
			entries.add(new Handles(handle, i++, "pidIssuer", pidIssuer.toString(), timestamp)); 
		}
		if (!digitalObjectType.isNull()) {
			entries.add(new Handles(handle, i++, "digitalObjectType", digitalObjectType.toString(), timestamp));
		}
		if (!digitalObjectSubtype.isNull()) {
			entries.add(new Handles(handle, i++, "digitalObjectSubtype", digitalObjectSubtype.toString(), timestamp)); 
		}
		if (digitalOrPhysical != null) {
			entries.add(new Handles(handle, i++, "digitalOrPhysical", digitalOrPhysical, timestamp));
		}
		
	}
	
	
	
	public void setDigitalObjectSubtype(NameIdTypeTriplet digitalObjectSubtype) {
		if (this.digSubtypes.contains(digitalObjectSubtype.getPrimaryNameFromPid())) {
			this.digitalObjectSubtype = digitalObjectSubtype;
		}
		else {
			this.digitalObjectSubtype= new NameIdTypeTriplet();
			logger.warning("Digital object type invalid");
		}
	}
	
	
	/*
	public HandleRecordSpecimen(byte[] handle, String url, String subtype, String institute) throws JsonProcessingException {
		super(handle);
		this.url = url;
		
		setDigitalObjectSubtype(subtype);
		setDigSubtypeStr();
		setDate();
		setReferent();
		setPidIssuer(); 
		setEntries();
	} */	
	
	public HandleRecordSpecimen(byte[] handle, List<Handles> entries) throws JsonMappingException, JsonProcessingException {
		super(handle);
		this.entries = entries;
		
		
		String type;
		// If we're using an outdated datamodel, put the string in the "PID" field for any NameIdTypeTriplet
		for (Handles h: entries) {
			type = h.getType();
			switch(type) {
				case "URL":
					this.url = h.getData();
					break;
				case "pidIssuer":
					try {
						this.pidIssuer = objectMapper.readValue(h.getData(), NameIdTypeTriplet.class);
					} catch (UnrecognizedPropertyException | JsonParseException e) {
						this.pidIssuer = new NameIdTypeTriplet();
						this.pidIssuer.setPid(h.getData());
					}
					break;
				case "digitalObjectType":
					try {
						this.digitalObjectType = objectMapper.readValue(h.getData(), NameIdTypeTriplet.class);
					} catch (UnrecognizedPropertyException | JsonParseException e) {
						this.digitalObjectType = new NameIdTypeTriplet();
						this.digitalObjectType.setPid(h.getData());
					}
					break;
				case "digitalObjectSubtype":
					try {
						this.digitalObjectSubtype = objectMapper.readValue(h.getData(), NameIdTypeTriplet.class);
					} catch (UnrecognizedPropertyException | JsonParseException e) {
						this.digitalObjectSubtype = new NameIdTypeTriplet();
						this.digitalObjectSubtype.setPid(h.getData());
					}
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
					this.referentStr = h.getData();
					try {
						this.referent = objectMapper.readValue(h.getData(), Referent.class);
					} catch (UnrecognizedPropertyException | JsonParseException e) {
						break;
					}
					this.materialSampleName = referent.getMaterialSampleName();
					this.principalAgent = referent.getPrincipalAgent();
					this.identifier = referent.getIdentifier();
					break;	
				case "digitalOrPhysical":
					this.digitalOrPhysical = h.getData();
					
				case "specimenHost":
					try {
						this.specimenHost = objectMapper.readValue(h.getData(), NameIdTypeTriplet.class);
					} catch (UnrecognizedPropertyException | JsonParseException e) {
						this.specimenHost = new NameIdTypeTriplet();
						this.specimenHost.setPid(h.getData());
					}
					break;
				case "inCollectionFacility":
					try {
						this.inCollectionFacility = objectMapper.readValue(h.getData(), NameIdTypeTriplet.class);
					} catch (UnrecognizedPropertyException | JsonParseException e) {
						this.inCollectionFacility = new NameIdTypeTriplet();
						this.inCollectionFacility.setPid(h.getData());
					}
				default:
					break;
				}	
		}
	}
	
	/*
	public HandleRecordSpecimen(DigitalSpecimenInput ds, byte[] handle) throws JsonProcessingException {
		super(handle);
		url = ds.getUrl();
		pidIssuer = ds.getPidIssuer().toString();
		digitalObjectSubtypeString = ds.getDigitalObjectSubtype().toString();
		digitalObjectSubtype = ds.getDigitalObjectSubtype().getPrimaryNameFromPid();
		digitalObjectTypeString = ds.getDigitalObjectType().toString();
		
		setReferentPojo(ds);
		setDate();
		setEntries();
	}*/
	
	
	/*
	@Override
	protected void setEntries() {
		entries = new ArrayList<Handles>();
		int i = 1;
		
		entries.add(new Handles(handle, i++, "URL", url, timestamp));
		entries.add(new Handles(handle, i++, "pidIssuer", pidIssuer, timestamp)); 
		entries.add(new Handles(handle, i++, "digitalObjectType", digitalObjectTypeString, timestamp)); 
		entries.add(new Handles(handle, i++, "digitalObjectSubtype", digitalObjectSubtypeString, timestamp)); 
		entries.add(new Handles(handle, i++, "issueDate", issueDate, timestamp));
		entries.add(new Handles(handle, i++, "issueNumber", "1", timestamp));
		entries.add(new Handles(handle, i++, "pidStatus", "TEST", timestamp));
		entries.add(new Handles(handle, i++, "pidKernelMetadataLicense", pidKernelMetadataLicense, timestamp));
		entries.add(new Handles(handle, i++, "referent", referent, timestamp));
		entries.add(new Handles(handle, i++, "digitalOrPhysical", digitalOrPhysical, timestamp));
		setAdminHandle();
	}*/
	
	/*
	private void parseDigitalObjectSubtypeStr(String subtypeStr) throws JsonMappingException, JsonProcessingException {
		NameIdTypeTriplet subtype = objectMapper.readValue(subtypeStr, NameIdTypeTriplet.class);
		logger.info("subtype String: " + subtypeStr + " \t subtype: " + subtype.getPrimaryNameFromPid());
		
		setDigitalObjectSubtype(subtype.getPrimaryNameFromPid());
	}
	
	private void setDigitalObjectSubtype(String subtype) throws JsonProcessingException {
		if (this.digSubtypes.contains(subtype)) {
			this.digitalObjectSubtype = subtype;
		}
		else {
			this.digitalObjectSubtype="";
			logger.warning("Digital object type invalid");
		}
	} */
	
	/*
	private void setDigSubtypeStr() throws JsonProcessingException {
		NameIdTypeTriplet digSubType = new NameIdTypeTriplet(); 
		digSubType.setPrimaryNameFromPid(digitalObjectSubtype);
		
		switch (digitalObjectSubtype) {
		case "ZoologyVertebrateSpecimen":
			digSubType.setPid("21.T11148/ccdb8cda8a785230257e");
			break;
		case "AstronomySpecimen":
			digSubType.setPid("21.T11148/894b1e6cad57e921764e");
			break;
		default:
			digSubType.setPid("");
		}
		
		logger.info("Subtype PID: " + digSubType.getPid());		
		digitalObjectSubtypeString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(digSubType);
		logger.info("Subtype String: "+ digitalObjectSubtypeString);
	}
	
	private String setDigitalObjectTypeString() {
		return "{\n"
				+ "  \"pid\": \"21.T11148/894b1e6cad57e921764e \",  \n"
				+ "  \"pidType\": \"Handle\",  \n"
				+ "  \"primaryNameFromPid\": \"Digital Specimen\",\n"
				+ "}";		
	} */
	
	
	private void setDate() {
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-YYYY");
		issueDate = dateFormat.format(d);
	}	
	
	@JsonIgnore
	public List<String> getDigTypeList(){
		return digSubtypes;
	}
	
	
	public boolean equals(HandleRecordSpecimen spec2) {
		// Not sure if I should also test times
		
		return (getHandleStr().equals(spec2.getHandleStr()) &&
				url.equals(spec2.getUrl()) &&
				pidIssuer.equals(spec2.getPidIssuer()) &&
				digitalObjectType.equals(spec2.getDigitalObjectType()) &&
				digitalObjectSubtype.equals(spec2.getDigitalObjectSubtype())&&
				issueDate.equals(spec2.getIssueDate())&&
				issueNumber.equals(spec2.getIssueNumber())&&
				pidStatus.equals(spec2.getPidStatus()) &&
				referent.equals(spec2.getReferent()));
	}
	
	public void setHandle(byte[] handle) {
		this.handle = handle;
	}
	
}
