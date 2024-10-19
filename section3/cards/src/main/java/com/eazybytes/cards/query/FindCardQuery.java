package com.eazybytes.cards.query;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FindCardQuery {
    private final String mobileNumber;
}
