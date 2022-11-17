package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// This class generates new handles

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleGeneratorService {
  @Autowired
  private HandleRepository handleRep;

  @Autowired
  private Random random;

  private static final String ALPHA_NUM = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890";

  private final char[] symbols;
  private final char[] buf;

  public HandleGeneratorService() {
    this(11, new Random(), ALPHA_NUM);
  }

  private HandleGeneratorService(int length, Random random, String symbols) {
    if ((length < 1) || (symbols.length() < 2)) {
      throw new IllegalArgumentException();
    }
    this.symbols = symbols.toCharArray();
    this.buf = new char[length];
  }

  public List<byte[]> genHandleList(int h) {
    return unwrapBytes(genHandleHash(h));
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


  public String newHandle() {
    String prefix = "20.5000.1025/";
    return prefix + newSuffix();
  }

  public byte[] newHandleBytes() {
    return newHandle().getBytes();
  }

  public List<byte[]> newHandle(int h) { // Generates h number of handles
    if (h < 1) {
      log.warn("Invalid number of handles to be generated");
      return new ArrayList<>();
    }
    int maxHandles = 1000;
    if (h > maxHandles) {
      log.warn("Max number of handles exceeded. Generating maximum {} handles",
          String.valueOf(maxHandles));
      h = maxHandles;
    }

    // We'll use this to make sure we're not duplicating results
    // It's of type ByteBuffer and not byte[] because ByteBuffer has equality testing
    // byte[] is too primitive for our needs
    HashSet<ByteBuffer> handleHash = new HashSet<>();

    // This is the object we'll actually return
    List<byte[]> handleList = new ArrayList<>();
    byte[] hdl;

    for (int i = 0; i < h; i++) {
      hdl = newHandleBytes();
      while (!handleHash.add(ByteBuffer.wrap(hdl))) {
        hdl = newHandleBytes();
      }
      handleList.add(hdl);
    }
    return handleList;
  }


  private HashSet<ByteBuffer> genHandleHash(int h) {
    /*
     * Generates a HashSet of minted handles of size h Calls the handlefactory
     * object for random strings (8 alphanum characters with a dash in the middle)
     * Checks list of random strings against database, replaces duplicates with new
     * strings Finally, checks for collisions within the list
     */

    // Generate h number of bytes and wrap it into a HashSet<ByteBuffer>
    List<byte[]> handleList = newHandle(h);
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
    HashSet<ByteBuffer> byteHash = new HashSet<>();
    for (byte[] bytes : byteList) {
      byteHash.add(ByteBuffer.wrap(bytes));
    }
    return byteHash;
  }

  // HashSet<ByteBuffer> --> List<byte[]>
  private List<byte[]> unwrapBytes(HashSet<ByteBuffer> handleHash) {
    List<byte[]> handleList = new ArrayList<>();
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
