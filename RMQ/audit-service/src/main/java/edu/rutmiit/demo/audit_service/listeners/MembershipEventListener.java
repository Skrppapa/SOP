package edu.rutmiit.demo.audit_service.listeners;

import edu.rutmiit.demo.events.MembershipCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MembershipEventListener {

    private static final Logger log = LoggerFactory.getLogger(MembershipEventListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notification-queue", durable = "true"),
            exchange = @Exchange(name = "membership-exchange", type = "topic"),
            key = "membership.created"
    ))
    public void handleMembershipCreatedEvent(MembershipCreatedEvent event) {
        log.info("Received new membership event: {}.", event);
        // Здесь могла бы быть логика аудита или уведомлений
    }
}
