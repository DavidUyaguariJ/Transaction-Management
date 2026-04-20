package ec.novobanco.transaction.management.dto.transactions;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull()
        UUID originAccountId,

        @NotNull()
        UUID destinationAccountId,

        @NotNull()
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @Size(max = 255)
        String description
) {}
