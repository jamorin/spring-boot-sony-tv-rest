package com.github.jamorin.proxy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableGlobalAuthentication
@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER - 1) // After TV filter but before SSO filter
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ProxyProperties props;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> configurer = auth.inMemoryAuthentication();

        // Build an in memory store of users from our application.properties
        for (Map.Entry<String, String> user : props.getUsers().entrySet()) {
            List<String> roles = props.getRoles().getOrDefault(user.getKey(), Collections.emptyList());
            configurer = configurer.withUser(user.getKey())
                    .password(user.getValue())
                    .roles(roles.toArray(new String[0]))
                    .and();
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RequestMatcher apiMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher("/api/**"),
                new AntPathRequestMatcher("/xmlrpc") // Special route for dognzb
        );

        http.requestMatcher(apiMatcher)
                .authorizeRequests()
                // permit all, as these apps will already require checking their required x-api-token header
                .antMatchers("/api/sonarr/api/**", "/api/radarr/api/**").permitAll()
                // nzbget does not use token headers and we must keep it protected
                .antMatchers("/api/nzbget/**", "/xmlrpc").hasRole("NZBGET")
                .anyRequest().hasRole("API")
                .and()
                .csrf().disable()
                .httpBasic().realmName("Proxy")
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }
}
