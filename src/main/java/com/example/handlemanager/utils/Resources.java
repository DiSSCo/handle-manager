package com.example.handlemanager.utils;

import java.util.List;
import java.util.logging.Logger;

import com.example.handlemanager.model.repositoryObjects.Handles;

public class Resources {
	static Logger logger =  Logger.getLogger(Resources.class.getName());
	
	public static String getDataFromType(String type, List<Handles> hList) {
		for (Handles h: hList) {
			if (h.getType().equals(type)) {
				return h.getData();
			}
		}
		logger.warning("Data for "+ type + "type not found");
		return ""; 
	}
	
	public static Handles genAdminHandle(byte [] handle, long timestamp) {
		return new Handles(handle, 100, "HS_ADMIN".getBytes(), decodeAdmin(), timestamp);
	}
	 
	private static byte[] decodeAdmin() {
		String admin = "0fff000000153330303a302e4e412f32302e353030302e31303235000000c8";
		byte[] adminByte = new byte[admin.length()/2];
		for (int i = 0; i < admin.length(); i += 2) {
			adminByte[i / 2] = hexToByte(admin.substring(i, i + 2));
			}
		return adminByte;		
	}
		
	private static byte hexToByte(String hexString) {
		int firstDigit = toDigit(hexString.charAt(0));
		int secondDigit = toDigit(hexString.charAt(1));
		return (byte) ((firstDigit << 4) + secondDigit);
	}
		

	private static int toDigit(char hexChar) {
		int digit = Character.digit(hexChar, 16);
		return digit;
	}
	

}
