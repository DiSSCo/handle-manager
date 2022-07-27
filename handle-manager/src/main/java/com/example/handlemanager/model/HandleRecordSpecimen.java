package com.example.handlemanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.example.handlemanager.HandleFactory;

public class HandleRecordSpecimen extends HandleRecord {
	
	// Digital-specimen specific data
	//
	private String url;
	private String digType;
	private String institute;
	
	private Logger logger = Logger.getLogger(HandleFactory.class.getName());
	
	// Should this be stored in a file instead? How do we want to save this data?
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
	
	public HandleRecordSpecimen(byte[] handle, String url, String digType, String institute) {
		super(handle);
		this.url = url;
		if (this.digTypes.contains(digType)) {
			this.digType = digType;
		}
		else {
			this.digType="";
			logger.warning("Digital object type invalid");
		}
		this.institute = institute;
		setEntries();
	}
	
	public HandleRecordSpecimen(List<Handles> entries, byte[] handle) {
		super(handle);
		this.entries = entries;
	}
	
	protected void setEntries() {
		entries = new ArrayList<Handles>(8);
		
		entries.add(new Handles(handle, 1, "URL", url));
		entries.add(new Handles(handle, 2, "pidIssuer", "{\"pid\":\"20.5000.1025/\", \"nameFromPid\":\"DiSSCo\" }"));  // Subject to change
		entries.add(new Handles(handle, 3, "digitalObjectType", digType));  // TODO: Verify digType
		entries.add(new Handles(handle, 4, "issueDate", getDate()));
		entries.add(new Handles(handle, 5, "issueNumber", "1"));
		entries.add(new Handles(handle, 6, "pidStatus", "TEST"));
		entries.add(new Handles(handle, 7, "pidKernelMetadataLicense", "Creative Commons Zero"));
		entries.add(new Handles(handle, 8, "referent", getReferentStr()));
	}
	
	
	private String getReferentStr() {
		String refStr = "{\"referentIdentifiers\": \"\", "
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
		
		return refStr;
	}
	
	
	private String getRor() {
		// TODO: Lookup ror
		return "";
	}
	
	
}
