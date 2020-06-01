package com.expense.config;


import javax.sql.DataSource;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.context.annotation.Profile;

/**
 * Конфигурационный класс уровня обслуживания в части базы данных для профиля prod.
 * @author Alexandr Trifonov
 *
 */
@Profile("prod")
@Configuration
@EnableJpaRepositories(basePackages = {"com.expense.repos"})
@ComponentScan(basePackages = {"com.expense"})
public class DataConfig {
	private static Logger logger = LoggerFactory.getLogger(DataConfig.class);
	
	
	@Bean
	DataSource dataSource() {
		try {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("org.postgresql.Driver");
			dataSource.setUrl("jdbc:postgresql://localhost:5433/expense_db");
			dataSource.setUsername("expense");
			dataSource.setPassword("expense");	
			return dataSource;
		} catch (Exception e) {
			logger.error("DataSource bean cannot be created!", e);
			return null;
		}
		
	}
	
	
	
}
