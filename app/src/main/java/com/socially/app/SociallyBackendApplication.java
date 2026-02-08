package com.socially.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
      "com.socially.app",
      "com.socially.donation.application",
      "com.socially.donation.infrastructure.left",
      "com.socially.donation.infrastructure.right"
    })
public class SociallyBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(SociallyBackendApplication.class, args);
  }
}
