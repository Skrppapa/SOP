package edu.rutmiit.demo.storage;

import edu.rutmiit.demo.fit_contract.dto.ClientResponse;
import edu.rutmiit.demo.fit_contract.dto.MembershipResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

    public final Map<Long, ClientResponse> clients = new ConcurrentHashMap<>();
    public final Map<Long, MembershipResponse> memberships = new ConcurrentHashMap<>();

    public final AtomicLong clientSequence = new AtomicLong(0);
    public final AtomicLong membershipSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {

        ClientResponse client1 = new ClientResponse(
                clientSequence.incrementAndGet(),
                "Иван",
                "Иванов",
                LocalDate.of(1990, 5, 15),
                "Москва"
        );

        ClientResponse client2 = new ClientResponse(
                clientSequence.incrementAndGet(),
                "Федор",
                "Федоров",
                LocalDate.of(1985, 8, 22),
                "Санкт-Петербург"
        );

        ClientResponse client3 = new ClientResponse(
                clientSequence.incrementAndGet(),
                "Петр",
                "Петров",
                LocalDate.of(1992, 3, 10),
                "Казань"
        );

        clients.put(client1.getId(), client1);
        clients.put(client2.getId(), client2);
        clients.put(client3.getId(), client3);


        MembershipResponse membership1 = new MembershipResponse(
                membershipSequence.incrementAndGet(),
                "1234567890",
                "12 месяцев",
                "Премиум",
                1L
        );

        MembershipResponse membership2 = new MembershipResponse(
                membershipSequence.incrementAndGet(),
                "0987654321",
                "6 месяцев",
                "Стандарт",
                2L
        );

        MembershipResponse membership3 = new MembershipResponse(
                membershipSequence.incrementAndGet(),
                "1122334455",
                "3 месяца",
                "Базовый",
                1L
        );

        memberships.put(membership1.getId(), membership1);
        memberships.put(membership2.getId(), membership2);
        memberships.put(membership3.getId(), membership3);
    }
}
