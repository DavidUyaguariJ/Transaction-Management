package ec.novobanco.transaction.management.services;

import ec.novobanco.transaction.management.dto.customers.CustomerRequest;
import ec.novobanco.transaction.management.entities.CustomerEntity;
import ec.novobanco.transaction.management.exception.DomainException;
import ec.novobanco.transaction.management.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerEntity findCustomerById(UUID id) throws DomainException {
        return customerRepository.findById(id).orElseThrow(() -> new DomainException("Cliente no encontrado"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createCustomer(CustomerRequest customer) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setDocumentId(customer.documentId());
        customerEntity.setFullName(customer.fullName());
        customerEntity.setEmail(customer.email());
        customerEntity.setCreatedAt(Instant.now());
        customerRepository.save(customerEntity);
    }
}
