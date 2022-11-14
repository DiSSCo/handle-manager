package com.example.handlemanager.configuration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.handlemanager.interceptor.HandleClientInterceptor;
import com.example.handlemanager.interceptor.HandleSessionsClientInterceptor;

import feign.okhttp.OkHttpClient;

@FeignClient(url = "https://35.178.174.137:8000/api/sessions")
@Configuration
public class HandleSessionsClientConfiguration {
	String sessionId;
	
	
	 @Bean
	    public OkHttpClient client() {
	        return new OkHttpClient();
	    }
	 
	 @Bean
	 public HandleSessionsClientInterceptor handleSessionsClientInterceptor() {
	        return new HandleSessionsClientInterceptor();
	    }
}
