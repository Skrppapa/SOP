package edu.rutmiit.demo.services;

import edu.rutmiit.demo.fit_contract.dto.MembershipRequest;
import edu.rutmiit.demo.fit_contract.dto.MembershipResponse;
import edu.rutmiit.demo.fit_contract.dto.PagedResponse;
import edu.rutmiit.demo.fit_contract.exeption.ResourceNotFoundException;
import edu.rutmiit.demo.storage.InMemoryStorage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembershipService {
    private final InMemoryStorage storage;
    private final ClientService clientService;

    public MembershipService(InMemoryStorage storage, @Lazy ClientService clientService) {
        this.storage = storage;
        this.clientService = clientService;
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
        // Проверяем, что клиент существует
        if (request.clientID() != null) {
            clientService.findById(request.clientID());
        }

        Long id = storage.membershipSequence.incrementAndGet();
        MembershipResponse created = new MembershipResponse(
                id,
                request.membershipNumber(),
                request.duration(),
                request.level(),
                request.clientID()
        );
        storage.memberships.put(id, created);
        return created;
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

    // Новый метод для поиска членств по clientId
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
        // Логика продления абонемента
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