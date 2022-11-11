package com.example.handlemanager.domain.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class HandleRecordRequest {
	final String pidIssuerPid;
	final String digitalObjectTypePid;
	final String digitalObjectSubtypePid;
	final String [] loc;
}
