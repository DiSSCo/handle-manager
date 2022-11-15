package com.example.handlemanager.controller;

import com.example.handlemanager.domain.requests.HandleRecordRequest;
import com.example.handlemanager.domain.responses.HandleRecordResponse;
import com.example.handlemanager.exceptions.PidCreationException;
import com.example.handlemanager.service.HandleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static com.example.handlemanager.testUtils.TestUtils.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HandleControllerTestNoMvc {

    @Mock
    HandleService service;

    @InjectMocks
    HandleController controller;

    @BeforeEach
    void init(){
        controller = new HandleController(service);
    }

    @Test
    public void handleRecordCreationTestNoMVC() throws PidCreationException {
        // Given
        HandleRecordRequest request = generateTestHandleRequest();
        HandleRecordResponse response = generateTestHandleResponse(HANDLE.getBytes());

        given(service.createRecord(eq(request), eq("hdl"))).willReturn(response);

        // When
        ResponseEntity<?> responseReceived = controller.createRecord(request);

        // Then
        assert(responseReceived.getStatusCodeValue() == 200);
    }

}
