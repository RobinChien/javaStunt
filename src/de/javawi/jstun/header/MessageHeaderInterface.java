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

package de.javawi.jstun.header;

public interface MessageHeaderInterface {
	public enum MessageHeaderType { BindingRequest, BindingIndication, BindingResponse, BindingErrorResponse};
	final static int BINDINGREQUEST = 0x0001;
	final static int BINDINGINDICATION = 0x0011;
	final static int BINDINGRESPONSE = 0x0101;
	final static int BINDINGERRORRESPONSE = 0x0111;
}