package eu.dissco.core.handlemanager.domain.jsonapi;

public class JsonApiFields {

  public static final String NODE_ATTRIBUTES = "attributes";
  public static final String NODE_DATA = "data";
  public static final String NODE_ID = "id";
  public static final String NODE_TYPE = "type";


  private JsonApiFields() {
    throw new IllegalStateException("Utility class");
  }

}
