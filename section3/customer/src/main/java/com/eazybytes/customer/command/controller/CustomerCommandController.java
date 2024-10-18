package com.eazybytes.customer.command.controller;

import com.eazybytes.customer.command.CreateCustomerCommand;
import com.eazybytes.customer.command.DeleteCustomerCommand;
import com.eazybytes.customer.command.UpdateCustomerCommand;
import com.eazybytes.customer.constants.CustomerConstants;
import com.eazybytes.customer.dto.CustomerDto;
import com.eazybytes.customer.dto.ResponseDto;
import com.eazybytes.customer.service.ICustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
@RequiredArgsConstructor
public class CustomerCommandController {

    private final ICustomerService iCustomerService;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        CreateCustomerCommand customerCommand = CreateCustomerCommand.builder()
                .customerId(UUID.randomUUID().toString()).email(customerDto.getEmail())
                .name(customerDto.getName()).mobileNumber(customerDto.getMobileNumber())
                .activeSw(CustomerConstants.ACTIVE_SW).build();
        commandGateway.sendAndWait(customerCommand);
        return ResponseEntity
                .status(org.springframework.http.HttpStatus.CREATED)
                .body(new ResponseDto(CustomerConstants.STATUS_201, CustomerConstants.MESSAGE_201));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCustomerDetails(@Valid @RequestBody CustomerDto customerDto) {
        UpdateCustomerCommand updateCustomerCommand = UpdateCustomerCommand.builder()
                .customerId(customerDto.getCustomerId()).email(customerDto.getEmail())
                .name(customerDto.getName()).mobileNumber(customerDto.getMobileNumber())
                .activeSw(CustomerConstants.ACTIVE_SW).build();
        commandGateway.sendAndWait(updateCustomerCommand);
        return ResponseEntity
                .status(org.springframework.http.HttpStatus.OK)
                .body(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
    }

    @PatchMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCustomer(@RequestParam("customerId") String customerId) {
        DeleteCustomerCommand deleteCustomerCommand = DeleteCustomerCommand.builder()
                .customerId(customerId).activeSw(CustomerConstants.IN_ACTIVE_SW).build();
        commandGateway.sendAndWait(deleteCustomerCommand);
        return ResponseEntity
                .status(org.springframework.http.HttpStatus.OK)
                .body(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
    }


}
