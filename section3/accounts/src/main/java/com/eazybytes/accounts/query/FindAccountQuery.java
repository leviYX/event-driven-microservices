package com.eazybytes.accounts.query;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FindAccountQuery {
    private final String mobileNumber;
}
