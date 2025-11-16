package edu.rutmiit.demo.audit_service.listeners;


import com.rabbitmq.client.Channel;
import edu.rutmiit.demo.events.MembershipCreatedEvent;
import edu.rutmiit.demo.events.MembershipDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MembershipEventListener {
    private static final Logger log = LoggerFactory.getLogger(MembershipEventListener.class);
    private static final String EXCHANGE_NAME = "membership-exchange";
    private static final String QUEUE_NAME = "notification-queue";

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "membership.created"
            )
    )
    public void handleMembershipCreatedEvent(@Payload MembershipCreatedEvent event, Channel channel,
                                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received MembershipCreatedEvent: {}", event);
            if (event.membershipNumber() != null && event.membershipNumber().equalsIgnoreCase("0000000000")) {
                throw new RuntimeException("Simulating processing error for DLQ test");
            }
            log.info("Notification sent for new membership '{}'!", event.membershipNumber());
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "membership.deleted"
            )
    )
    public void handleMembershipDeletedEvent(@Payload MembershipDeletedEvent event, Channel channel,
                                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received MembershipDeletedEvent: {}", event);
            log.info("Notifications cancelled for deleted membershipId {}!", event.membershipId());
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "notification-queue.dlq", durable = "true"),
                    exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
                    key = "dlq.notifications"
            )
    )
    public void handleDlqMessages(Object failedMessage) {
        log.error("!!! Received message in DLQ: {}", failedMessage);
    }
}

