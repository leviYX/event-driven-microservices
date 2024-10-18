package com.eazybytes.customer.mapper;

import com.eazybytes.customer.command.event.CustomerUpdatedEvent;
import com.eazybytes.customer.dto.CustomerDto;
import com.eazybytes.customer.entity.Customer;

public class CustomerMapper {

    public static CustomerDto mapToCustomerDto(Customer customer, CustomerDto customerDto) {
        customerDto.setCustomerId(customer.getCustomerId());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setMobileNumber(customer.getMobileNumber());
        customerDto.setActiveSw(customer.isActiveSw());
        return customerDto;
    }

    public static Customer mapEventToCustomer(CustomerUpdatedEvent event, Customer customer) {
        customer.setName(event.getName());
        customer.setEmail(event.getEmail());
        return customer;
    }

}
