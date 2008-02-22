/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.message;

import java.util.Date;
import java.util.Set;

import org.springframework.integration.util.IdGenerator;
import org.springframework.integration.util.RandomUuidGenerator;
import org.springframework.util.Assert;

/**
 * Base Message class defining common properties such as id, header, and lock.
 * 
 * @author Mark Fisher
 */
public class GenericMessage<T> implements Message<T> {

	private final Object id;

	private final MessageHeader header = new MessageHeader();

	private final T payload;

	private final IdGenerator defaultIdGenerator = new RandomUuidGenerator();


	/**
	 * Create a new message with the given id and payload.
	 * 
	 * @param id unique identifier for this message
	 * @param payload the message payload
	 */
	public GenericMessage(Object id, T payload) {
		Assert.notNull(id, "id must not be null");
		Assert.notNull(payload, "payload must not be null");
		this.id = id;
		this.payload = payload;
	}

	/**
	 * Create a new message with the given payload. The id will be generated by
	 * the default {@link IdGenerator} strategy.
	 * 
	 * @param payload the message payload
	 */
	public GenericMessage(T payload) {
		Assert.notNull(payload, "payload must not be null");
		this.id = this.defaultIdGenerator.generateId();
		this.payload = payload;
	}

	/**
	 * Create a new message with the given payload. The id will be generated by
	 * the default {@link IdGenerator} strategy. The header will be populated
	 * with the attributes and properties of the provided header.
	 * 
	 * @param payload the message payload
	 * @param headerToCopy message header whose attributes and properties should
	 * be copied into the new message's header
	 */
	public GenericMessage(T payload, MessageHeader headerToCopy) {
		this(payload);
		this.copyHeader(headerToCopy);
	}


	public Object getId() {
		return this.id;
	}

	public MessageHeader getHeader() {
		return this.header;
	}

	public T getPayload() {
		return this.payload;
	}

	public boolean isExpired() {
		Date expiration = this.header.getExpiration();
		return (expiration != null) ? expiration.getTime() < System.currentTimeMillis() : false;
	}

	public String toString() {
		return "[ID=" + this.id + "][Header=" + this.header + "][Payload='" + this.payload + "']";
	}

	private void copyHeader(final MessageHeader headerToCopy) {
		Set<String> propertyNames = headerToCopy.getPropertyNames();
		for (String key : propertyNames) {
			this.header.setProperty(key, headerToCopy.getProperty(key));
		}
		Set<String> attributeNames = headerToCopy.getAttributeNames();
		for (String key : attributeNames) {
			this.header.setAttribute(key, headerToCopy.getAttribute(key));
		}
		this.header.setReturnAddress(headerToCopy.getReturnAddress());
	}

}
