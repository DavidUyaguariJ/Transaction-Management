package ec.novobanco.transaction.management.services.impl;

import ec.novobanco.transaction.management.dto.accounts.AccountRequest;
import ec.novobanco.transaction.management.dto.accounts.AccountResponse;
import ec.novobanco.transaction.management.dto.customers.CustomerResponse;
import ec.novobanco.transaction.management.entities.AccountEntity;
import ec.novobanco.transaction.management.entities.CustomerEntity;
import ec.novobanco.transaction.management.enumerables.AccountStatus;
import ec.novobanco.transaction.management.enumerables.TransactionTypes;
import ec.novobanco.transaction.management.exception.DomainException;
import ec.novobanco.transaction.management.exception.EntityNotFoundException;
import ec.novobanco.transaction.management.repositories.AccountRepository;
import ec.novobanco.transaction.management.services.AccountService;
import ec.novobanco.transaction.management.services.CustomerService;
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
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerService customerService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AccountResponse createAccount(AccountRequest request) throws EntityNotFoundException {
        CustomerEntity customer = customerService.findCustomerById(request.customerId());
        log.info("Cliente encontrado {}", customer.getId());

        AccountEntity entity = new AccountEntity();
        entity.setType(request.type());
        entity.setCurrency("USD");
        entity.setBalance(request.balance() != null ? request.balance() : BigDecimal.ZERO);
        entity.setStatus(AccountStatus.ACTIVE);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity.setCustomer(customer);

        AccountEntity saved = accountRepository.save(entity);
        log.info("Cuenta creada {}", saved.getAccountNumber());
        return new AccountResponse(
                saved.getId(),
                saved.getAccountNumber(),
                new CustomerResponse(customer.getId(), customer.getFullName()),
                saved.getType(),
                saved.getCurrency(),
                saved.getBalance(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountResponse findAccountByAccountNumber(Long accountNumber) throws EntityNotFoundException {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new EntityNotFoundException(
                String.format("Cuenta no encontrada con id: %s", accountNumber)
        ));
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                new CustomerResponse(account.getCustomer().getId(), account.getCustomer().getFullName()),
                account.getType(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AccountEntity findAndLockAccount(UUID id) throws EntityNotFoundException {
        return accountRepository.findByIdForUpdate(id).orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public BigDecimal applyBalance(AccountEntity account, BigDecimal amount, TransactionTypes type) throws DomainException {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new DomainException(String.format("La cuenta %s no puede operar — estado: %s",
                    account.getAccountNumber(), account.getStatus()));
        }
        BigDecimal newBalance;
        if (type == TransactionTypes.WITHDRAWAL || type == TransactionTypes.TRANSFER_OUT) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new DomainException(String.format("Saldo insuficiente en cuenta %s (disponible: %s, solicitado: %s)",
                        account.getAccountNumber(), account.getBalance(), amount));
            }
            newBalance = account.getBalance().subtract(amount);
        } else {
            newBalance = account.getBalance().add(amount);
        }

        account.setBalance(newBalance);
        account.setUpdatedAt(Instant.now());
        return newBalance;
    }
}
