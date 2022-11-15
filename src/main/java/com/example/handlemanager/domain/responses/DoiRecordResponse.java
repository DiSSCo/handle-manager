package com.example.handlemanager.domain.responses;

import com.example.handlemanager.repositoryobjects.Handles;
import lombok.Data;

import java.util.List;

@Data
public class DoiRecordResponse extends HandleRecordResponse {
    String referentDoiName;
    String referent;

    public DoiRecordResponse(List<Handles> entries) {
        super(entries);

        String type;

        for (Handles h : entries) {
            type = h.getType();
            if (type.equals(referentDoiName)) {
                this.referentDoiName = h.getData();
            }
            if (type.equals("referent")) {
                this.referent = h.getData();
            }
        }
    }
}
