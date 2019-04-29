package com.yizhuoyan.gameforprogrammer.util;

import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**
 * Created by ben on 10/27/18.
 */
public interface AlgorithmUtil {
	String[] MORSE_LETTER_CODES = { "01", "1000", "1010", "100", "0", "0010", "110", "0000", "00", "0111", "101",
			"0100", "11", "10", "111", "0110", "1101", "010", "000", "1", "001", "0001", "011", "1001", "1011",
			"1100" };
	String[] MORSE_NUMBER_CODES = { "11111", "01111", "00111", "00011", "00001", "00000", "10000", "11000", "11100",
			"11110" };

	default public String uuid32() {
		String uuid = UUID.randomUUID().toString();
		char[] cs = new char[32];
		char c = 0;
		for (int i = uuid.length(), j = 0; i-- > 0;) {
			if ((c = uuid.charAt(i)) != '-') {
				cs[j++] = c;
			}
		}
		return new String(cs);
	}

	default public String base64(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes());
	}

	default public String to8bitsString(char c) {
		char[] cs = new char[8];
		Arrays.fill(cs, '0');
		String result = Integer.toBinaryString(c);
		for (int i = result.length(), j = cs.length - 1; i-- > 0;) {
			cs[j--] = result.charAt(i);
		}
		return new String(cs);
	}

	default public String morseCode(char c) {
		if (c >= 'a' && c <= 'z') {
			return MORSE_LETTER_CODES[c - 'a'];
		}

		if (c >= 'A' && c <= 'Z') {
			return MORSE_LETTER_CODES[c - 'A'];
		}
		if (c >= '0' && c <= '9') {
			return MORSE_NUMBER_CODES[c - '0'];
		}
		return "" + c;

	}

	default public String morseCode(String s) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			result.append(morseCode(s.charAt(i))).append(' ');
		}
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}
}
