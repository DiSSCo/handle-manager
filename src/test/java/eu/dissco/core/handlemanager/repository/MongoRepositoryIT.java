package eu.dissco.core.handlemanager.repository;


import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_MEDIA_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMongoDocument;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdatedFdoRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class MongoRepositoryIT {

  private static final String SPECIMEN_ID = PREFIX + "/SPECIMEN";
  private static final String MEDIA_ID = PREFIX + "/MEDIA";

  private static final DockerImageName MONGODB =
      DockerImageName.parse("mongo:7.0.12");

  @Container
  private static final MongoDBContainer CONTAINER = new MongoDBContainer(MONGODB);
  private MongoDatabase database;
  private MongoClient client;
  private MongoCollection collection;
  private MongoRepository repository;

  @BeforeEach
  void prepareDocumentStore() {
    client = MongoClients.create(CONTAINER.getConnectionString());
    database = client.getDatabase("dissco");
    collection = database.getCollection("handles");
    repository = new MongoRepository(collection, MAPPER);
  }

  @AfterEach
  void disposeDocumentStore() {
    database.drop();
    client.close();
  }

  @Test
  void testPostEmptyList() {
    // When / Then
    assertDoesNotThrow(() -> repository.postHandleRecords(List.of()));
  }

  @Test
  void testUpdateEmptyList() {
    // When / Then
    assertDoesNotThrow(() -> repository.updateHandleRecords(List.of()));
  }

  @Test
  void testGetHandleRecordsHandle() throws Exception {
    // Given
    populateMongoDB();
    var expected = List.of(givenHandleFdoRecord(HANDLE));

    // When
    var result = repository.getHandleRecords(List.of(HANDLE));

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testGetHandleRecordsSpecimen() throws Exception {
    // Given
    populateMongoDB();
    var expected = List.of(givenDigitalSpecimenFdoRecord(SPECIMEN_ID));

    // When
    var result = repository.getHandleRecords(List.of(SPECIMEN_ID));

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testGetHandleRecordsMedia() throws Exception {
    // Given
    populateMongoDB();
    var expected = List.of(givenDigitalMediaFdoRecord(MEDIA_ID));

    // When
    var result = repository.getHandleRecords(List.of(MEDIA_ID));

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testGetHandleRecordsNotFound() throws Exception {
    // Given
    populateMongoDB();

    // When
    var result = repository.getHandleRecords(List.of(HANDLE_ALT));

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void testGetExistingHandles() throws Exception {
    // Given
    populateMongoDB();

    // When
    var result = repository.getExistingHandles(List.of(HANDLE));

    // Then
    assertThat(result).isEqualTo(List.of(HANDLE));
  }

  @Test
  void testGetExistingHandlesNotFound() throws Exception {
    // Given
    populateMongoDB();

    // When
    var result = repository.getExistingHandles(List.of(HANDLE_ALT));

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void testPostHandleRecords() throws Exception {
    // Given
    var fdoRecord = givenDigitalSpecimenFdoRecord(HANDLE);
    var expected = givenMongoDocument(fdoRecord);

    // When
    repository.postHandleRecords(List.of(expected));
    var result = collection.find(eq("_id", HANDLE)).first();

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateHandleRecords() throws Exception {
    // Given
    var specimenDoc = givenMongoDocument(givenDigitalSpecimenFdoRecord(HANDLE));
    collection.insertOne(specimenDoc);
    var expected = givenMongoDocument(givenUpdatedFdoRecord(FdoType.DIGITAL_SPECIMEN,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));

    // When
    repository.updateHandleRecords(List.of(expected));
    var result = collection.find(eq("_id", HANDLE)).first();

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testSearchByPrimaryLocalIdSpecimen() throws Exception {
    // Given
    populateMongoDB();
    var expected = List.of(givenDigitalSpecimenFdoRecord(SPECIMEN_ID));

    // When
    var result = repository.searchByPrimaryLocalId(FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID.get(),
        List.of(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testSearchByPrimaryLocalIdMedia() throws Exception {
    // Given
    populateMongoDB();
    var expected = List.of(givenDigitalMediaFdoRecord(MEDIA_ID));

    // When
    var result = repository.searchByPrimaryLocalId(FdoProfile.PRIMARY_MEDIA_ID.get(),
        List.of(PRIMARY_MEDIA_ID_TESTVAL));

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testRollbackHandles() throws Exception {
    // Given
    populateMongoDB();
    var idList = List.of(HANDLE, SPECIMEN_ID, MEDIA_ID);

    // When
    repository.rollbackHandles(idList);
    var result = collection.find(in("_id", idList));

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void testRollbackHandlesFromPhysicalId() throws Exception {
    // Given
    populateMongoDB();

    // When
    var rollbackCount = repository.rollbackHandlesFromLocalId(
        FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID.get(),
        List.of(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));
    var result = collection.find(in("_id", SPECIMEN_ID));

    // Then
    assertThat(result).isEmpty();
    assertThat(rollbackCount).isEqualTo(1);
  }

  @Test
  void testFdoTypeNotFound() throws Exception {
    // Given
    var fdoRecord = new FdoRecord(HANDLE, FdoType.HANDLE,
        List.of(new FdoAttribute(FdoProfile.FDO_RECORD_LICENSE_NAME, CREATED, "License")), null);
    collection.insertOne(givenMongoDocument(fdoRecord));
    var handleList = List.of(HANDLE);

    // When / Then
    assertThrows(IllegalStateException.class, () -> repository.getHandleRecords(handleList));
  }

  private void populateMongoDB() throws Exception {
    var handleDoc = givenMongoDocument(givenHandleFdoRecord(HANDLE));
    var specimenDoc = givenMongoDocument(givenDigitalSpecimenFdoRecord(SPECIMEN_ID));
    var mediaDoc = givenMongoDocument(givenDigitalMediaFdoRecord(MEDIA_ID));
    collection.insertOne(handleDoc);
    collection.insertOne(specimenDoc);
    collection.insertOne(mediaDoc);
  }


}