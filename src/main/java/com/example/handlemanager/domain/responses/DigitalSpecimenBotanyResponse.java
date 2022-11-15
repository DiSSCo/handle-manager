package com.example.handlemanager.domain.responses;

import com.example.handlemanager.repositoryobjects.Handles;
import lombok.Data;

import java.util.List;

@Data
public class DigitalSpecimenBotanyResponse extends DigitalSpecimenResponse {
	String objectType;
	String preservedOrLiving;

	public DigitalSpecimenBotanyResponse(List<Handles> entries) {
		super(entries);
		String type;

		for (Handles h : entries) {
			type = h.getType();
			if (type.equals("objectType")) {
				this.objectType = h.getData();
			}
			if (type.equals("preservedOrLiving")) {
				this.preservedOrLiving = h.getData();
			}
		}
	}
}
