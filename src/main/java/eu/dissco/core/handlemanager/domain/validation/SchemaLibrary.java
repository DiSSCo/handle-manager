package eu.dissco.core.handlemanager.domain.validation;

import com.networknt.schema.JsonSchema;

public record SchemaLibrary(
    JsonSchema handleSchema,
    JsonSchema doiSchema,
    JsonSchema annotationSchema,
    JsonSchema dataMappingSchema,
    JsonSchema digitalMediaSchema,
    JsonSchema digitalSpecimenSchema,
    JsonSchema masSchema,
    JsonSchema organisationSchema,
    JsonSchema sourceSystemSchema
) {

}
