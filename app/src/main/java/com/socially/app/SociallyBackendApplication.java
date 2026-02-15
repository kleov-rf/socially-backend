package com.socially.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
      "com.socially.app",
      "com.socially.donation.application",
      "com.socially.donation.infrastructure.left",
      "com.socially.donation.infrastructure.right"
    })
@EntityScan(basePackages = "com.socially.donation.infrastructure.right")
@EnableJpaRepositories(basePackages = "com.socially.donation.infrastructure.right")
public class SociallyBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(SociallyBackendApplication.class, args);
  }
}
