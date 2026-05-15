package com.socially.auth.kernel.infrastructure.left;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

@Configuration
public class SecurityConfiguration {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
    http.csrf(AbstractHttpConfigurer::disable);
    http.formLogin(AbstractHttpConfigurer::disable);
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.logout(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(
        authorize ->
            authorize
                .requestMatchers(
                    "/actuator/health", "/api/auth/login/google", "/api/auth/callback/google")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/refresh")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/donations")
                .authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/donations/*")
                .authenticated()
                .requestMatchers("/api/auth/me", "/api/auth/logout")
                .authenticated()
                .anyRequest()
                .permitAll());

    if (StringUtils.hasText(issuerUri)) {
      http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    }

    return http.build();
  }
}
