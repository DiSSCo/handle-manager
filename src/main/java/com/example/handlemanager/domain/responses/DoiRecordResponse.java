package com.example.handlemanager.domain.responses;

import com.example.handlemanager.model.repositoryObjects.Handles;
import lombok.Data;

import java.util.List;

@Data
public class DoiRecordResponse extends HandleRecordResponse {
	String referentDoiName;
	String referent;

	public DoiRecordResponse(List<Handles> entries) {
		super(entries);

		String type;
		String data;

		for (Handles h : entries) {
			type = h.getType();
			data = h.getData();
			switch (type) {
				case "referentDoiName" -> this.referentDoiName = data;
				case "referent" -> this.referent = data;
			}
		}
	}
}