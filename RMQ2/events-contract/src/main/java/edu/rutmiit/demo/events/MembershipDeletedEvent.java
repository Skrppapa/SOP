package edu.rutmiit.demo.events;
import java.io.Serializable;
public record MembershipDeletedEvent(Long membershipId) implements Serializable {}
