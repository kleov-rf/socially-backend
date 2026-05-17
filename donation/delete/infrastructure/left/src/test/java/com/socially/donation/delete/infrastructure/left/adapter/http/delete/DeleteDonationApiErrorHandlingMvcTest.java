package com.socially.donation.delete.infrastructure.left.adapter.http.delete;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.socially.donation.delete.application.DonationForbiddenException;
import com.socially.donation.delete.application.DonationNotFoundException;
import com.socially.donation.delete.application.port.left.DeleteDonationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeleteDonationController.class)
@Import(DeleteDonationApiErrorHandlingMvcTest.SecurityTestConfig.class)
@TestPropertySource(
    properties = {
      "error.handling.codes.com.socially.donation.delete.application.DonationNotFoundException=DONATION_NOT_FOUND",
      "error.handling.codes.com.socially.donation.delete.application.DonationForbiddenException=DONATION_FORBIDDEN"
    })
class DeleteDonationApiErrorHandlingMvcTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DeleteDonationUseCase deleteDonationUseCase;

  @Test
  void deleteDonation_should_return_not_found_with_code_and_message_when_donation_missing()
      throws Exception {
    doThrow(new DonationNotFoundException(DONATION_ID)).when(deleteDonationUseCase).execute(any());

    mockMvc
        .perform(delete("/api/donations/{id}", DONATION_ID).with(jwt()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("DONATION_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Donation not found: " + DONATION_ID));
  }

  @Test
  void deleteDonation_should_return_forbidden_with_code_and_message_when_donation_forbidden()
      throws Exception {
    doThrow(new DonationForbiddenException(DONATION_ID)).when(deleteDonationUseCase).execute(any());

    mockMvc
        .perform(delete("/api/donations/{id}", DONATION_ID).with(jwt()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("DONATION_FORBIDDEN"))
        .andExpect(jsonPath("$.message").value("Donation forbidden: " + DONATION_ID));
  }

  @TestConfiguration
  @EnableWebSecurity
  static class SecurityTestConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(
              authorize ->
                  authorize
                      .requestMatchers(HttpMethod.DELETE, "/api/donations/*")
                      .authenticated()
                      .anyRequest()
                      .permitAll());
      return http.build();
    }
  }
}
