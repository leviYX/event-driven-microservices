package com.eazybytes.cards.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Data
public class DeleteCardCommand {

    @TargetAggregateIdentifier
    private Long cardNumber;
    private boolean activeSw;

}
