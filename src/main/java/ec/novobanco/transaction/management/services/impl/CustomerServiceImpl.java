package ec.novobanco.transaction.management.services.impl;

import ec.novobanco.transaction.management.dto.customers.CustomerRequest;
import ec.novobanco.transaction.management.entities.CustomerEntity;
import ec.novobanco.transaction.management.exception.EntityNotFoundException;
import ec.novobanco.transaction.management.repositories.CustomerRepository;
import ec.novobanco.transaction.management.services.CustomerService;
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
    public CustomerEntity findCustomerById(UUID id) throws EntityNotFoundException {
        return customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
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
