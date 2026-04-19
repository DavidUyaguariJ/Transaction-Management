package ec.novobanco.transaction.management.dto.accounts;

import ec.novobanco.transaction.management.dto.customers.CustomerResponse;
import ec.novobanco.transaction.management.enumerables.AccountStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        Long accountNumber,
        CustomerResponse customer,
        String type,
        String currency,
        BigDecimal balance,
        AccountStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
