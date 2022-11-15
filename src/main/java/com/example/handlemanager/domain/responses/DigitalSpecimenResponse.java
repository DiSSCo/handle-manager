package com.example.handlemanager.domain.responses;

import com.example.handlemanager.repositoryobjects.Handles;
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

        for (Handles h : entries) {
            type = h.getType();
            if (type.equals("digitalOrPhysical")) {
                this.digitalOrPhysical = h.getData();
            }
            if (type.equals("specimenHost")) {
                this.specimenHost = h.getData();
            }
            if (type.equals("inCollectionFacility")) {
                this.inCollectionFacility = h.getData();
            }
        }
    }
}
