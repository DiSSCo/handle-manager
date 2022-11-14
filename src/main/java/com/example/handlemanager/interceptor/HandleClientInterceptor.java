package com.example.handlemanager.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class HandleClientInterceptor implements RequestInterceptor{
	//Handle client interceptor? I barely handle client know her
	String sessionId;
	
	@Override
    public void apply(RequestTemplate template) {
        template.header("Content-Type", "application/json;charset=UTF-8"); // Conent Type
        template.header("Authorization", "\"Handle version=\"0\", \"sessionId=\""+sessionId);
	}
	
	
	
}
