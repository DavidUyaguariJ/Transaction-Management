package ec.novobanco.transaction.management.services;

import ec.novobanco.transaction.management.dto.accounts.AccountRequest;
import ec.novobanco.transaction.management.dto.accounts.AccountResponse;
import ec.novobanco.transaction.management.entities.AccountEntity;
import ec.novobanco.transaction.management.enumerables.TransactionTypes;
import ec.novobanco.transaction.management.exception.DomainException;
import ec.novobanco.transaction.management.exception.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountService {

    /**
     * Crea una nueva cuenta bancaria vinculada a un cliente existente.
     * @param request Datos de la cuenta a crear {@link AccountRequest}.
     * @return {@link AccountResponse} con los datos de la cuenta.
     * @throws DomainException Si el cliente no existe o el tipo de cuenta no es válido.
     */
    AccountResponse createAccount(AccountRequest request) throws EntityNotFoundException;

    /**
     * Busca la cuenta y adquiere un lock pesimista.
     * @param id identificador para buscar una cuenta.
     * @throws EntityNotFoundException si no existe.
     */
    AccountEntity findAndLockAccount(UUID id) throws EntityNotFoundException;

    /**
     * Busca la cuenta para consultar saldos.
     * @param accountNumber identificador para buscar una cuenta.
     * @throws EntityNotFoundException si no existe.
     */
    AccountResponse findAccountByAccountNumber(Long accountNumber) throws EntityNotFoundException;

    /**
     * Actualiza el saldo de la cuenta basándose en el tipo de transacción.
     *
     * @param account Identificador UUID de la cuenta.
     * @param amount Monto a procesar.
     * @param type Tipo de transacción para determinar la operación.
     * @return El nuevo saldo resultante tras la operación.
     * @throws DomainException Si la cuenta no está activa o el saldo es insuficiente.
     */
    BigDecimal applyBalance(AccountEntity account, BigDecimal amount, TransactionTypes type) throws DomainException;

}
