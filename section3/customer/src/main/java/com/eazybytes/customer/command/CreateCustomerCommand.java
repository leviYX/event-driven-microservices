package com.eazybytes.customer.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Data
public class CreateCustomerCommand {

    @TargetAggregateIdentifier
    private final String customerId;
    private final String mobileNumber;
    private final String name;
    private final String email;
    private boolean activeSw;

}