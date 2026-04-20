package ec.novobanco.transaction.management.dto.transactions;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
        @NotNull(message = "El ID de la cuenta es obligatorio")
        UUID accountId,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
        String description
) {}
