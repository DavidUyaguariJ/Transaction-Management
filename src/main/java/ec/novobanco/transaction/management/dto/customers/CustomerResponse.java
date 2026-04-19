package ec.novobanco.transaction.management.dto.customers;

import java.util.UUID;

public record CustomerResponse(UUID id, String fullName) {
}
