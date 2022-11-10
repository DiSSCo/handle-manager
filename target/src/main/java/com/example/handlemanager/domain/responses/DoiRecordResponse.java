package com.example.handlemanager.domain.responses;

import java.util.List;

import com.example.handlemanager.model.repositoryObjects.Handles;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DoiRecordResponse extends HandleRecordResponse {
	protected String referentDoiName;
	protected String referent;

	public DoiRecordResponse(List<Handles> entries) {
		super(entries);
		String type;
		for (Handles h: entries) {
			type = h.getType();
			switch(type) {
				case "referentDoiName":
					this.referentDoiName= h.getData();
					break;
				case "referent":
					this.referent = h.getData();
					break;
				default:
					break;
			}
		}
	}
}
