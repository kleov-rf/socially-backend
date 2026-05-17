package com.socially.donation.create.infrastructure.left.adapter.http.create;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.socially.donation.create.application.port.left.CreateDonationUseCase;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.mapper.CreateDonationRequestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CreateDonationController.class)
@Import(CreateDonationApiErrorHandlingMvcTest.SecurityTestConfig.class)
class CreateDonationApiErrorHandlingMvcTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CreateDonationUseCase createDonationUseCase;

  @MockitoBean private CreateDonationRequestMapper createDonationRequestMapper;

  @Test
  void createDonation_should_return_unauthorized_when_anonymous() throws Exception {
    mockMvc
        .perform(
            post("/api/donations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "id": "550e8400-e29b-41d4-a716-446655440001",
                      "title": "Title",
                      "description": "Description"
                    }
                    """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void createDonation_should_return_validation_error_with_field_errors_when_body_invalid()
      throws Exception {
    mockMvc
        .perform(
            post("/api/donations")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fieldErrors").isArray())
        .andExpect(jsonPath("$.message").exists());
  }

  @TestConfiguration
  @EnableWebSecurity
  static class SecurityTestConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
          .exceptionHandling(
              exceptions ->
                  exceptions.authenticationEntryPoint(
                      new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
          .authorizeHttpRequests(
              authorize ->
                  authorize
                      .requestMatchers(HttpMethod.POST, "/api/donations")
                      .authenticated()
                      .anyRequest()
                      .permitAll());
      return http.build();
    }
  }
}
