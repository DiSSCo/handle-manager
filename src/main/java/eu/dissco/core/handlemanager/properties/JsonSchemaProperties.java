package eu.dissco.core.handlemanager.properties;

import lombok.Data;

@Data
public class JsonSchemaProperties {

  String handleSchemaPath = "json-schema/handle-request-attributes.json";
  String doiSchemaPath = "json-schema/doi-kernel-request-attributes.json";
  String annotationSchemaPath = "json-schema/annotation-request-attributes.json";
  String dataMappingSchemaPath = "json-schema/data-mapping-request-attributes.json";
  String digitalMediaSchemaPath = "json-schema/digital-media-request-attributes.json";
  String digitalSpecimenSchemaPath = "json-schema/digital-specimen-request-attributes.json";
  String masSchemaPath = "json-schema/machine-annotation-service-request-attributes.json";
  String organisationSchemaPath = "json-schema/organisation-request-attributes.json";
  String sourceSystemPath = "json-schema/source-system-request-attributes.json";
}
