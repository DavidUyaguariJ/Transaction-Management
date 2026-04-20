package ec.novobanco.transaction.management.services;

import ec.novobanco.transaction.management.dto.transactions.TransactionHistoryList;
import ec.novobanco.transaction.management.dto.transactions.TransactionRequest;
import ec.novobanco.transaction.management.dto.transactions.TransactionResponse;
import ec.novobanco.transaction.management.dto.transactions.TransferRequest;
import ec.novobanco.transaction.management.exception.DomainException;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {
    /**
     * Realiza un depósito en una cuenta activa.
     * @param request Datos del depósito.
     * @return Detalle de la transacción procesada.
     * @throws DomainException Si la cuenta no existe o está inactiva.
     */
    TransactionResponse deposit(TransactionRequest request) throws DomainException;

    /**
     * Realiza un retiro verificando saldo suficiente y estado de cuenta.
     * @param request Datos del retiro.
     * @return Detalle de la transacción procesada.
     * @throws DomainException Si hay saldo insuficiente o la cuenta no es operativa.
     */
    TransactionResponse withdraw(TransactionRequest request) throws DomainException;

    /**
     * Realiza una transferencia atómica entre dos cuentas.
     * @param request Datos de origen, destino y monto.
     * @return Detalle de la transferencia saliente.
     * @throws DomainException Si falla alguna validación en cualquiera de las cuentas.
     */
    TransactionResponse transfer(TransferRequest request) throws DomainException;

    /**
     * Lista el historial de transacciones .
     * @param accountId Datos de la consulta.
     * @param pageable para paginar el historial.
     * @return Lista paginada de las transacciones.
     */
    TransactionHistoryList listHistory(UUID accountId, Pageable pageable);
}
