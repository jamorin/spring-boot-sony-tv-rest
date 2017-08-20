package com.github.jamorin.androidtv.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/*
 This security config handles ONLY the basic auth against the TV api endpoints.
 */
@Configuration
@RequiredArgsConstructor
@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER - 2) // Needs to be first to have highest priority over SSO
public class TvSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/command")
                .authorizeRequests()
                .anyRequest().hasRole("API")
            .and()
                .csrf().disable()
                .httpBasic().realmName("TV")
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }
}
