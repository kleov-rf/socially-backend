package com.socially.app.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(CucumberSpringConfiguration.TestConfig.class)
public class CucumberSpringConfiguration {

  @TestConfiguration
  static class TestConfig {

    @Bean
    public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
      return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }
}
