package com.pluralsight.paygov.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public Storage customerStorage() {
        return new Storage();
    }
}
