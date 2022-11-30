package eu.dissco.core.handlemanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.domain.pidrecords.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import eu.dissco.core.handlemanager.testUtils.TestUtils;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
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
  void testPidTypeRecordResolutionHandle() throws PidResolutionException, JsonProcessingException {
    // Given
    initTestPidTypeRecordHandle();
    String expected = TestUtils.PTR_HANDLE_RECORD;

    given(handleRep.resolveHandle(recordPid)).willReturn(typeRecord);

    // When
    String returned = pidTypeService.resolveTypePid(TestUtils.PID_ISSUER_PID);

    // Then
    assertThat(expected).isEqualTo(returned);
  }

  @Test
  void testPidTypeRecordResolutionDoi() throws PidResolutionException, JsonProcessingException {
    //Given
    initTestPidTypeRecordDoi();
    String expected = TestUtils.PTR_DOI_RECORD;
    given(handleRep.resolveHandle(recordPid)).willReturn(typeRecord);

    // When
    String returned = pidTypeService.resolveTypePid(TestUtils.PID_ISSUER_PID);

    // Then
    assertThat(expected).isEqualTo(returned);
  }

  private void initTestPidTypeRecordHandle() {
    recordPid = TestUtils.PID_ISSUER_PID.getBytes();
    pid = TestUtils.PTR_PID;
    pidType = TestUtils.PTR_TYPE;
    primaryNameFromPid = TestUtils.PTR_PRIMARY_NAME;

    typeRecord = initTestPidTypeRecord(false);
  }

  private void initTestPidTypeRecordDoi() {
    recordPid = TestUtils.PID_ISSUER_PID.getBytes();
    pid = TestUtils.PTR_PID_DOI;
    pidType = TestUtils.PTR_TYPE_DOI;
    primaryNameFromPid = TestUtils.PTR_PRIMARY_NAME;
    registrationAgencyDoiName = TestUtils.PTR_REGISTRATION_DOI_NAME;

    typeRecord = initTestPidTypeRecord(true);
  }

  private List<HandleAttribute> initTestPidTypeRecord(boolean isDoi) {
    List<HandleAttribute> record = new ArrayList<>();

    record.add(new HandleAttribute(1, recordPid, "pid", pid.getBytes()));
    record.add(new HandleAttribute(2, recordPid, "pidType", pidType.getBytes()));
    record.add(
        new HandleAttribute(3, recordPid, "primaryNameFromPid", primaryNameFromPid.getBytes()));

    if (isDoi) {
      registrationAgencyDoiName = TestUtils.PTR_REGISTRATION_DOI_NAME;
      record.add(new HandleAttribute(4, recordPid, "registrationAgencyDoiName",
          registrationAgencyDoiName.getBytes()));
    }
    return record;
  }

}
