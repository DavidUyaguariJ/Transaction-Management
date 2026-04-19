package ec.novobanco.transaction.management.controllers;

import ec.novobanco.transaction.management.dto.customers.CustomerRequest;
import ec.novobanco.transaction.management.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveCustomer(@Valid @RequestBody CustomerRequest customer){
        customerService.createCustomer(customer);
    }

}
