package com.example.handlemanager.utils;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.example.handlemanager.model.repositoryObjects.Handles;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Resources {

	public static String getDataFromType(String type, List<Handles> hList) {
		for (Handles h : hList) {
			if (h.getType().equals(type)) {
				return h.getData();
			}
		}
		return ""; // This should maybe return a warning?
	}
	public static Handles genAdminHandle(byte[] handle, long timestamp) {
		return new Handles(handle, 100, "HS_ADMIN".getBytes(), decodeAdmin(), timestamp);
	}

	private static byte[] decodeAdmin() {
		String admin = "0fff000000153330303a302e4e412f32302e353030302e31303235000000c8";
		byte[] adminByte = new byte[admin.length() / 2];
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

	public static byte[] setLocations(String[] objectLocations) throws TransformerException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

		var doc = documentBuilder.newDocument();
		var locations = doc.createElement("locations");
		doc.appendChild(locations);
		for (int i = 0; i < objectLocations.length; i++) {

			var locs = doc.createElement("location");
			locs.setAttribute("id", String.valueOf(i));
			locs.setAttribute("href", objectLocations[i]);
			locs.setAttribute("weight", "0");
			locations.appendChild(locs);
		}
		return documentToString(doc).getBytes(StandardCharsets.UTF_8);
	}

	private static String documentToString(Document document) throws TransformerException {
		var tf = TransformerFactory.newInstance();
		var transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		return writer.getBuffer().toString();
	}

}
