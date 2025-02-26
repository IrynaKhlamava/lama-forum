package com.company.config;

import jakarta.persistence.EntityManagerFactory;

import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class JpaConfiguration {

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hibernateHbm2ddlAuto;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.enable_lazy_load_no_trans}")
    private String hibernateLazyLoadNoTrans;

    @Value("${hibernate.connection.release_mode}")
    private String hibernateConnectionReleaseMode;

    @Value("${hibernate.cache.use_second_level_cache}")
    private String hibernateCacheUseSecondLevelCache;

    @Value("${hibernate.cache.use_query_cache}")
    private String  hibernateCacheUseQueryCache;

    @Value("${hibernate.cache.region.factory_class}")
    private String  hibernateCacheRegionFactoryClass;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManager(CacheManager cacheManager) {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource());
        entityManager.setPackagesToScan("com.company.model");
        entityManager.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManager.setJpaProperties(getJpaProperties(cacheManager));
        return entityManager;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    private Properties getJpaProperties(CacheManager cacheManager) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
        properties.setProperty("hibernate.show_sql", showSql);
        properties.setProperty("hibernate.enable_lazy_load_no_trans", hibernateLazyLoadNoTrans);

        properties.setProperty("hibernate.connection.release_mode", hibernateConnectionReleaseMode);

        properties.setProperty("hibernate.cache.use_query_cache", hibernateCacheUseQueryCache);
        properties.setProperty("hibernate.cache.use_second_level_cache", hibernateCacheUseSecondLevelCache);
        properties.setProperty("hibernate.cache.region.factory_class", hibernateCacheRegionFactoryClass);

        properties.put(ConfigSettings.CACHE_MANAGER, cacheManager);

        return properties;
    }

}
