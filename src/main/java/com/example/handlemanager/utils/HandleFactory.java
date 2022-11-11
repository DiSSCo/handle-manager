package com.example.handlemanager.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.handlemanager.repository.HandleRepository;

import lombok.Data;
import java.util.logging.*;

// This class generates new handles

public class HandleFactory {	
	
	public static final String alphaNum = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890";
	private final Random random;
	private final char[] symbols;
	private final char[] buf;
	private final String prefix = "20.5000.1025/";
	private final int maxHandles = 1000;
	
	// Structure of handle: ABC-123-DEF
	
	Logger logger = Logger.getLogger(HandleFactory.class.getName());

	public HandleFactory() {  
		this(11, new Random(), alphaNum);
	}

	private HandleFactory(int length, Random random, String symbols) {
        if ((length < 1) || (symbols.length() < 2)) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

	
	private String newSuffix(){
		for (int idx = 0; idx < buf.length; ++idx) {
			if (idx == 3 || idx ==7) { //
				buf[idx] = '-'; // Sneak a lil dash in the middle
			}
			else buf[idx] = symbols[random.nextInt(symbols.length)];
		}

		return new String(buf);
	}
	

	public String newHandle() { // Generates single handle
		return prefix + newSuffix();
	}
	
	public byte[] newHandleBytes() {
		return newHandle().getBytes(); //Yeesh...
	}
	
	public List<byte[]> newHandle(int h) { // Generates h number of handles
		if (h < 1) {
			logger.warning("Invalid number of handles to be generated");
			return new ArrayList<byte[]>();
		}
		if (h > maxHandles) {
			logger.warning("Max number of handles exceeded. Generating maximum " + String.valueOf(maxHandles) + " handles");
			h = maxHandles;
		}
		
		// We'll use this to make sure we're not duplicating results
		// It's of type ByteBuffer and not byte[] because ByteBuffer has equality testing
		// byte[] is too primitive for our needs
		HashSet<ByteBuffer> handleHash = new HashSet<ByteBuffer>(); 
		
		// This is the object we'll actually return 	
		List<byte[]> handleList = new ArrayList<byte[]>(); 
		byte[] hdl;
		
		for (int i=0; i<h; i++) {
			hdl = newHandleBytes(); // Generate new handle
			while(!handleHash.add(ByteBuffer.wrap(hdl))) { // If hdl is already in the hash, regenerate a new one
				hdl = newHandleBytes();	
			}
			handleList.add(hdl);
		}
		return handleList;
	}

}
