package com.example.handlemanager.domain.responses;

import java.util.List;

import com.example.handlemanager.model.repositoryObjects.Handles;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DigitalSpecimenResponse extends DoiRecordResponse{
	protected String digitalOrPhysical;
	protected String specimenHost;
	protected String inCollectionFacility;
	
	public DigitalSpecimenResponse(List<Handles> entries) {
		super(entries);
		String type;
		for (Handles h: entries) {
			type = h.getType();
			switch(type) {
				case "digitalOrPhysical":
					this.digitalOrPhysical= h.getData();
					break;
				case "specimenHost":
					this.specimenHost = h.getData();
					break;
				case "inCollectionFacility":
					this.inCollectionFacility = h.getData();
					break;
				default:
					break;
			}
		}
	}

}
