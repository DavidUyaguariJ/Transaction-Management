package ec.novobanco.transaction.management.dto.accounts;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequest(

        Long accountNumber,

        @NotNull
        UUID customerId,

        @NotNull
        @Size(max = 10)
        String type,

        @NotNull
        @Size(max = 3)
        String currency,

        @NotNull
        BigDecimal balance
) {
}
