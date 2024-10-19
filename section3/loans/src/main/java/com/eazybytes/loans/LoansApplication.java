package com.eazybytes.loans;

import com.eazybytes.common.config.AxonConfig;
import com.eazybytes.loans.command.interceptor.LoanCommandInterceptor;
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
@Import({AxonConfig.class})
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class LoansApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoansApplication.class, args);
    }

    @Autowired
    public void registerLoanCommandInterceptor(ApplicationContext context,
            CommandBus commandBus) {
        commandBus.registerDispatchInterceptor(context.getBean(LoanCommandInterceptor.class));
    }

    @Autowired
    public void configure(EventProcessingConfigurer config) {
        config.registerListenerInvocationErrorHandler("loan-group",
                conf -> PropagatingErrorHandler.instance());
    }

}
