package de.javawi.jstun.attribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XORMappedAddress extends MappedResponseChangedSourceAddressReflectedFrom {
	private static final Logger LOGGER = LoggerFactory.getLogger(MappedAddress.class);
	public XORMappedAddress() {
		super(MessageAttribute.MessageAttributeType.XORMappedAddress);
	}
	
	public static MessageAttribute parse(byte[] data) throws MessageAttributeParsingException {
		XORMappedAddress xorma = new XORMappedAddress();
		MappedResponseChangedSourceAddressReflectedFrom.parse(xorma, data);
		LOGGER.debug("Message Attribute: XorMapped Address parsed: " + xorma.toString() + ".");
		return xorma;
	}
}
