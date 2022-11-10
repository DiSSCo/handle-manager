package com.example.handlemanager.repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import com.example.handlemanager.model.DigitalSpecimen;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


import net.handle.hdllib.HandleException;
import reactor.core.publisher.Mono;


@Repository
public class HandleObjectRepositoryWeb extends HandleObjectRepository{
	
	String baseUrl = "http://35.178.174.137:8000/api/handles/";
	HttpClient client = HttpClient.newHttpClient();
	HttpRequest request;
	
	String recordStr =  "{\"values\": [ "
				+ "{\"index\": 1, "
				+ "\"ttl\": 86400,"
				+ "\"type\": \"URL\", "
				+ "\"timestamp\": \"2022-06-07T11:40:10Z\","
				+ "\"data\": {"
					+ "\"value\": \"sandbox.dissco.tech/ \", "
					+ "\"format\": \"string\""
					+ "}"
				+ "}"
			+ "], "
			+ "\"handle\": \"20.5000.1025/test\", \"responseCode\": 1}";
	
	
	@Override
	public String createHandle(String handle, String record) {
	
		// Create Web Client
		WebClient client = WebClient.create(baseUrl); // Create entry point for performing web requests
		
		// Prepare Request
		UriSpec<RequestBodySpec> uriSpec = client.put();  // Define the method - the Handle API uses PUT to write records
		RequestBodySpec bodySpec = uriSpec.uri(handle); // Define the URL (check to make sure this doesn't overwrite webclient value
		RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(record); // Define the body - Handle Data goes here		
		WebClient.ResponseSpec responseSpec = client.put().retrieve(); // Define Headers
		
		// retrieve() method directly performs HTTP request and retrieves the response body
		// exchange() returns ClientResponse having response and headers. Can then get response body from ClientResponse instance\
		return null;
				
	}
	

	public String resolveHandleWeb(String handle) throws JsonMappingException, JsonProcessingException {
		
		// Spring Boot Method 
		WebClient client = WebClient.create(baseUrl + handle ); // Create WebClient instance, entry for performing web requests
		
		WebClient.ResponseSpec responseSpec = client.get().retrieve(); //define request; program will not send request until something wants to read it
		String responseBody = responseSpec.bodyToMono(String.class).block();  // Blocking to wait for the data to arrive
		return responseBody;
		
	}
	
	
	//Resolve handle. Could allow filtering
	public String resolveHandle2(String handle) throws IOException, InterruptedException {
		request = HttpRequest.newBuilder().uri(URI.create(baseUrl+handle)).build();
		
		//HttpResponse response = client.send(request, null);
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		//return baseUrl + handle;
		return response.body();			
	}
	
	
	// Create handle record
	// This method is unauthenticated as of now! It won't work (401 forbidden), just a placeholder
	public String createHandleWeb(String handle) throws IOException, InterruptedException, URISyntaxException{  // This method doesn't even have the courtesy to return a response smh

		WebClient client = WebClient.create(); 
		
		MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
		bodyValues.add("Content-Type", "application/json;charset=UTF-8");
		
		String response = client.put()
				.uri(new URI(baseUrl + handle))
				.header("Content-Type", "application/json;charset=UTF-8")
				.retrieve()
				.bodyToMono(String.class)
				.block();

		return response; // Should be 401 unauthenticated		
		
		/**
		request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl+suffix))
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build(); */
		
		//HttpResponse response = client.send(request, null);
		//HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		//return baseUrl + handle;
		
		
		/**
		 * 
		 * 		UriSpec<RequestBodySpec> uriSpec = client.method(HttpMethod.GET); // Specify HTTP Method
		RequestBodySpec bodySpec = uriSpec.uri(handle); // End point for Handle System API
		RequestHeadersSpec<?> headerSpec = bodySpec.bodyValue("data"); //Set request body and content type
		
		
		 * ResponseSpec responseSpec = headerSpec.header(
				HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
				.acceptCharset(StandardCharsets.UTF_8)
				.ifNoneMatch("*")
				.ifModifiedSince(ZonedDateTime.now())
				.retrieve();

				
		Mono<String> response = headerSpec.retrieve().bodyToMono(String.class);	
		*/		
		
		//String username = "300%3A20.5000.1025"; //Percent-encoding of 300:20.5000.1025 i.e. the identity handle
		//return username;
		
		}



	@Override
	DigitalSpecimen resolveHandle(String handle, String[] types, int[] indexes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public
	DigitalSpecimen resolveHandle(String handle, byte[][] types, int[] indexes)
			throws HandleException, JsonMappingException, JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	DigitalSpecimen resolveHandle(String handle) {
		// TODO Auto-generated method stub
		return null;
	}


}
