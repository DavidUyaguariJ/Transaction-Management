package ec.novobanco.transaction.management.dto.transactions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionHistoryResponse(
        UUID id,
        String reference,
        UUID relatedAccountId,
        String type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String status,
        String description,
        Instant createdAt
) {
}
