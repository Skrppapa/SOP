package edu.rutmiit.demo.events;
import java.io.Serializable;

public record MembershipCreatedEvent(
        Long membershipId,
        String membershipNumber,
        String clientFullName
) implements Serializable {}
