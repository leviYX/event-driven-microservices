package com.eazybytes.customer.query;

import lombok.Value;

@Value
public class FindCustomerQuery {
    private final String mobileNumber;
}
