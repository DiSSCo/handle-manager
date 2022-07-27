package com.example.handlemanager.configuration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.handlemanager.interceptor.HandleClientInterceptor;

import feign.RequestInterceptor;
import feign.okhttp.OkHttpClient;

@FeignClient(url = "https://35.178.174.137:8000")
@Configuration
public class HandleClientConfiguration {
	String sessionId;
	
	
	 @Bean
	    public OkHttpClient client() {
	        return new OkHttpClient();
	    }
	 
	 @Bean
	 public HandleClientInterceptor handleClientInterceptor() {
	        return new HandleClientInterceptor();
	    }
}
