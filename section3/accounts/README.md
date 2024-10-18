# CQRS and Event Sourcing setup in accounts

### 1. Add the following maven dependency inside **accounts/pom.xml**

```
<dependency>
    <groupId>org.axonframework</groupId>
    <artifactId>axon-spring-boot-starter</artifactId>
</dependency>
```
### 2. Add the following property inside application.yml

```yaml
axon:
  axonserver:
    servers: localhost:8124
```
### 3. Create the following subpackages
  - com.eazybytes.accounts.command
    -   aggregate
    - controller
    - event
    - interceptor
- com.eazybytes.accounts.query
  - controller
  - handler
  - projections

### 4. Create the following classes under the respective packages
For the actual source code, please refer to the GitHub repo,
  - com.eazybytes.accounts.command
    - CreateAccountCommand
    - DeleteAccountCommand
    - UpdateAccountCommand
- com.eazybytes.accounts.command.event
  - AccountCreatedEvent
  - AccountDeletedEvent
  - AccountUpdatedEvent
- com.eazybytes.accounts.command.aggregate
  - AccountsAggregate
- com.eazybytes.accounts.command.controller
  - AccountsCommandController
- com.eazybytes.accounts.command.interceptor
  - AccountsCommandInterceptor
- com.eazybytes.accounts.query
  - FindAccountQuery
- com.eazybytes.accounts.query.projection
  - AccountProjection
- com.eazybytes.accounts.query.handler
  - AccountsQueryHandler
- com.eazybytes.accounts.query.controller
  - AccountsQueryController

### 4. Create the following method in AccountsRepository

```java
Optional<Accounts> findByAccountNumberAndActiveSw(Long accountNumber, boolean active);
```

### 4. Create the following method in AccountsMapper

```java
public static Accounts mapEventToAccount(AccountUpdatedEvent event, Accounts account) {
  account.setAccountType(event.getAccountType());
  account.setBranchAddress(event.getBranchAddress());
  return account;
}
```

### 5. Update the IAccountsService with the below abstract methods

Once the interface is updated, update the AccountsServiceImpl class as well with the code present in the repository

```java
public interface IAccountsService {

    /**
     *
     * @param account - Accounts Object
     */
    void createAccount(Accounts account);

    /**
     *
     * @param mobileNumber - Input Mobile Number
     * @return Accounts Details based on a given mobileNumber
     */
    AccountsDto fetchAccount(String mobileNumber);

    /**
     *
     * @param event - AccountUpdatedEvent Object
     * @return boolean indicating if the update of Account details is successful or not
     */
    boolean updateAccount(AccountUpdatedEvent event);

    /**
     *
     * @param accountNumber - Input Account Number
     * @return boolean indicating if the delete of Account details is successful or not
     */
    boolean deleteAccount(Long accountNumber);
}
```

### 6. Delete the AccountsController class & it's package as we separated our APIs in to Commands and Queries

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

### 8. Inside the AccountsApplication class, make the following changes

```java
package com.eazybytes.accounts;

import com.eazybytes.accounts.command.interceptor.AccountsCommandInterceptor;
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
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

    @Autowired
    public void registerAccountCommandInterceptor(ApplicationContext context,
            CommandBus commandBus) {
        commandBus.registerDispatchInterceptor(context.getBean(AccountsCommandInterceptor.class));
    }

    @Autowired
    public void configure(EventProcessingConfigurer config) {
        config.registerListenerInvocationErrorHandler("account-group",
                conf -> PropagatingErrorHandler.instance());
    }

}
```

---

This setup allows you to run Axon Server locally while ensuring that all important data (like events and configurations) persist even if the container is stopped or removed. Each volume maps specific local directories to the container, ensuring that Axon Server has access to the necessary configuration, data, and events on your system.