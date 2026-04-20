package ec.novobanco.transaction.management.controllers;

import ec.novobanco.transaction.management.dto.transactions.TransactionHistoryList;
import ec.novobanco.transaction.management.dto.transactions.TransactionRequest;
import ec.novobanco.transaction.management.dto.transactions.TransactionResponse;
import ec.novobanco.transaction.management.dto.transactions.TransferRequest;
import ec.novobanco.transaction.management.exception.DomainException;
import ec.novobanco.transaction.management.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody TransactionRequest request) throws DomainException {
        return ResponseEntity.ok(transactionService.deposit(request));
    }


    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody TransactionRequest request) throws DomainException {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }


    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request) throws DomainException {
        return ResponseEntity.ok(transactionService.transfer(request));
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<TransactionHistoryList> getHistory(@PathVariable UUID accountId,
                                                             @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                                             Pageable pageable) throws DomainException {
        return ResponseEntity.ok(transactionService.listHistory(accountId, pageable));
    }

}
