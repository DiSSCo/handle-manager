package eu.dissco.core.handlemanager.domain.requests.datacite;

import java.util.Date;

public record DcDate (
    Date date,
    String dateType,
    String dateInformation
){

}
