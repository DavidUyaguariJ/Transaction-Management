package ec.novobanco.transaction.management.services.impl;

import ec.novobanco.transaction.management.dto.accounts.AccountRequest;
import ec.novobanco.transaction.management.dto.accounts.AccountResponse;
import ec.novobanco.transaction.management.dto.customers.CustomerResponse;
import ec.novobanco.transaction.management.entities.AccountEntity;
import ec.novobanco.transaction.management.entities.CustomerEntity;
import ec.novobanco.transaction.management.enumerables.AccountStatus;
import ec.novobanco.transaction.management.exception.DomainException;
import ec.novobanco.transaction.management.exception.EntityNotFoundException;
import ec.novobanco.transaction.management.repositories.AccountRepository;
import ec.novobanco.transaction.management.services.AccountService;
import ec.novobanco.transaction.management.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        entity.setBalance(request.balance());
        entity.setStatus(AccountStatus.ACTIVE);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity.setCustomer(customer);
        AccountEntity saved = accountRepository.save(entity);
        log.info("entidad guardada {}", saved.getAccountNumber());
        CustomerResponse customerResponse = new CustomerResponse(
                customer.getId(),
                customer.getFullName()
        );
        return new AccountResponse(
                saved.getId(),
                saved.getAccountNumber(),
                customerResponse,
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
    @Transactional(readOnly = true)
    @Override
    public AccountEntity findAccountById(UUID id) throws DomainException {
        return accountRepository.findById(id).orElseThrow(() ->
                new DomainException("La cuenta no encontrada"));
    }

}
