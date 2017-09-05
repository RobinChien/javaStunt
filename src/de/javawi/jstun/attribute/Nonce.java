package de.javawi.jstun.attribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nonce extends MappedResponseChangedSourceAddressReflectedFrom {
	private static final Logger LOGGER = LoggerFactory.getLogger(MappedAddress.class);
	public Nonce() {
		super(MessageAttribute.MessageAttributeType.Nonce);
	}
	
	public static MessageAttribute parse(byte[] data) throws MessageAttributeParsingException {
		Nonce nonce = new Nonce();
		MappedResponseChangedSourceAddressReflectedFrom.parse(nonce, data);
		LOGGER.debug("Message Attribute: Realm parsed: " + nonce.toString() + ".");
		return nonce;
	}
}