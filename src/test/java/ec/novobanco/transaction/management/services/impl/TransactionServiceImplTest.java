package ec.novobanco.transaction.management.services.impl;

import ec.novobanco.transaction.management.dto.transactions.TransactionRequest;
import ec.novobanco.transaction.management.dto.transactions.TransactionResponse;
import ec.novobanco.transaction.management.dto.transactions.TransferRequest;
import ec.novobanco.transaction.management.entities.AccountEntity;
import ec.novobanco.transaction.management.entities.TransactionEntity;
import ec.novobanco.transaction.management.enumerables.AccountStatus;
import ec.novobanco.transaction.management.enumerables.TransactionTypes;
import ec.novobanco.transaction.management.exception.DomainException;
import ec.novobanco.transaction.management.repositories.TransactionRepository;
import ec.novobanco.transaction.management.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TransactionServiceImpl")
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private static final UUID ACCOUNT_ID    = UUID.randomUUID();
    private static final UUID OTHER_ACCOUNT = UUID.randomUUID();
    private static final BigDecimal AMOUNT  = new BigDecimal("200.00");

    private AccountEntity activeAccount(UUID id, BigDecimal balance) {
        AccountEntity a = new AccountEntity();
        a.setId(id);
        a.setAccountNumber(10059L);
        a.setBalance(balance);
        a.setStatus(AccountStatus.ACTIVE);
        a.setCurrency("USD");
        return a;
    }

    private TransactionEntity savedEntity(TransactionEntity tx) {
        tx.setId(UUID.randomUUID());
        tx.setCreatedAt(Instant.now());
        return tx;
    }

    @BeforeEach
    void stubSave() {
        when(transactionRepository.save(any(TransactionEntity.class)))
                .thenAnswer(inv -> savedEntity(inv.getArgument(0)));
    }

    @Nested
    @DisplayName("deposit()")
    class DepositTests {
        @Test
        @DisplayName("deposito exitoso")
        void deposit_success() throws DomainException {
            AccountEntity account = activeAccount(ACCOUNT_ID, new BigDecimal("500.00"));
            BigDecimal newBalance = new BigDecimal("700.00");
            when(accountService.findAndLockAccount(ACCOUNT_ID)).thenReturn(account);
            when(accountService.applyBalance(account, AMOUNT, TransactionTypes.DEPOSIT))
                    .thenReturn(newBalance);
            TransactionResponse response = transactionService.deposit(new TransactionRequest(ACCOUNT_ID, AMOUNT, "Pago"));
            assertThat(response.type()).isEqualTo(TransactionTypes.DEPOSIT.name());
            verify(transactionRepository).save(any(TransactionEntity.class));
        }
    }

    @Nested
    @DisplayName("transfer()")
    class TransferTests {

        private TransferRequest transferRequest() {
            return new TransferRequest(ACCOUNT_ID, OTHER_ACCOUNT, AMOUNT, "Transferencia test");
        }

        @Test
        @DisplayName("transferencia exitosa: se crea solo registro de débito")
        void transfer_success_persistsOneRecord() throws DomainException {
            AccountEntity origin = activeAccount(ACCOUNT_ID, new BigDecimal("500.00"));
            AccountEntity dest   = activeAccount(OTHER_ACCOUNT, new BigDecimal("100.00"));
            when(accountService.findAndLockAccount(ACCOUNT_ID)).thenReturn(origin);
            when(accountService.findAndLockAccount(OTHER_ACCOUNT)).thenReturn(dest);
            when(accountService.applyBalance(origin, AMOUNT, TransactionTypes.TRANSFER_OUT))
                    .thenReturn(new BigDecimal("300.00"));
            when(accountService.applyBalance(dest, AMOUNT, TransactionTypes.TRANSFER_IN))
                    .thenReturn(new BigDecimal("300.00"));
            transactionService.transfer(transferRequest());
            ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionRepository, times(1)).save(captor.capture());
            TransactionEntity saved = captor.getValue();
            assertThat(saved.getType()).isEqualTo(TransactionTypes.TRANSFER_OUT.name());
            assertThat(saved.getAccount().getId()).isEqualTo(ACCOUNT_ID);
            assertThat(saved.getRelatedAccount().getId()).isEqualTo(OTHER_ACCOUNT);
            assertThat(saved.getAmount()).isEqualTo(AMOUNT);
        }

        @Test
        @DisplayName("transferencia: falla el débito, no guarda nada en la base")
        void transfer_debitFails_noSave() throws DomainException {
            AccountEntity origin = activeAccount(ACCOUNT_ID,    new BigDecimal("10.00"));
            AccountEntity dest   = activeAccount(OTHER_ACCOUNT, new BigDecimal("100.00"));
            when(accountService.findAndLockAccount(ACCOUNT_ID)).thenReturn(origin);
            when(accountService.findAndLockAccount(OTHER_ACCOUNT)).thenReturn(dest);
            when(accountService.applyBalance(origin, AMOUNT, TransactionTypes.TRANSFER_OUT))
                    .thenThrow(new DomainException("Saldo insuficiente"));
            assertThatThrownBy(() -> transactionService.transfer(transferRequest()))
                    .isInstanceOf(DomainException.class);
            verify(transactionRepository, never()).save(any());
        }
    }
}