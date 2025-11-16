package edu.rutmiit.demo.services;

import edu.rutmiit.demo.config.RabbitMQConfig;
import edu.rutmiit.demo.events.MembershipCreatedEvent;
import edu.rutmiit.demo.fit_contract.dto.ClientResponse;
import edu.rutmiit.demo.fit_contract.dto.MembershipRequest;
import edu.rutmiit.demo.fit_contract.dto.MembershipResponse;
import edu.rutmiit.demo.fit_contract.dto.PagedResponse;
import edu.rutmiit.demo.fit_contract.exeption.ResourceNotFoundException;
import edu.rutmiit.demo.storage.InMemoryStorage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembershipService {
    private final InMemoryStorage storage;
    private final ClientService clientService;
    private final RabbitTemplate rabbitTemplate; // Внедряем RabbitTemplate

    // Добавляем RabbitTemplate в параметры конструктора
    public MembershipService(InMemoryStorage storage, @Lazy ClientService clientService, RabbitTemplate rabbitTemplate) {
        this.storage = storage;
        this.clientService = clientService;
        this.rabbitTemplate = rabbitTemplate; // инициализируем из параметра
    }

    public PagedResponse<MembershipResponse> findAll(String membershipNumber, String level, Long clientId, int page, int size) {
        List<MembershipResponse> all = storage.memberships.values().stream()
                .filter(m -> membershipNumber == null || (m.getMembershipNumber() != null && m.getMembershipNumber().contains(membershipNumber)))
                .filter(m -> level == null || (m.getLevel() != null && m.getLevel().equalsIgnoreCase(level)))
                .filter(m -> clientId == null || (m.getClientID() != null && m.getClientID().equals(clientId)))
                .collect(Collectors.toList());

        int from = Math.max(0, page * size);
        int to = Math.min(all.size(), from + size);
        List<MembershipResponse> pageContent = all.subList(from, to);
        int totalPages = (int) Math.ceil((double) all.size() / size);
        boolean last = page >= totalPages - 1;

        return new PagedResponse<>(pageContent, page, size, all.size(), totalPages, last);
    }

    public MembershipResponse findById(Long id) {
        MembershipResponse m = storage.memberships.get(id);
        if (m == null) {
            throw new ResourceNotFoundException("Membership not found with id: " + id, null);
        }
        return m;
    }



    public MembershipResponse create(MembershipRequest request) {
        // Находим клиента, если не найден - будет исключение
        ClientResponse client = null;
        if (request.clientID() != null) {
            client = clientService.findById(request.clientID());
        }

        Long id = storage.membershipSequence.incrementAndGet();
        var membership = new MembershipResponse(
                id,
                request.membershipNumber(),
                request.duration(),
                request.level(),
                request.clientID()
        );
        storage.memberships.put(id, membership);

        // Тут публикуем событие

        MembershipCreatedEvent event = new MembershipCreatedEvent(
                membership.getId(),
                membership.getMembershipNumber(),
                client != null ? client.getFirstName() + " " + client.getLastName() : "Неизвестный клиент"
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_MEMBERSHIP_CREATED, event);

        return membership;
    }



    public MembershipResponse update(Long id, MembershipRequest request) {
        MembershipResponse existing = findById(id);

        // Проверяем, что клиент существует, если clientId обновляется
        if (request.clientID() != null && !request.clientID().equals(existing.getClientID())) {
            clientService.findById(request.clientID());
        }

        MembershipResponse updated = new MembershipResponse(
                id,
                request.membershipNumber() == null ? existing.getMembershipNumber() : request.membershipNumber(),
                request.duration() == null ? existing.getDuration() : request.duration(),
                request.level() == null ? existing.getLevel() : request.level(),
                request.clientID() == null ? existing.getClientID() : request.clientID()
        );
        storage.memberships.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id);
        storage.memberships.remove(id);
    }


    public List<MembershipResponse> findByClientId(Long clientId) {
        return storage.memberships.values().stream()
                .filter(m -> m.getClientID() != null && m.getClientID().equals(clientId))
                .collect(Collectors.toList());
    }

    public MembershipResponse findByMembershipNumber(String membershipNumber) {
        return storage.memberships.values().stream()
                .filter(m -> m.getMembershipNumber().equals(membershipNumber))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with number: " + membershipNumber, null));
    }

    public MembershipResponse renewMembership(Long id, String newDuration) {
        MembershipResponse existing = findById(id);
        MembershipRequest request = new MembershipRequest(
                existing.getMembershipNumber(),
                newDuration,
                existing.getLevel(),
                existing.getClientID()
        );
        return update(id, request);
    }

    public MembershipResponse changeMembershipLevel(Long id, String newLevel) {
        MembershipResponse existing = findById(id);
        MembershipRequest request = new MembershipRequest(
                existing.getMembershipNumber(),
                existing.getDuration(),
                newLevel,
                existing.getClientID()
        );
        return update(id, request);
    }
}