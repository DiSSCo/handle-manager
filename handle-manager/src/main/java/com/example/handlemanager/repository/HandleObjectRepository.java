package com.example.handlemanager.repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;
import java.security.cert.Certificate;
import java.time.ZonedDateTime;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import com.example.handlemanager.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import net.handle.hdllib.*;

import reactor.core.publisher.Mono;
import net.handle.api.*;
import net.handle.hdllib.*; 



@Repository
public abstract class HandleObjectRepository {
	
	protected final String usernamePCE = "300%3A20.5000.1025"; // Percent-encoded
	protected final String userId= "300:0.NA/20.5000.1025"; //Index at which the Pubkey is stored on the admin handle record
	protected String filename;

	
	protected ObjectMapper mapper = new ObjectMapper();
	//ZZX7-CEFZ -> sample suffix
	
	
	// CRUD Operations
	
	//Create Handle
	abstract String createHandle(String handle, String record);
	
	//Resolve Handle
	abstract DigitalSpecimen resolveHandle(String handle);
	abstract DigitalSpecimen resolveHandle(String handle, String[] types, int[] indexes); 
	abstract DigitalSpecimen resolveHandle(String handle, byte[][] types, int[] indexes) throws HandleException, JsonMappingException, JsonProcessingException;
	
	// Other operations
	protected DigitalSpecimen mapToDS(String hdlStr) throws JsonMappingException, JsonProcessingException {
		DigitalSpecimen digSpec = mapper.readValue(hdlStr, DigitalSpecimen.class);
		return digSpec;	
	}
	
}


