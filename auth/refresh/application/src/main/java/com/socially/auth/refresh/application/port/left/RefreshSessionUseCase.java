package com.socially.auth.refresh.application.port.left;

import com.socially.auth.refresh.application.output.RefreshSessionCommandResult;
import java.util.Map;

public interface RefreshSessionUseCase {

  RefreshSessionCommandResult execute(Map<String, String> requestCookies);
}
