package com.poc.chat.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Manual Liquibase configuration.
 * Required because Spring Boot 4.0 doesn't auto-configure Liquibase by default.
 */
@Configuration
@Slf4j
public class LiquibaseConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.liquibase.enabled", havingValue = "true", matchIfMissing = true)
    public SpringLiquibase liquibase(DataSource dataSource,
                                     @Value("${spring.liquibase.change-log:classpath:db/changelog/db.changelog-master.yaml}") String changeLog,
                                     @Value("${spring.liquibase.drop-first:false}") boolean dropFirst) {
        log.info("Configuring Liquibase with changelog: {}, dropFirst: {}", changeLog, dropFirst);

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDropFirst(dropFirst);
        liquibase.setShouldRun(true);

        return liquibase;
    }
}
