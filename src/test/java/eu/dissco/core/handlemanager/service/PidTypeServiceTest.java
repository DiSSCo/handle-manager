package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PTR_DOI_RECORD;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PTR_HANDLE_RECORD;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PTR_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PTR_PID_DOI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PTR_PRIMARY_NAME;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PTR_REGISTRATION_DOI_NAME;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PTR_TYPE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PTR_TYPE_DOI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PidTypeServiceTest {
  // NOTE: Pid Type Record => PTR in naming convention because these
  // PTR refers to the handle record that stores information about a type

  ObjectMapper mapper;
  @Mock
  private HandleRepository handleRep;
  @Mock
  private Clock clock;
  private PidTypeService pidTypeService;
  private String pid;
  private String pidType;
  private String primaryNameFromPid;
  private String registrationAgencyDoiName;
  private byte[] recordPid;
  private List<HandleAttribute> typeRecord;


  @BeforeEach
  void init() {
    mapper = new ObjectMapper();
    pidTypeService = new PidTypeService(handleRep, mapper);
  }

  @Test
  void testPidTypeRecordResolutionHandle() throws Exception {
    // Given
    initTestPidTypeRecordHandle();

    given(handleRep.resolveHandleAttributes(recordPid)).willReturn(typeRecord);

    // When
    String returned = pidTypeService.resolveTypePid(PID_ISSUER_PID);

    // Then
    assertThat(returned).isEqualTo(PTR_HANDLE_RECORD);
  }

  @Test
  void testPidTypeRecordResolutionDoi() throws Exception {
    //Given
    initTestPidTypeRecordDoi();
    String expected = PTR_DOI_RECORD;
    given(handleRep.resolveHandleAttributes(recordPid)).willReturn(typeRecord);

    // When
    String returned = pidTypeService.resolveTypePid(PID_ISSUER_PID);

    // Then
    assertThat(expected).isEqualTo(returned);
  }

  @Test
  void testResolutionException() throws Exception {
    // Given
    String typePid = PID_ISSUER_PID;
    given(handleRep.resolveHandleAttributes(typePid.getBytes())).willReturn(new ArrayList<>());

    // Then
    assertThrows(PidResolutionException.class, () -> {
      pidTypeService.resolveTypePid(typePid);
    });
  }

  private void initTestPidTypeRecordHandle() {
    recordPid = PID_ISSUER_PID.getBytes(StandardCharsets.UTF_8);
    pid = PTR_PID;
    pidType = PTR_TYPE;
    primaryNameFromPid = PTR_PRIMARY_NAME;

    typeRecord = initTestPidTypeRecord(false);
  }

  private void initTestPidTypeRecordDoi() {
    recordPid = PID_ISSUER_PID.getBytes(StandardCharsets.UTF_8);
    pid = PTR_PID_DOI;
    pidType = PTR_TYPE_DOI;
    primaryNameFromPid = PTR_PRIMARY_NAME;
    registrationAgencyDoiName = PTR_REGISTRATION_DOI_NAME;

    typeRecord = initTestPidTypeRecord(true);
  }

  private List<HandleAttribute> initTestPidTypeRecord(boolean isDoi) {
    List<HandleAttribute> record = new ArrayList<>();

    record.add(new HandleAttribute(1, recordPid, "pid", pid.getBytes(StandardCharsets.UTF_8)));
    record.add(
        new HandleAttribute(2, recordPid, "pidType", pidType.getBytes(StandardCharsets.UTF_8)));
    record.add(
        new HandleAttribute(3, recordPid, "primaryNameFromPid",
            primaryNameFromPid.getBytes(StandardCharsets.UTF_8)));

    if (isDoi) {
      registrationAgencyDoiName = PTR_REGISTRATION_DOI_NAME;
      record.add(new HandleAttribute(4, recordPid, "registrationAgencyDoiName",
          registrationAgencyDoiName.getBytes(StandardCharsets.UTF_8)));
    }
    return record;
  }

}
