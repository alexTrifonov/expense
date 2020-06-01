package com.expense.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Конфигурационный класс уровня обслуживания в части базы данных для профиля test.
 * @author Alexandr Trifonov
 *
 */
@Profile("test")
@Configuration
@EnableJpaRepositories(basePackages = {"com.expense.repos"})
@ComponentScan(basePackages = {"com.expense"})
public class DataTestConfig {
	private static Logger logger = LoggerFactory.getLogger(DataTestConfig.class);
	
	@Bean
	DataSource dataSource() {
		try {
			EmbeddedDatabaseBuilder dbBuilder = new EmbeddedDatabaseBuilder();
			return dbBuilder.setType(EmbeddedDatabaseType.H2).build();
		} catch(Exception e) {
			logger.error("Embedded DataSource bean cannot be created!", e);
			return null;
		}
		
	}
	
}
