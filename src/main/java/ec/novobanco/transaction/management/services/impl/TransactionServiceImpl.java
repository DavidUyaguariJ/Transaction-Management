package ec.novobanco.transaction.management.services.impl;

import ec.novobanco.transaction.management.dto.transactions.TransactionRequest;
import ec.novobanco.transaction.management.dto.transactions.TransactionResponse;
import ec.novobanco.transaction.management.dto.transactions.TransferRequest;
import ec.novobanco.transaction.management.entities.AccountEntity;
import ec.novobanco.transaction.management.entities.TransactionEntity;
import ec.novobanco.transaction.management.enumerables.TransactionStatus;
import ec.novobanco.transaction.management.enumerables.TransactionTypes;
import ec.novobanco.transaction.management.exception.DomainException;
import ec.novobanco.transaction.management.repositories.TransactionRepository;
import ec.novobanco.transaction.management.services.AccountService;
import ec.novobanco.transaction.management.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransactionResponse deposit(TransactionRequest request) throws DomainException {
        log.info("Procesando DEPÓSITO - Cuenta ID: {} | Monto: {}", request.accountId(), request.amount());
        AccountEntity account = accountService.findAndLockAccount(request.accountId());
        BigDecimal newBalance = accountService.applyBalance(account, request.amount(), TransactionTypes.DEPOSIT);
        return buildAndSave(account, null, TransactionTypes.DEPOSIT, request.amount(), newBalance,
                request.description());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransactionResponse withdraw(TransactionRequest request) throws DomainException {
        log.info("Procesando RETIRO - Cuenta ID: {} | Monto: {}", request.accountId(), request.amount());
        AccountEntity account = accountService.findAndLockAccount(request.accountId());
        BigDecimal newBalance = accountService.applyBalance(account, request.amount(), TransactionTypes.WITHDRAWAL);
        return buildAndSave(account, null, TransactionTypes.WITHDRAWAL, request.amount(), newBalance,
                request.description());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) throws DomainException {
        log.info("Procesando TRANSFERENCIA - Origen: {} | Destino: {} | Monto: {}",
                request.originAccountId(), request.destinationAccountId(), request.amount());
        UUID originId = request.originAccountId();
        UUID destId = request.destinationAccountId();
        AccountEntity origin = accountService.findAndLockAccount(originId);
        AccountEntity dest = accountService.findAndLockAccount(destId);
        BigDecimal balanceOrigin = accountService.applyBalance(origin, request.amount(), TransactionTypes.TRANSFER_OUT);
        BigDecimal balanceDest = accountService.applyBalance(dest, request.amount(), TransactionTypes.TRANSFER_IN);
        log.info("Generando registros vinculados a cliente: {}", originId);
        buildAndSave(origin, dest, TransactionTypes.TRANSFER_OUT, request.amount(),
                balanceOrigin, request.description());

        return buildAndSave(dest, origin, TransactionTypes.TRANSFER_IN, request.amount(),
                balanceOrigin, request.description());
    }


    private TransactionResponse buildAndSave(AccountEntity account, AccountEntity relatedAccount, TransactionTypes type,
                                             BigDecimal amount, BigDecimal balanceAfter, String description) {
        log.info("Construyendo registro vinculado a cliente {}", account.getId());
        TransactionEntity tx = new TransactionEntity();
        tx.setAccount(account);
        tx.setRelatedAccount(relatedAccount);
        tx.setType(type.name());
        tx.setAmount(amount);
        tx.setBalanceAfter(balanceAfter);
        tx.setStatus(TransactionStatus.SUCCESS.name());
        tx.setDescription(description);
        tx.setCreatedAt(Instant.now());
        TransactionEntity saved = transactionRepository.save(tx);
        log.info("Transacción registrada - ID: {} , Tipo: {} , Cuenta: {}",
                saved.getId(), type, account.getAccountNumber());
        return new TransactionResponse(saved.getId(), account.getId(), relatedAccount != null ? relatedAccount.getId() : null,
                saved.getType(), saved.getAmount(), saved.getBalanceAfter(), saved.getStatus(), saved.getDescription(), saved.getCreatedAt()
        );
    }
}
