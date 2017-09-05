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

import java.util.Iterator;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.javawi.jstun.attribute.MessageAttribute;
import de.javawi.jstun.attribute.MessageAttributeParsingException;
import de.javawi.jstun.util.Utility;
import de.javawi.jstun.util.UtilityException;

public class MessageHeader implements MessageHeaderInterface {
	
	/*
	0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |0 0|     STUN Message Type     |         Message Length        |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                         Magic Cookie                          |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                                                               |
   |                     Transaction ID (96 bits)                  |
   |                                                               |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   */
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageHeader.class);
	MessageHeaderType type;
	byte[] id = new byte[16];

	TreeMap<MessageAttribute.MessageAttributeType, MessageAttribute> ma = new TreeMap<MessageAttribute.MessageAttributeType, MessageAttribute>();

	public MessageHeader() {
		super();
	}

	public MessageHeader(MessageHeaderType type) {
		super();
		setType(type);
	}

	public void setType(MessageHeaderType type) {
		this.type = type;
	}

	public MessageHeaderType getType() {
		return type;
	}

	public static int typeToInteger(MessageHeaderType type) {
		if (type == MessageHeaderType.BindingRequest)
			return BINDINGREQUEST;
		if (type == MessageHeaderType.BindingIndication)
			return BINDINGINDICATION;
		if (type == MessageHeaderType.BindingResponse)
			return BINDINGRESPONSE;
		if (type == MessageHeaderType.BindingErrorResponse)
			return BINDINGERRORRESPONSE;
		return -1;
	}

	public void setTransactionID(byte[] id) {
		System.arraycopy(id, 0, this.id, 0, 16);
	}

	public void generateTransactionID() throws UtilityException {
		int magicCookie_a = 0x2112;
		int magicCookie_b = 0xA442;
		System.arraycopy(Utility.integerToTwoBytes(magicCookie_a), 0, id, 0, 2); // Magic Cookie
		System.arraycopy(Utility.integerToTwoBytes(magicCookie_b), 0, id, 2, 2);
		System.arraycopy(Utility.integerToTwoBytes((int) (Math.random() * 65536)), 0, id, 4, 2);
		System.arraycopy(Utility.integerToTwoBytes((int) (Math.random() * 65536)), 0, id, 6, 2);
		System.arraycopy(Utility.integerToTwoBytes((int) (Math.random() * 65536)), 0, id, 8, 2);
		System.arraycopy(Utility.integerToTwoBytes((int) (Math.random() * 65536)), 0, id, 10, 2);
		System.arraycopy(Utility.integerToTwoBytes((int) (Math.random() * 65536)), 0, id, 12, 2);
		System.arraycopy(Utility.integerToTwoBytes((int) (Math.random() * 65536)), 0, id, 14, 2);
	}

	public byte[] getTransactionID() {
		byte[] idCopy = new byte[id.length];
		System.arraycopy(id, 0, idCopy, 0, id.length);
		return idCopy;
	}

	public boolean equalTransactionID(MessageHeader header) {
		byte[] idHeader = header.getTransactionID();
		if (idHeader.length != 16)
			return false;
		// Magic Cookie¦³4byte
		if ((idHeader[4] == id[0]) && (idHeader[5] == id[1]) && (idHeader[6] == id[2]) && (idHeader[7] == id[3])
				&& (idHeader[8] == id[4]) && (idHeader[9] == id[5]) && (idHeader[10] == id[6])
				&& (idHeader[11] == id[7]) && (idHeader[12] == id[8]) && (idHeader[13] == id[9])
				&& (idHeader[14] == id[10]) && (idHeader[15] == id[11])) {
			return true;
		} else {
			return false;
		}
	}

	public void addMessageAttribute(MessageAttribute attri) {
		ma.put(attri.getType(), attri);
	}

	public MessageAttribute getMessageAttribute(MessageAttribute.MessageAttributeType type) {
		return ma.get(type);
	}

	public byte[] getBytes() throws UtilityException {
		int length = 20;
		byte[] result = new byte[length];
		System.arraycopy(Utility.integerToTwoBytes(typeToInteger(type)), 0, result, 0, 2);
		System.arraycopy(Utility.integerToTwoBytes(length - 20), 0, result, 2, 2);
		System.arraycopy(id, 0, result, 4, 16);
		return result;
	}

	public int getLength() throws UtilityException {
		return getBytes().length;
	}

	public void parseAttributes(byte[] data) throws MessageAttributeParsingException {
		try {
			byte[] lengthArray = new byte[2];
			System.arraycopy(data, 4, lengthArray, 0, 2);
			int length = Utility.twoBytesToInteger(lengthArray);
			System.arraycopy(data, 10, id, 0, 12);
			byte[] cuttedData;
			int offset = 22;
			while (length > 0) {
				cuttedData = new byte[length];
				System.arraycopy(data, offset, cuttedData, 0, length);
				MessageAttribute ma = MessageAttribute.parseCommonHeader(cuttedData);
				addMessageAttribute(ma);
				length -= ma.getLength();
				offset += ma.getLength();
			}
		} catch (UtilityException ue) {
			throw new MessageAttributeParsingException("Parsing error");
		}
	}

	public static MessageHeader parseHeader(byte[] data) throws MessageHeaderParsingException {
		try {
			MessageHeader mh = new MessageHeader();
			byte[] typeArray = new byte[2];
			System.arraycopy(data, 2, typeArray, 0, 2);
			int type = Utility.twoBytesToInteger(typeArray);
			switch (type) {
			case BINDINGREQUEST:
				mh.setType(MessageHeaderType.BindingRequest);
				LOGGER.debug("Binding Request received.");
				break;
			case BINDINGINDICATION:
				mh.setType(MessageHeaderType.BindingIndication);
				LOGGER.debug("Binding Indication received.");
				break;
			case BINDINGRESPONSE:
				mh.setType(MessageHeaderType.BindingResponse);
				LOGGER.debug("Binding Response received.");
				break;
			case BINDINGERRORRESPONSE:
				mh.setType(MessageHeaderType.BindingErrorResponse);
				LOGGER.debug("Binding Error Response received.");
				break;
			default:
				throw new MessageHeaderParsingException("Message type " + type + "is not supported");
			}
			return mh;
		} catch (UtilityException ue) {
			throw new MessageHeaderParsingException("Parsing error");
		}
	}
}