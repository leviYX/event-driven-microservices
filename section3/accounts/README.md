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

### 5. Update the AccountsServiceImpl class with the code present in the repository

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


**Explanation of the Docker Command**

- `-d`: Runs the container in detached mode (in the background).
- `--name axonserver`: Gives the container a name (`axonserver`), making it easier to manage.
- `-p 8024:8024 -p 8124:8124`: Maps Axon Server's internal ports (`8024` and `8124`) to the corresponding ports on your local machine. 
  - Port `8024` is used for the HTTP interface (Axon Dashboard).
  - Port `8124` is used for the gRPC interface that Axon Framework applications use to communicate with the server.
- `-v "/Users/eazybytes/Desktop/axonserver/data":/axonserver/data`: Mounts the local `data` folder to the container's `/axonserver/data` directory to persist Axon Server's internal data.
- `-v "/Users/eazybytes/Desktop/axonserver/events":/axonserver/events`: Mounts the local `events` folder to store Axon events.
- `-v "/Users/eazybytes/Desktop/axonserver/config":/axonserver/config`: Mounts the local `config` folder, where you placed the `axonserver.properties` file, to the container's `/axonserver/config` directory. This ensures that the server uses the custom configuration defined in this file.

### 4. Verify the Setup

Once the container is running, you can verify the Axon Server by visiting [http://localhost:8024](http://localhost:8024) in your browser. This should open the Axon Server dashboard.

---

This setup allows you to run Axon Server locally while ensuring that all important data (like events and configurations) persist even if the container is stopped or removed. Each volume maps specific local directories to the container, ensuring that Axon Server has access to the necessary configuration, data, and events on your system.