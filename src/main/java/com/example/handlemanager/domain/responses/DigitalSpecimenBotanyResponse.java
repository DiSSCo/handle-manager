package com.example.handlemanager.domain.responses;

import java.util.List;

import com.example.handlemanager.model.repositoryObjects.Handles;

import lombok.Data;

@Data
public class DigitalSpecimenBotanyResponse extends DigitalSpecimenResponse {
	String objectType;
	String preservedOrLiving;

	public DigitalSpecimenBotanyResponse(List<Handles> entries) {
		super(entries);
		String type;
		String data;

		for (Handles h : entries) {
			type = h.getType();
			data = h.getData();
			switch (type) {
			case "objectType":
				this.objectType = data;
				break;
			case "preservedOrLiving":
				this.preservedOrLiving = data;
				break;
			default:
				break;
			}
		}

	}

}
