package ec.novobanco.transaction.management.dto.transactions;

import java.util.List;

public record TransactionHistoryList(
        List<TransactionHistoryResponse> list,
        Long totalElements
) {
}
