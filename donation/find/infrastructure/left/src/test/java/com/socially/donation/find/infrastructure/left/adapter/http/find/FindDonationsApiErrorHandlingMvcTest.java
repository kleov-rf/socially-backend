package com.socially.donation.find.infrastructure.left.adapter.http.find;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.socially.donation.find.application.port.left.FindDonationsUseCase;
import com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper.FindDonationsQueryMapper;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper.PageResponseMapper;
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

@WebMvcTest(FindDonationsController.class)
@Import({
  FindDonationsQueryMapper.class,
  FindDonationsApiErrorHandlingMvcTest.SecurityTestConfig.class
})
@TestPropertySource(
    properties = {
      "error.handling.codes.com.socially.donation.find.domain.exception.FindDonationsBadRequestException=FIND_DONATIONS_BAD_REQUEST"
    })
class FindDonationsApiErrorHandlingMvcTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private FindDonationsUseCase findDonationsUseCase;

  @MockitoBean private PageResponseMapper pageResponseMapper;

  @Test
  void find_should_return_bad_request_when_nearest_first_without_coordinates() throws Exception {
    mockMvc
        .perform(get("/api/donations").param("order", "nearest_first"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("FIND_DONATIONS_BAD_REQUEST"))
        .andExpect(
            jsonPath("$.message")
                .value("latitude and longitude are required when order is nearest_first"));
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
                      .requestMatchers(HttpMethod.GET, "/api/donations")
                      .permitAll()
                      .anyRequest()
                      .authenticated());
      return http.build();
    }
  }
}
