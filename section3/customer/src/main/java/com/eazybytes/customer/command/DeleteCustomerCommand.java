package com.eazybytes.customer.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Data
public class DeleteCustomerCommand {

    @TargetAggregateIdentifier
    private final String customerId;
    private boolean activeSw;
}
