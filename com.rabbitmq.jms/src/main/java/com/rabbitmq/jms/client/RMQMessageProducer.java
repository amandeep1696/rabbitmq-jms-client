package com.rabbitmq.jms.client;

import java.io.IOException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.jms.admin.RMQDestination;
import com.rabbitmq.jms.util.Util;

/**
 *
 */
public class RMQMessageProducer implements MessageProducer, QueueSender, TopicPublisher {

    private final RMQDestination destination;
    private final RMQSession session;
    private int deliveryMode;
    private boolean disableMessageID;
    private boolean disableMessageTimestamp;
    private int priority;
    private long ttl;

    public RMQMessageProducer(RMQSession session, RMQDestination destination) {
        this.session = session;
        this.destination = destination;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisableMessageID(boolean value) throws JMSException {
        this.disableMessageID = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDisableMessageID() throws JMSException {
        return this.disableMessageID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisableMessageTimestamp(boolean value) throws JMSException {
        this.disableMessageTimestamp = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDisableMessageTimestamp() throws JMSException {
        return this.disableMessageTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeliveryMode(int deliveryMode) throws JMSException {
        this.deliveryMode = deliveryMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDeliveryMode() throws JMSException {
        return this.deliveryMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPriority(int defaultPriority) throws JMSException {
        this.priority = defaultPriority;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPriority() throws JMSException {
        return this.priority;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimeToLive(long timeToLive) throws JMSException {
        this.ttl = timeToLive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeToLive() throws JMSException {
        return this.ttl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Destination getDestination() throws JMSException {
        return this.destination;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws JMSException {
        this.session.producerClose(this);
        internalClose();
    }
    
    /**
     * Method called internally or by the Session
     * when system is shutting down
     */
    protected void internalClose() {
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Message message) throws JMSException {
        this.send(message, this.getDeliveryMode(), this.getPriority(), this.getTimeToLive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.send(this.destination, message, deliveryMode, priority, timeToLive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Destination destination, Message message) throws JMSException {
        this.send(destination, message, this.getDeliveryMode(), this.getPriority(), this.getTimeToLive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        try {
            if (deliveryMode<1 || deliveryMode>2) {
                deliveryMode = javax.jms.DeliveryMode.NON_PERSISTENT;
            }
            RMQMessage msg = (RMQMessage) ((RMQMessage) message);
            RMQDestination dest = (RMQDestination) destination;
            AMQP.BasicProperties.Builder bob = new AMQP.BasicProperties.Builder();
            msg.setJMSDeliveryMode(deliveryMode);
            msg.setJMSPriority(priority);
            msg.setJMSExpiration(timeToLive == 0 ? 0 : System.currentTimeMillis() + timeToLive);
            bob.contentType("application/octet-stream");
            bob.deliveryMode(deliveryMode);
            bob.priority(priority);
            // bob.expiration(expiration) // TODO TTL implementation
            byte[] data = RMQMessage.toMessage(msg);
            this.session.getChannel().basicPublish(dest.getExchangeName(), dest.getRoutingKey(), bob.build(), data);
        } catch (IOException x) {
            Util.util().handleException(x);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queue getQueue() throws JMSException {
        return this.destination;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.send((Destination) this.destination, message, this.getDeliveryMode(), this.getPriority(), this.getTimeToLive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Queue queue, Message message) throws JMSException {
        this.send((Destination) queue, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Topic getTopic() throws JMSException {
        return this.destination;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(Message message) throws JMSException {
        this.send(getTopic(),message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.send(getTopic(), message, deliveryMode, priority, timeToLive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(Topic topic, Message message) throws JMSException {
        this.send(getTopic(), message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(Topic topic, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.send(getTopic(), message, deliveryMode, priority, timeToLive);
    }

}
