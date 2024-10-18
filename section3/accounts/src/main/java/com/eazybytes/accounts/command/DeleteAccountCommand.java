package com.eazybytes.accounts.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Data
public class DeleteAccountCommand {

    @TargetAggregateIdentifier
    private Long accountNumber;
    private boolean activeSw;

}
