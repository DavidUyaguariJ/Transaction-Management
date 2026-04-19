package ec.novobanco.transaction.management.services;

import ec.novobanco.transaction.management.dto.customers.CustomerRequest;
import ec.novobanco.transaction.management.dto.customers.CustomerResponse;
import ec.novobanco.transaction.management.exception.DomainException;

import java.util.UUID;

public interface CustomerService {

    /**
     * Busca un cliente en el sistema a partir de su identificador único.
     *
     * @param id El {@link UUID} del cliente a consultar.
     * @return Un objeto {@link CustomerResponse} con la información detallada del cliente.
     * @throws DomainException Si no existe un cliente con el ID proporcionado.
     */
    CustomerResponse findCustomerById(UUID id) throws DomainException;

    /**
     * Crea un cliente en el sistema a partir de un request.
     *
     * @param customer El {@link CustomerRequest} del cliente a consultar.
     */
    void createCustomer(CustomerRequest customer);


}
