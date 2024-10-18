package com.eazybytes.customer.query.projection;

import com.eazybytes.customer.command.event.*;
import com.eazybytes.customer.entity.Customer;
import com.eazybytes.customer.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("customer-group")
public class CustomerProjection {

    private final ICustomerService iCustomerService;
    private final EventGateway eventGateway;

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        Customer customerEntity = new Customer();
        BeanUtils.copyProperties(event, customerEntity);
        iCustomerService.createCustomer(customerEntity);
    }

    @EventHandler
    public void on(CustomerUpdatedEvent event) {
        iCustomerService.updateCustomer(event);
    }

    @EventHandler
    public void on(CustomerDeletedEvent event) {
        iCustomerService.deleteCustomer(event.getCustomerId());
    }

}
