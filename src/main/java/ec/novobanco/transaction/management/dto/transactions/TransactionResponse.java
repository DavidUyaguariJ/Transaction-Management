package ec.novobanco.transaction.management.dto.transactions;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID accountId,
        UUID relatedAccountId,
        String type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String status,
        String description,
        Instant createdAt
) {}