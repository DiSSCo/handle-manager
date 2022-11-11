package com.example.handlemanager.domain.responses;

import java.util.List;

import com.example.handlemanager.model.repositoryObjects.Handles;

import lombok.Data;

@Data
public class DigitalSpecimenResponse extends DoiRecordResponse {
	String digitalOrPhysical;
	String specimenHost;
	String inCollectionFacility;
	

	public DigitalSpecimenResponse(List<Handles> entries) {
		super(entries);
		String type;
		String data;

		for (Handles h : entries) {
			type = h.getType();
			data = h.getData();
			switch (type) {
			case "digitalOrPhysical":
				this.digitalOrPhysical = data;
				break;
			case "specimenHost":
				this.specimenHost = data;
				break;
			case "inCollectionFacility":
				this.inCollectionFacility = data;
				break;
			default:
				break;
			}
		}
		
		
	}
	

}
