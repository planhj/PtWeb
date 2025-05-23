package com.example.ptweb.autoconfig;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SafeHTMLConfig {
    @Bean
    public PolicyFactory safeHTML(){
        return Sanitizers.LINKS
                .and(Sanitizers.TABLES)
                .and(Sanitizers.IMAGES)
                .and(Sanitizers.BLOCKS)
                .and(Sanitizers.FORMATTING)
                .and(Sanitizers.STYLES);
    }
}
