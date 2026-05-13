package com.socially.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
      "com.socially.app",
      "com.socially.auth",
      "com.socially.donation",
      "com.socially.donation.kernel.infrastructure.right",
      "com.socially.user"
    })
@ConfigurationPropertiesScan(basePackages = "com.socially.auth")
@EntityScan(
    basePackages = {
      "com.socially.donation.kernel.infrastructure.right",
      "com.socially.user.kernel.infrastructure.right"
    })
@EnableJpaRepositories(
    basePackages = {
      "com.socially.donation.kernel.infrastructure.right",
      "com.socially.user.kernel.infrastructure.right"
    })
public class SociallyBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(SociallyBackendApplication.class, args);
  }
}
