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

import java.net.*;

public class DiscoveryInfo {
	private InetAddress testIP;
	private boolean error = false;
	private int errorResponseCode = 0;
	private String errorReason;
	private boolean openAccess = false;
	private InetAddress publicIP;

	public DiscoveryInfo(InetAddress testIP) {
		this.testIP = testIP;
	}

	public boolean isError() {
		return error;
	}

	public void setError(int responseCode, String reason) {
		this.error = true;
		this.errorResponseCode = responseCode;
		this.errorReason = reason;
	}

	public boolean isOpenAccess() {
		if (error)
			return false;
		return openAccess;
	}

	public void setOpenAccess() {
		this.openAccess = true;
	}

	public InetAddress getPublicIP() {
		return publicIP;
	}

	public InetAddress getLocalIP() {
		return testIP;
	}

	public void setPublicIP(InetAddress publicIP) {
		this.publicIP = publicIP;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Network interface: ");
		try {
			sb.append(NetworkInterface.getByInetAddress(testIP).getName());
		} catch (SocketException se) {
			sb.append("unknown");
		}
		sb.append("\n");
		sb.append("Local IP address: ");
		sb.append(testIP.getHostAddress());
		sb.append("\n");
		if (error) {
			sb.append(errorReason + " - Responsecode: " + errorResponseCode);
			return sb.toString();
		}
		// sb.append("Result: ");
		// if (openAccess) sb.append("Open access to the Internet.\n");
		// if (!openAccess) sb.append("unkown\n");
		sb.append("Public IP address: ");
		if (publicIP != null) {
			sb.append(publicIP.getHostAddress());

		} else {
			sb.append("unknown");
		}
		sb.append("\n");
		return sb.toString();
	}
}
