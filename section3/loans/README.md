# CQRS and Event Sourcing setup in loans

### 1. Add the following maven dependency inside **loans/pom.xml**

```
<dependency>
    <groupId>org.axonframework</groupId>
    <artifactId>axon-spring-boot-starter</artifactId>
</dependency>
```

### 2. Add the following property inside application.yml

```yaml
axon:
  eventhandling:
    processors:
      loan-group:
        mode: subscribing
  axonserver:
    servers: localhost:8124
```

### 3. Create the following subpackages

- com.eazybytes.loans.command
    - aggregate
    - controller
    - event
    - interceptor
- com.eazybytes.loans.query
    - controller
    - handler
    - projection

### 4. Create the following classes under the respective packages

For the actual source code, please refer to the GitHub repo,

- com.eazybytes.loans.command
    - CreateLoanCommand
    - DeleteLoanCommand
    - UpdateLoanCommand
- com.eazybytes.loans.command.event
    - LoanCreatedEvent
    - LoanDeletedEvent
    - LoanUpdatedEvent
- com.eazybytes.loans.command.aggregate
    - LoanAggregate
- com.eazybytes.loans.command.controller
    - LoanCommandController
- com.eazybytes.loans.command.interceptor
    - LoanCommandInterceptor
- com.eazybytes.loans.query
    - FindLoanQuery
- com.eazybytes.loans.query.projection
    - LoanProjection
- com.eazybytes.loans.query.handler
    - LoanQueryHandler
- com.eazybytes.loans.query.controller
    - LoanQueryController

### 4. Create the following method in LoansRepository

```java
Optional<Loans> findByLoanNumberAndActiveSw(Long loanNumber, boolean activeSw);
```

### 4. Create the following method in LoanMapper

```java
public static Loans mapEventToLoan(LoanUpdatedEvent event, Loans loan) {
  loan.setLoanType(event.getLoanType());
  loan.setTotalLoan(event.getTotalLoan());
  loan.setAmountPaid(event.getAmountPaid());
  loan.setOutstandingAmount(event.getOutstandingAmount());
  return loan;
}
```

### 5. Update the ILoansService with the below abstract methods

Once the interface is updated, update the LoansServiceImpl class as well with the code present in the repository

```java
public interface ILoansService {

  /**
   * @param loan - Loans object
   */
  void createLoan(Loans loan);

  /**
   * @param mobileNumber - Input mobile Number
   * @return Loan Details based on a given mobileNumber
   */
  LoansDto fetchLoan(String mobileNumber);

  /**
   * @param event - LoanUpdatedEvent Object
   * @return boolean indicating if the update of card details is successful or not
   */
  boolean updateLoan(LoanUpdatedEvent event);

  /**
   * @param loanNumber - Input Loan Number
   * @return boolean indicating if the delete of loan details is successful or not
   */
  boolean deleteLoan(Long loanNumber);

}

```

### 6. Delete the CardsController class & it's package as we separated our APIs in to Commands and Queries

### 7. Add the below method inside the GlobalExceptionHandler class

```java

@ExceptionHandler(CommandExecutionException.class)
public ResponseEntity<ErrorResponseDto> handleGlobalException(CommandExecutionException exception,
        WebRequest webRequest) {
    ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
            webRequest.getDescription(false),
            HttpStatus.INTERNAL_SERVER_ERROR,
            exception.getMessage(),
            LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
}
```

### 8. Inside the CardsApplication class, make the following changes

```java
package com.eazybytes.cards;

import com.eazybytes.cards.command.interceptor.CardsCommandInterceptor;
import com.eazybytes.common.config.AxonConfig;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@Import({ AxonConfig.class })
public class CardsApplication {

  public static void main(String[] args) {
    SpringApplication.run(CardsApplication.class, args);
  }

  @Autowired
  public void registerCardsCommandInterceptor(ApplicationContext context,
          CommandBus commandBus) {
    commandBus.registerDispatchInterceptor(context.getBean(CardsCommandInterceptor.class));
  }

  @Autowired
  public void configure(EventProcessingConfigurer config) {
    config.registerListenerInvocationErrorHandler("card-group",
            conf -> PropagatingErrorHandler.instance());
  }

}

```

---