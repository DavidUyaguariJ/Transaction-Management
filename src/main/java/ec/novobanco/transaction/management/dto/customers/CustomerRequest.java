package ec.novobanco.transaction.management.dto.customers;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CustomerRequest(
        UUID id,
        @NotBlank String fullName,
        @NotBlank String email,
        @NotBlank String documentId
) {
}
