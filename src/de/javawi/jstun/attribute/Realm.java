package de.javawi.jstun.attribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Realm extends MappedResponseChangedSourceAddressReflectedFrom {
	private static final Logger LOGGER = LoggerFactory.getLogger(MappedAddress.class);
	public Realm() {
		super(MessageAttribute.MessageAttributeType.Realm);
	}
	
	public static MessageAttribute parse(byte[] data) throws MessageAttributeParsingException {
		Realm realm = new Realm();
		MappedResponseChangedSourceAddressReflectedFrom.parse(realm, data);
		LOGGER.debug("Message Attribute: Realm parsed: " + realm.toString() + ".");
		return realm;
	}
}
