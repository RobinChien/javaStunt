/*
 * This file is part of JSTUN. 
 * 
 * Copyright (c) 2005 Thomas King <king@t-king.de> - All rights
 * reserved.
 * 
 * This software is licensed under either the GNU Public License (GPL),
 * or the Apache 2.0 license. Copies of both license agreements are
 * included in this distribution.
 */

package de.javawi.jstun.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import de.javawi.jstun.attribute.ErrorCode;
import de.javawi.jstun.attribute.XORMappedAddress;
import de.javawi.jstun.attribute.MessageAttribute;
import de.javawi.jstun.attribute.MessageAttributeException;
import de.javawi.jstun.attribute.MessageAttributeParsingException;
import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.header.MessageHeaderParsingException;
import de.javawi.jstun.util.UtilityException;

public class DiscoveryTest {
	
//	private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryTest.class);
	InetAddress iaddress;
	String stunServer;
	int port;
	boolean nodeNatted = true;

	XORMappedAddress xorma = null;
	SocketAddress sc_add;
	Socket socketTest1 = null;
	DiscoveryInfo di = null;
	DataInputStream input = null;
	DataOutputStream os = null;

	public DiscoveryTest(InetAddress iaddress, String stunServer, int port) {
		super();
		this.iaddress = iaddress;
		this.stunServer = stunServer;
		this.port = port;
	}

	public DiscoveryInfo test() throws UtilityException, SocketException, UnknownHostException, IOException,
			MessageAttributeParsingException, MessageAttributeException, MessageHeaderParsingException {
		xorma = null;
		nodeNatted = true;
		socketTest1 = null;
		di = new DiscoveryInfo(iaddress);
		test1();
		socketTest1.close();
		return di;
	}

	private boolean test1() throws UtilityException, SocketException, UnknownHostException, IOException,
			MessageAttributeParsingException, MessageHeaderParsingException {
		int timeSinceFirstTransmission = 0;
		while (true) {
			try {
				// Test 1 including response
				socketTest1 = new Socket(stunServer, port);

				MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
				sendMH.generateTransactionID();

				byte[] data = sendMH.getBytes();
				os = new DataOutputStream(socketTest1.getOutputStream());
				os.writeInt(data.length);
				os.write(data);
//				LOGGER.debug("Test 1: Binding Request sent.");

				MessageHeader receiveMH = new MessageHeader();
				while (!(receiveMH.equalTransactionID(sendMH))) {
					int read = 0;
					byte[] get_data = new byte[200];
					input = new DataInputStream(socketTest1.getInputStream());
					while ((read = input.read(get_data, 0, get_data.length)) != -1) {
						receiveMH = MessageHeader.parseHeader(get_data);
						receiveMH.parseAttributes(get_data);
					}
				}

				xorma = (XORMappedAddress) receiveMH
						.getMessageAttribute(MessageAttribute.MessageAttributeType.XORMappedAddress);
				ErrorCode ec = (ErrorCode) receiveMH
						.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);

				if (ec != null) {
					di.setError(ec.getResponseCode(), ec.getReason());
//					LOGGER.debug("Message header contains an Errorcode message attribute.");
					return false;
				}

				if ((xorma == null)) {
					di.setError(700,
							"The server is sending an incomplete response (Mapped Address and Changed Address message attributes are missing). The client should not retry.");
//					LOGGER.debug("Response does not contain a Mapped Address or Changed Address message attribute.");
					return false;
				} else {
					di.setPublicIP(xorma.getAddress().getInetAddress());
					if ((xorma.getPort() == socketTest1.getLocalPort())
							&& (xorma.getAddress().getInetAddress().equals(socketTest1.getLocalAddress()))) {
//						LOGGER.debug("Node is not natted.");
						nodeNatted = false;
					} else {
//						LOGGER.debug("Node is natted.");
					}
					input.close();
					os.close();
					return true;
				}
			} catch (SocketTimeoutException ste) {
				// node is not capable of udp communication
//				LOGGER.debug("Node is not capable of UDP communication.");
				return false;
			}
		}
	}
}
