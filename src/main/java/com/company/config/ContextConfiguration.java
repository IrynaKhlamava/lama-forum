package com.company.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan({"com.company.config",
        "com.company.service",
        "com.company.repository"})
@EnableTransactionManagement
public class ContextConfiguration {

}
