package com.example.handlemanager.domain.responses;

import com.example.handlemanager.repositoryObjects.Handles;
import lombok.Data;

import java.util.List;

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
				case "digitalOrPhysical" -> this.digitalOrPhysical = data;
				case "specimenHost" -> this.specimenHost = data;
				case "inCollectionFacility" -> this.inCollectionFacility = data;
				default -> {
				}
			}
		}
		
		
	}
	

}
