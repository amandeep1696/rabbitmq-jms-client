/* Copyright (c) 2013 GoPivotal, Inc. All rights reserved. */
package com.rabbitmq.integration.tests;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.DeliveryMode;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.junit.Test;

import com.rabbitmq.jms.client.message.TestMessages;

/**
 * Integration test for simple point-to-point messaging.
 */
public class SSLSimpleQueueMessageIT extends AbstractITQueueSSL {

    private static final String QUEUE_NAME = "test.queue."+SSLSimpleQueueMessageIT.class.getCanonicalName();
    private static final String MESSAGE = "Hello " + SSLSimpleQueueMessageIT.class.getName();
    private static final long TEST_RECEIVE_TIMEOUT = 1000; // one second

    @Test
    public void testSendAndReceiveTextMessage() throws Exception {
        try {
            queueConn.start();
            queueConn.start();
            QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
            Queue queue = queueSession.createQueue(QUEUE_NAME);
            QueueSender queueSender = queueSession.createSender(queue);
            queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage message = queueSession.createTextMessage(MESSAGE);
            queueSender.send(message);
        } finally {
            reconnect();
        }

        queueConn.start();
        QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        Queue queue = queueSession.createQueue(QUEUE_NAME);
        QueueReceiver queueReceiver = queueSession.createReceiver(queue);
        TextMessage message = (TextMessage) queueReceiver.receive(TEST_RECEIVE_TIMEOUT);
        assertEquals(MESSAGE, message.getText());
    }

    @Test
    public void testSendAndReceiveBytesMessage() throws Exception {
        try {
            queueConn.start();
            QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
            Queue queue = queueSession.createQueue(QUEUE_NAME);
            QueueSender queueSender = queueSession.createSender(queue);
            queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            BytesMessage message = queueSession.createBytesMessage();

            TestMessages.writeBytesMessage(message);
            queueSender.send(message);
        } finally {
            reconnect();
        }

        queueConn.start();
        QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        Queue queue = queueSession.createQueue(QUEUE_NAME);
        QueueReceiver queueReceiver = queueSession.createReceiver(queue);
        BytesMessage message = (BytesMessage) queueReceiver.receive(TEST_RECEIVE_TIMEOUT);
        TestMessages.readBytesMessage(message);
    }

    @Test
    public void testSendAndReceiveMapMessage() throws Exception {
        try {
            queueConn.start();
            QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
            Queue queue = queueSession.createQueue(QUEUE_NAME);
            QueueSender queueSender = queueSession.createSender(queue);
            queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            MapMessage message = queueSession.createMapMessage();

            TestMessages.writeMapMessage(message);
            queueSender.send(message);
        } finally {
            reconnect();
        }

        queueConn.start();
        QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        Queue queue = queueSession.createQueue(QUEUE_NAME);
        QueueReceiver queueReceiver = queueSession.createReceiver(queue);
        MapMessage message = (MapMessage) queueReceiver.receive(TEST_RECEIVE_TIMEOUT);
        TestMessages.readMapMessage(message);
    }

    @Test
    public void testSendAndReceiveStreamMessage() throws Exception {
        try {
            queueConn.start();
            QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
            Queue queue = queueSession.createQueue(QUEUE_NAME);
            QueueSender queueSender = queueSession.createSender(queue);
            queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            StreamMessage message = queueSession.createStreamMessage();
            TestMessages.writeStreamMessage(message);
            queueSender.send(message);
        } finally {
            reconnect();
        }

        queueConn.start();
        QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        Queue queue = queueSession.createQueue(QUEUE_NAME);
        QueueReceiver queueReceiver = queueSession.createReceiver(queue);
        StreamMessage message = (StreamMessage) queueReceiver.receive(TEST_RECEIVE_TIMEOUT);
        TestMessages.readStreamMessage(message);
    }

    @Test
    public void testSendAndReceiveObjectMessage() throws Exception {
        try {
            queueConn.start();
            QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
            Queue queue = queueSession.createQueue(QUEUE_NAME);
            QueueSender queueSender = queueSession.createSender(queue);
            queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            ObjectMessage message = queueSession.createObjectMessage();
            message.setObjectProperty("objectProp", "This is an object property"); // try setting an object property, too
            //TestMessages.writeObjectMessage(message);
            message.setObject((Serializable)queue);
            queueSender.send(message);
        } finally {
            reconnect();
        }

        queueConn.start();
        QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        Queue queue = queueSession.createQueue(QUEUE_NAME);
        QueueReceiver queueReceiver = queueSession.createReceiver(queue);
        ObjectMessage message = (ObjectMessage) queueReceiver.receive(TEST_RECEIVE_TIMEOUT);
        assertEquals("Object not read correctly;", queue.getQueueName(), ((Queue) message.getObject()).getQueueName());
    }
}
