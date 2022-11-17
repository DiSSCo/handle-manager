package com.example.handlemanager.service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// This class generates new handles

@Slf4j
@Service
public class HandleFactoryService {

  private static final String ALPHA_NUM = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890";
  private final Random random;
  private final char[] symbols;
  private final char[] buf;

  public HandleFactoryService() {
    this(11, new Random(), ALPHA_NUM);
  }

  private HandleFactoryService(int length, Random random, String symbols) {
    if ((length < 1) || (symbols.length() < 2)) {
      throw new IllegalArgumentException();
    }
    this.random = Objects.requireNonNull(random);
    this.symbols = symbols.toCharArray();
    this.buf = new char[length];
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

}
