package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PidNameGeneratorService {

  private final ApplicationProperties applicationProperties;

  private static final int LENGTH = 11;
  private static final String ALPHA_NUM = "ABCDEFGHJKLMNPQRSTVWXYZ1234567890";
  private final char[] symbols = ALPHA_NUM.toCharArray();
  private final char[] buf = new char[LENGTH];
  private final PidRepository pidRepository;
  private final Random random;


  public List<byte[]> genHandleList(int numberOfHandles) {
    return unwrapBytes(genNewHandlesRecursive(numberOfHandles));
  }

  private Set<ByteBuffer> wrapBytes(List<byte[]> byteList) {
    return byteList.stream().map(ByteBuffer::wrap).collect(Collectors.toSet());
  }

  private List<byte[]> unwrapBytes(Set<ByteBuffer> handleHash) {
    return handleHash.stream().map(ByteBuffer::array).toList();
  }

  private Set<ByteBuffer> genNewHandlesRecursive(int numberOfHandles) {
    /*
     * Generates a HashSet of minted handles of size h
     * Handles are random strings (9 alphanum characters with a dash in the middle)
     * Checks list of random strings against database, replaces duplicates with new
     * strings. Finally, checks for collisions within the list
     */

    // Generate h number of bytes and wrap it into a HashSet<ByteBuffer>
    var handleHashList = genNewHandles(numberOfHandles);

    // Check for duplicates from repository
    var existingHandles = wrapBytes(pidRepository.getExistingHandles(unwrapBytes(handleHashList)));

    // If a duplicate was found, recursively call this function
    // Generate new handles for every duplicate found and add it to our hash list
    if (!existingHandles.isEmpty()) {
      handleHashList.removeAll(existingHandles);
      handleHashList.addAll(genNewHandlesRecursive(existingHandles.size()));
    }
    /*
     * It's possible we have a collision within our joined list now. If this occurs, we
     * will not have our expected number of handles
     */
    while (numberOfHandles > handleHashList.size()) {
      handleHashList.addAll(genNewHandlesRecursive(numberOfHandles - handleHashList.size()));
    }
    return handleHashList;
  }

  private Set<ByteBuffer> genNewHandles(int numberOfHandles) { // Generates h number of handles
    if (numberOfHandles < 1) {
      return Collections.emptySet();
    }
    if (numberOfHandles > applicationProperties.getMaxHandles()) {
      log.error("Max number of handles exceeded : {} requested", numberOfHandles);
      throw new InvalidRequestException("Max number of handles exceeded");
    }

    // Use ByteBuffer because it has equality testing
    HashSet<ByteBuffer> handleHash = new HashSet<>();
    byte[] hdl;
    for (int i = 0; i < numberOfHandles; i++) {
      hdl = newHandle();
      while (!handleHash.add(ByteBuffer.wrap(hdl))) {
        hdl = newHandle();
      }
    }
    return handleHash;
  }

  private byte[] newHandle() {
    for (int idx = 0; idx < buf.length; ++idx) {
      if (idx == 3 || idx == 7) {
        buf[idx] = '-';
      } else {
        buf[idx] = symbols[random.nextInt(symbols.length)];
      }
    }
    return (applicationProperties.getPrefix() + "/" + new String(buf)).getBytes(
        StandardCharsets.UTF_8);
  }
}
