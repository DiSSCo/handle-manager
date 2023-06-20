package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// This class generates new handles

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleGeneratorService {
  private final ApplicationProperties applicationProperties;

  private static final int LENGTH = 11;
  private static final String ALPHA_NUM = "ABCDEFGHJKLMNPQRSTVWXYZ1234567890";
  private static final String PREFIX = "20.5000.1025/";
  private final char[] symbols = ALPHA_NUM.toCharArray();
  private final char[] buf = new char[LENGTH];
  private final HandleRepository handleRep;
  private final Random random;

  public List<byte[]> genHandleList(int h) {
    return unwrapBytes((HashSet<ByteBuffer>) genHandleHash(h));
  }

  private List<byte[]> unwrapBytes(HashSet<ByteBuffer> handleHash) {
    List<byte[]> handleList = new ArrayList<>();
    for (ByteBuffer hash : handleHash) {
      handleList.add(hash.array());
    }
    return handleList;
  }

  private Set<ByteBuffer> genHandleHash(int h) {

    /*
     * Generates a HashSet of minted handles of size h Calls the handlefactory
     * object for random strings (9 alphanum characters with a dash in the middle)
     * Checks list of random strings against database, replaces duplicates with new
     * strings Finally, checks for collisions within the list
     */

    // Generate h number of bytes and wrap it into a HashSet<ByteBuffer>
    List<byte[]> handleList = newHandle(h);

    HashSet<ByteBuffer> handleHash = wrapBytes(handleList);

    // Check for duplicates from repository and wrap the duplicates
    HashSet<ByteBuffer> duplicates = wrapBytes(handleRep.getHandlesExist(handleList));

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
    HashSet<ByteBuffer> byteHash = new HashSet<>();
    for (byte[] bytes : byteList) {
      byteHash.add(ByteBuffer.wrap(bytes));
    }
    return byteHash;
  }

  private String newSuffix() {
    for (int idx = 0; idx < buf.length; ++idx) {
      if (idx == 3 || idx == 7) { //
        buf[idx] = '-'; // Sneak a lil dash in the middle
      } else {
        buf[idx] = symbols[random.nextInt(symbols.length)];
      }
    }
    return new String(buf);
  }

  private String newHandle() {
    return PREFIX + newSuffix();
  }

  private byte[] newHandleBytes() {
    return newHandle().getBytes(StandardCharsets.UTF_8);
  }

  private List<byte[]> newHandle(int numberOfHandles) { // Generates h number of handles
    if (numberOfHandles < 1) {
      return new ArrayList<>();
    }
    if (numberOfHandles > applicationProperties.getMaxHandles()) {
      log.warn("Max number of handles exceeded. Generating maximum {} handles instead",
          applicationProperties.getMaxHandles());
      numberOfHandles = applicationProperties.getMaxHandles();
    }

    // We'll use this to make sure we're not duplicating results
    // It's of type ByteBuffer and not byte[] because ByteBuffer has equality testing
    // byte[] is too primitive for our needs
    HashSet<ByteBuffer> handleHash = new HashSet<>();

    // This is the object we'll actually return
    List<byte[]> handleList = new ArrayList<>();
    byte[] hdl;

    for (int i = 0; i < numberOfHandles; i++) {
      hdl = newHandleBytes();
      while (!handleHash.add(ByteBuffer.wrap(hdl))) {
        hdl = newHandleBytes();
      }
      handleList.add(hdl);
    }
    return handleList;
  }
}
