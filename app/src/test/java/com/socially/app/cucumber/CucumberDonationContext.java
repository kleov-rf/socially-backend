package com.socially.app.cucumber;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@ScenarioScope
@Component
public class CucumberDonationContext {

  private String currentDonationId;

  public String getCurrentDonationId() {
    return currentDonationId;
  }

  public void setCurrentDonationId(String currentDonationId) {
    this.currentDonationId = currentDonationId;
  }
}
