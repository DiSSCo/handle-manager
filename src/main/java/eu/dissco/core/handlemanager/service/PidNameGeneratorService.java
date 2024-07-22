package eu.dissco.core.handlemanager.service;

import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import java.util.ArrayList;
import java.util.Collections;
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
public class PidNameGeneratorService {

  private final ApplicationProperties applicationProperties;

  private static final int LENGTH = 11;
  private static final char[] SYMBOLS = "ABCDEFGHJKLMNPQRSTVWXYZ1234567890".toCharArray();
  private final char[] buf = new char[LENGTH];
  private final MongoRepository mongoRepository;
  private final Random random;


  public Set<String> generateNewHandles(int h) {
    if (h > applicationProperties.getMaxHandles()) {
      log.warn("Max number of handles exceeded. Generating maximum {} handles instead",
          applicationProperties.getMaxHandles());
      h = applicationProperties.getMaxHandles();
    }
    return genHandleHashSet(h);
  }

  private Set<String> genHandleHashSet(int h) {

    /*
     * Generates a HashSet of minted handles of size h Calls the handlefactory
     * object for random strings (9 alphanum characters with a dash in the middle)
     * Checks list of random strings against database, replaces duplicates with new
     * strings Finally, checks for collisions within the list
     */

    // Generate h number of bytes and wrap it into a HashSet<ByteBuffer>
    var handleList = newHandles(h);
    var handleSet = new HashSet<>(handleList);

    // Check for duplicates from repository and wrap the duplicates
    var duplicates = new HashSet<>(mongoRepository.getExistingHandles(handleList));

    // If a duplicate was found, recursively call this function
    // Generate new handles for every duplicate found and add it to our hash list

    if (!duplicates.isEmpty()) {
      handleSet.removeAll(duplicates);
      handleSet.addAll(genHandleHashSet(duplicates.size()));
    }
    /*
     * It's possible we have a collision within our list now i.e. on two different
     * recursive cal)ls to this function, we generate the same If this occurs, we
     * will not have our expected number of handles
     */
    while (h > handleSet.size()) {
      handleSet.addAll(genHandleHashSet(h - handleSet.size()));
    }
    return handleSet;
  }

  private List<String> newHandles(int numberOfHandles) { // Generates h number of handles
    if (numberOfHandles < 1) {
      return Collections.emptyList();
    }
    // We'll use this to make sure we're not duplicating results
    HashSet<String> handleHash = new HashSet<>();
    // This is the object we'll actually return
    var handleList = new ArrayList<String>();
    String hdl;
    for (int i = 0; i < numberOfHandles; i++) {
      hdl = newHandle();
      while (!handleHash.add(hdl)) {
        hdl = newHandle();
      }
      handleList.add(hdl);
    }
    return handleList;
  }

  private String newHandle() {
    return applicationProperties.getPrefix() + "/" + newSuffix();
  }

  private String newSuffix() {
    for (int idx = 0; idx < buf.length; ++idx) {
      if (idx == 3 || idx == 7) { //
        buf[idx] = '-'; // Sneak a lil dash in the middle
      } else {
        buf[idx] = SYMBOLS[random.nextInt(SYMBOLS.length)];
      }
    }
    return new String(buf);
  }
}
