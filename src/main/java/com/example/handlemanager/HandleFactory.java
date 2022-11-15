package com.example.handlemanager;

import lombok.Data;

import java.util.Objects;
import java.util.Random;


@Data
class HandleFactory {

	public static final String ALPHA_NUM = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890";
	private final Random random;
	private final char[] symbols;
	private final char[] buf;
	private final String prefix = "20.5000.1025/";

	public String newSuffix(){
		for (int idx = 0; idx < buf.length; ++idx) {
			if (idx == 4) { //
				buf[idx] = '-'; // Sneak a lil dash in the middle
			}
			else buf[idx] = symbols[random.nextInt(symbols.length)];
		}

		return new String(buf);
	}

	public String newHandle() {
		return prefix + newSuffix();
	}

	public HandleFactory(int length, Random random, String symbols) {
        if ((length < 1) || (symbols.length() < 2)) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

	public HandleFactory(int length) {
        this(length, new Random(), ALPHA_NUM);
    }

	public HandleFactory() {  // Default should be 9 chars
		this(9, new Random(), ALPHA_NUM);
	}


}
