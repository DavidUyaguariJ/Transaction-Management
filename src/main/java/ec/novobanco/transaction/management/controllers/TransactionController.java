package ec.novobanco.transaction.management.controllers;

import ec.novobanco.transaction.management.dto.transactions.TransactionRequest;
import ec.novobanco.transaction.management.dto.transactions.TransactionResponse;
import ec.novobanco.transaction.management.dto.transactions.TransferRequest;
import ec.novobanco.transaction.management.exception.DomainException;
import ec.novobanco.transaction.management.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
