package ec.novobanco.transaction.management.services;

import ec.novobanco.transaction.management.dto.accounts.AccountRequest;
import ec.novobanco.transaction.management.dto.accounts.AccountResponse;
import ec.novobanco.transaction.management.entities.AccountEntity;
import ec.novobanco.transaction.management.exception.DomainException;

import java.util.UUID;

public interface AccountService {

    /**
     * Crea una nueva cuenta bancaria vinculada a un cliente existente.
     * @param request Datos de la cuenta a crear {@link AccountRequest}.
     * @return {@link AccountResponse} con los datos de la cuenta.
     * @throws DomainException Si el cliente no existe o el tipo de cuenta no es válido.
     */
    AccountResponse createAccount(AccountRequest request) throws DomainException;

    /**
     * Recupera la información detallada de una cuenta mediante su identificador único.
     * * @param id Identificador UUID de la cuenta.
     * @return {@link AccountEntity} con el estado y saldo actual.
     * @throws DomainException Si la cuenta no existe.
     */
    AccountEntity findAccountById(UUID id) throws DomainException;

}
