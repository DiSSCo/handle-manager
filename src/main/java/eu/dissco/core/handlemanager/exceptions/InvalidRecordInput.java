package eu.dissco.core.handlemanager.exceptions;

public class InvalidRecordInput extends Exception{

  // Response code = 400 BAD REQUEST

  public InvalidRecordInput(String s) {
    super(s);
  }

}
