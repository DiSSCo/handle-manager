package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import lombok.Value;

@Value
public class StringHandleData implements HandleData {

  String format = "string";
  String value;
}
