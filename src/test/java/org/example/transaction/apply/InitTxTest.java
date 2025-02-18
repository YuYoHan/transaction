package org.example.transaction.apply;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class InitTxTest {

    @Autowired Hello hello;

    @Test
    void go() {
        // 초기화 코드는 스프링이 초기화될 때 실행, 여기서 초기화할 때는 트랜잭션이 적용되지 않음
        // 왜냐하면 초기화 코드가 먼저 호출되고 그 다음에 트랜잭션 AOP가 적용되기 때문이다.
        hello.initV1();
    }

    @TestConfiguration
    static class InitTxTestConfiguration {
        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    @Log4j2
    static class Hello {
        @PostConstruct
        @Transactional
        void initV1() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);
        }

        // Spring Container이 완전이 떠야 이거를 실행
        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        void initV2() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);
        }
    }
}
