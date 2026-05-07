package com.socially.auth.logout.application.port.left;

import com.socially.auth.logout.application.output.LogoutResult;
import java.util.Map;

public interface LogoutUseCase {

  LogoutResult execute(Map<String, String> requestCookies);
}
