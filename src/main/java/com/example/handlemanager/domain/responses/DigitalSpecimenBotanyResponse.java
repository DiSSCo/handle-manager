package com.example.handlemanager.domain.responses;

import java.util.List;

import com.example.handlemanager.model.repositoryObjects.Handles;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DigitalSpecimenBotanyResponse extends DigitalSpecimenResponse {
	protected String objectType;
	protected String preservedOrLiving;
	
	public DigitalSpecimenBotanyResponse(List<Handles> entries) {
		super(entries);
		String type;
		for (Handles h: entries) {
			type = h.getType();
			switch(type) {
				case "objectType":
					this.objectType= h.getData();
					break;
				case "preservedOrLiving":
					this.preservedOrLiving = h.getData();
					break;
				default:
					break;
			}
		}
		
	}

}
