/* Copyright (c) 2018 Pivotal Software, Inc. All rights reserved. */

package com.rabbitmq.jms.client;

import javax.jms.Destination;
import javax.jms.Message;

/**
 * Context when sending message.
 *
 * @see com.rabbitmq.jms.admin.RMQConnectionFactory#setSendingContextConsumer(SendingContextConsumer)
 * @see SendingContextConsumer
 * @since 1.11.0
 */
public class SendingContext {

    private final Message message;

    private final Destination destination;

    public SendingContext(Destination destination, Message message) {
        this.destination = destination;
        this.message = message;
    }

    public Destination getDestination() {
        return destination;
    }

    public Message getMessage() {
        return message;
    }
}
