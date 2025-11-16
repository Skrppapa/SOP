package edu.rutmiit.demo.fit_contract.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.util.Objects;

@Relation(collectionRelation = "memberships", itemRelation = "membership")
public class MembershipResponse extends RepresentationModel<MembershipResponse> {

    private final Long id;
    private final String membershipNumber;
    private final String duration;
    private final String level;
    private final Long clientID;

    public MembershipResponse(Long id, String membershipNumber, String duration, String level, Long clientID) {
        this.id = id;
        this.membershipNumber = membershipNumber;
        this.duration = duration;
        this.level = level;
        this.clientID = clientID;
    }

    public Long getId() {
        return id;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public String getDuration() {
        return duration;
    }

    public String getLevel() {
        return level;
    }

    public Long getClientID() {
        return clientID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MembershipResponse that = (MembershipResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(membershipNumber, that.membershipNumber) &&
                Objects.equals(duration, that.duration) &&
                Objects.equals(level, that.level) &&
                Objects.equals(clientID, that.clientID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, membershipNumber, duration, level, clientID);
    }
}