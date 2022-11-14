package com.example.handlemanager.service;

import com.example.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import com.example.handlemanager.domain.requests.DigitalSpecimenRequest;
import com.example.handlemanager.domain.requests.DoiRecordRequest;
import com.example.handlemanager.domain.requests.HandleRecordRequest;
import com.example.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import com.example.handlemanager.domain.responses.DigitalSpecimenResponse;
import com.example.handlemanager.domain.responses.DoiRecordResponse;
import com.example.handlemanager.domain.responses.HandleRecordResponse;
import com.example.handlemanager.exceptions.PidCreationException;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.example.handlemanager.repository.HandleRepository;
import com.example.handlemanager.utils.HandleFactory;
import com.example.handlemanager.utils.Resources;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import static com.example.handlemanager.utils.Resources.setLocations;

// Postgres value in (value1, value2)..
// Generate all handles before posting...

@Service
@RequiredArgsConstructor
public class HandleService {

	@Autowired
	public HandleRepository handleRep;

	@Autowired
	PidTypeService pidTypeService;

	@Autowired
	private Clock clock;

	private HandleFactory hf = new HandleFactory();
	Logger logger = Logger.getLogger(HandleService.class.getName());
	//ObjectMapper mapper = new ObjectMapper();

	// Return all handle identifiers, option to filter by status
	public List<String> getHandles() {
		return getStrList(handleRep.getHandles());
	}

	public List<String> getHandles(String pidStatus) {
		return getStrList(handleRep.getHandles(pidStatus.getBytes()));
	}

	// Create Handle Record Batch
	public List<HandleRecordResponse> createHandleRecordBatch(List<HandleRecordRequest> requests) {
		List<byte[]> handles = genHandleList(requests.size());
		long timestamp = clock.instant().getEpochSecond();
		List<Handles> handleRecord = new ArrayList<Handles>();
		List<Handles> handleRecordsAll = new ArrayList<Handles>();
		List<HandleRecordResponse> response = new ArrayList<HandleRecordResponse>();

		for (int i = 0; i < requests.size(); i++) {

			// Prepare handle record as list of Handles
			handleRecord = prepareHandleRecord(requests.get(i), handles.get(i), timestamp);

			// Add list of handle entries (i.e. collection of single rows in db) to list to
			// be posted at the end
			handleRecordsAll.addAll(handleRecord);

			// Add new handleRecordResponse to our response list
			response.add(new HandleRecordResponse(handleRecord));

			// Clear our list
			handleRecord.clear();
		}

		// Save all records
		System.out.println("GeneratedRecord");
		for (Handles h: handleRecordsAll){
			System.out.println(h.toString());
		}

		handleRep.saveAll(handleRecordsAll);
		return response;
	}

	public List<DoiRecordResponse> createDoiRecordBatch(List<DoiRecordRequest> requests) {
		List<byte[]> handles = genHandleList(requests.size());
		long timestamp = clock.instant().getEpochSecond();
		List<Handles> doiRecord;
		List<Handles> doiRecordsAll = new ArrayList<>();
		List<DoiRecordResponse> response = new ArrayList<>();
		for (int i = 0; i < requests.size(); i++) {

			// Prepare record as list of Handles
			doiRecord = prepareDoiRecord(requests.get(i), handles.get(i), timestamp);

			// Add list of Handle entries (i.e. collection of single rows in db) to list to
			// be posted at the end
			doiRecordsAll.addAll(doiRecord);

			// Add new doiRecordResponse to our response list
			response.add(new DoiRecordResponse(doiRecord));

			// Clear our list
			doiRecord.clear();
		}

		// Save all records
		handleRep.saveAll(doiRecordsAll);
		return response;
	}

	public List<DigitalSpecimenResponse> createDigitalSpecimenBatch(List<DigitalSpecimenRequest> requests) {
		List<byte[]> handles = genHandleList(requests.size());
		long timestamp = clock.instant().getEpochSecond();
		List<Handles> digitalSpecimenRecord;
		List<Handles> digitalSpecimenRecordsAll = new ArrayList<>();
		List<DigitalSpecimenResponse> response = new ArrayList<>();
		for (int i = 0; i < requests.size(); i++) {

			// Prepare record as list of Handles
			digitalSpecimenRecord = prepareDigitalSpecimenRecord(requests.get(i), handles.get(i), timestamp);

			// Add list of Handle entries (i.e. collection of single rows in db) to list to
			// be posted at the end
			digitalSpecimenRecordsAll.addAll(digitalSpecimenRecord);

			// Add new DigitalSpecimenResponse to our response list
			response.add(new DigitalSpecimenResponse(digitalSpecimenRecord));

			// Clear our list
			digitalSpecimenRecord.clear();
		}
		// Save all records
		handleRep.saveAll(digitalSpecimenRecordsAll);
		return response;
	}

	public List<DigitalSpecimenBotanyResponse> createDigitalSpecimenBotanyBatch(
			List<DigitalSpecimenBotanyRequest> requests) {
		List<byte[]> handles = genHandleList(requests.size());
		long timestamp = clock.instant().getEpochSecond();
		List<Handles> digitalSpecimenBotanyRecord = new ArrayList<Handles>();
		List<Handles> digitalSpecimenRecordsAll = new ArrayList<Handles>();
		List<DigitalSpecimenBotanyResponse> response = new ArrayList<DigitalSpecimenBotanyResponse>();
		for (int i = 0; i < requests.size(); i++) {

			// Prepare record as list of Handles
			digitalSpecimenBotanyRecord = prepareDigitalSpecimenBotanyRecord(requests.get(i), handles.get(i),
					timestamp);

			// Add list of Handle entries (i.e. collection of single rows in db) to list to
			// be posted at the end
			digitalSpecimenRecordsAll.addAll(digitalSpecimenBotanyRecord);

			// Add new doiRecordResponse to our response list
			response.add(new DigitalSpecimenBotanyResponse(digitalSpecimenBotanyRecord));

			// Clear our list
			digitalSpecimenBotanyRecord.clear();
		}
		// Save all records
		handleRep.saveAll(digitalSpecimenRecordsAll);
		return response;
	}

	public HandleRecordResponse createRecord(HandleRecordRequest request, String recordType)
			throws PidCreationException {
		byte[] handle = genHandleList(1).get(0);
		long timestamp = clock.instant().getEpochSecond();
		List<Handles> handleRecord;
		HandleRecordResponse response;

		switch (recordType) {
			case "hdl" -> {
				handleRecord = prepareHandleRecord(request, handle, timestamp);
				response = new HandleRecordResponse(handleRep.saveAll(handleRecord));
			}
			case "doi" -> {
				handleRecord = prepareDoiRecord((DoiRecordRequest) request, handle, timestamp);
				response = new DoiRecordResponse(handleRep.saveAll(handleRecord));
			}
			case "ds" -> {
				handleRecord = prepareDigitalSpecimenRecord((DigitalSpecimenRequest) request, handle, timestamp);
				response = new DigitalSpecimenResponse(handleRep.saveAll(handleRecord));
				logger.info("creating digital specimen");
			}
			case "dsB" -> {
				handleRecord = prepareDigitalSpecimenBotanyRecord((DigitalSpecimenBotanyRequest) request, handle,
						timestamp);
				response = new DigitalSpecimenBotanyResponse(handleRep.saveAll(handleRecord));
			}
			default ->
				// Not sure if this is the best way to do this. We should never get here.
					throw new PidCreationException("An internal error has occured. Invalid pid record type.");
		}
		// TODO: Maybe check response has all the pid kernel entries we're expecting
		return response;
	}

	// Prepare Record Lists

	private List<Handles> prepareHandleRecord(HandleRecordRequest request, byte[] handle, long timestamp) {
		List<Handles> handleRecord = new ArrayList<Handles>();

		// 100: Admin Handle
		handleRecord.add(Resources.genAdminHandle(handle, timestamp));

		int i = 1;
		// 1: Pid
		byte[] pid = concatBytes("https://hdl.handle.net/".getBytes(), handle); // TODO this should check if it's a DOI?
		handleRecord.add(new Handles(handle, i++, "pid", pid, timestamp));

		// 2: PidIssuer
		String pidIssuer = pidTypeService.resolveTypePid(request.getPidIssuerPid());
		handleRecord.add(new Handles(handle, i++, "pidIssuer", pidIssuer, timestamp));

		// 3: Digital Object Type
		String digitalObjectType = pidTypeService.resolveTypePid(request.getDigitalObjectTypePid());
		handleRecord.add(new Handles(handle, i++, "digitalObjectType", digitalObjectType, timestamp));

		// 4: Digital Object Subtype
		String digitalObjectSubtype = pidTypeService.resolveTypePid(request.getDigitalObjectSubtypePid());
		handleRecord.add(new Handles(handle, i++, "digitalObjectSubtype", digitalObjectSubtype, timestamp));

		// 5: 10320/loc
		byte[] loc = "".getBytes();
		try {
			loc = setLocations(request.getLocations());
		} catch (TransformerException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		handleRecord.add(new Handles(handle, i++, "10320/loc", loc, timestamp));

		// 6: Issue Date
		handleRecord.add((new Handles(handle, i++, "issueDate", getDate(), timestamp)));

		// 7: Issue number
		handleRecord.add((new Handles(handle, i++, "issueNumber", "1", timestamp))); // TODO: Will every created handle
																						// have a 1 for the issue date?

		// 8: PidStatus
		handleRecord.add((new Handles(handle, i++, "pidStatus", "TEST", timestamp))); // TODO: Can I keep this as test?

		// 9, 10: tombstone text, tombstone pids -> Skip
		i++;
		i++;
		// 11: PidKernelMetadataLicense:
		// https://creativecommons.org/publicdomain/zero/1.0/
		handleRecord.add((new Handles(handle, i++, "pidKernelMetadataLicense",
				"https://creativecommons.org/publicdomain/zero/1.0/", timestamp)));

		return handleRecord;
	}

	private List<Handles> prepareDoiRecord(DoiRecordRequest request, byte[] handle, long timestamp) {
		List<Handles> handleRecord = prepareHandleRecord(request.getHandleRecordRequest(), handle, timestamp);
		int i = 12;

		// 12: Referent DOI Name
		String referentDoiName = pidTypeService.resolveTypePid(request.getReferentDoiName());
		handleRecord.add(new Handles(handle, i++, "referentDoiName", referentDoiName, timestamp));

		// 13: Referent -> NOTE: Referent is blank currently until we have a model for
		// it
		handleRecord.add(new Handles(handle, i++, "referent", request.getReferent(), timestamp));

		return handleRecord;
	}

	private List<Handles> prepareDigitalSpecimenRecord(DigitalSpecimenRequest request, byte[] handle, long timestamp) {
		List<Handles> handleRecord = prepareDoiRecord(request.getDoiRecordRequest(), handle, timestamp);

		int i = 14;

		// 14: digitalOrPhysical
		handleRecord.add(new Handles(handle, i++, "digitalOrPhysical", request.getDigitalOrPhysical(), timestamp));

		// 15: specimenHost
		String specimenHost = pidTypeService.resolveTypePid(request.getSpecimenHostPid());
		handleRecord.add(new Handles(handle, i++, "specimenHost", specimenHost, timestamp));

		// 16: In collectionFacillity
		String inCollectionFacillity = pidTypeService.resolveTypePid(request.getInCollectionFacilityPid());
		handleRecord.add(new Handles(handle, i++, "inCollectionFacillity", inCollectionFacillity, timestamp));

		return handleRecord;
	}

	private List<Handles> prepareDigitalSpecimenBotanyRecord(DigitalSpecimenBotanyRequest request, byte[] handle,
			long timestamp) {
		List<Handles> handleRecord = prepareDigitalSpecimenRecord(request.getDigitalSpecimenRequest(), handle,
				timestamp);

		int i = 17;

		// 17: ObjectType
		handleRecord.add(new Handles(handle, i++, "objectType", request.getObjectType(), timestamp));

		// 18: preservedOrLiving
		handleRecord.add(new Handles(handle, i++, "preservedOrLiving", request.getPreservedOrLiving(), timestamp));

		return handleRecord;
	}

	private byte[] concatBytes(byte[] a, byte[] b) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(a);
			outputStream.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputStream.toByteArray();
	}



	private String getDate() {
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime now = LocalDateTime.now(clock);
		return dt.format(now);
	}

	// TODO: Resolving PID Records

	/*

	public List<HandleRecordResponse> resolve(String[] h) {
		String type;
		for (int i = 0; i < h.length; i++) {
			type = getDataFromType("digitalObjectType", resolveRecord(h[i]));

		}
		return null;
	}

	private List<Handles> resolveRecord(String h) {
		return handleRep.resolveHandle(h.getBytes());
	}*/

	// Given a list of Handles (of unknown pidStatus), return HandleRecord

	// Minting Handles

	// Mint a list of handles
	private List<byte[]> genHandleList(int h) {
		return unwrapBytes(genHandleHash(h));
	}

	private HashSet<ByteBuffer> genHandleHash(int h) {
		/*
		 * Generates a HashSet of minted handles of size h Calls the handlefactory
		 * object for random strings (8 alphanum characters with a dash in the middle)
		 * Checks list of random strings against database, replaces duplicates with new
		 * strings Finally, checks for collisions within the list
		 */

		// Generate h number of bytes and wrap it into a HashSet<ByteBuffer>
		List<byte[]> handleList = hf.newHandle(h);
		HashSet<ByteBuffer> handleHash = wrapBytes(handleList);

		// Check for duplicates from repository and wrap the duplicates
		HashSet<ByteBuffer> duplicates = wrapBytes(handleRep.checkDuplicateHandles(handleList));

		// If a duplicate was found, recursively call this function
		// Generate new handles for every duplicate found and add it to our hash list

		if (!duplicates.isEmpty()) {
			handleHash.removeAll(duplicates);
			handleHash.addAll(genHandleHash(duplicates.size()));
		}

		/*
		 * It's possible we have a collision within our list now i.e. on two different
		 * recursive cal)ls to this function, we generate the same If this occurs, we
		 * will not have our expected number of handles
		 */
		while (h > handleHash.size()) {
			handleHash.addAll(genHandleHash(h - handleHash.size()));
		}

		return handleHash;
	}

	// Converting between List<Byte[] and HashSet<ByteBuffer>
	/*
	 * List<byte[]> <----> HashSet<ByteBuffer> HashSets are useful for preventing
	 * collisions within the list List<byte[]> is used to interface with repository
	 * layer
	 */

	// Converts List<byte[]> --> HashSet<ByteBuffer>
	private HashSet<ByteBuffer> wrapBytes(List<byte[]> byteList) {
		HashSet<ByteBuffer> byteHash = new HashSet<ByteBuffer>();
		for (byte[] bytes : byteList) {
			byteHash.add(ByteBuffer.wrap(bytes));
		}
		return byteHash;
	}

	// HashSet<ByteBuffer> --> List<byte[]>
	private List<byte[]> unwrapBytes(HashSet<ByteBuffer> handleHash) {
		List<byte[]> handleList = new ArrayList<byte[]>();
		for (ByteBuffer hash : handleHash) {
			handleList.add(hash.array());
		}
		return handleList;
	}
	
	
	private List<String> getStrList(List<byte[]> byteList) {
		// It's crazy that ArrayList doesn't have a native type converter (especially
		// from byte[] to str?
		// If there's a better way to do this that would be cool

		int l = byteList.size();
		List<String> strList = new ArrayList<>(l);
		String s;
		for (byte[] b : byteList) {
			s = byteToString(b);
			strList.add(s);
		}
		return strList;
	}

	private String byteToString(byte[] b) {
		return new String(b, StandardCharsets.UTF_8);
	}

}
