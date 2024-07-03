package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import lombok.Value;

@Value
public class AdminHandleData implements HandleData {

  String format = "admin";
  AdminValue value;

  public AdminHandleData(String prefix) {
    this.value = new AdminValue("0.NA/" + prefix);
  }

  @Value
  protected static class AdminValue {

    String handle;
    int index = 200;
    String permissions = "011111110011";
  }

}
