package com.example.handlemanager.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class HandleSessionsClientInterceptor implements RequestInterceptor {
	
	public void apply(RequestTemplate template) {
        template.header("Content-Type", "application/json;charset=UTF-8"); // Conent Type
	}
	
	

}
