package com.example.handlemanager.domain.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HandleRecordRequest {
	final String pidIssuerPid;
	final String digitalObjectTypePid;
	final String digitalObjectSubtypePid;
	final String [] locations;
}
