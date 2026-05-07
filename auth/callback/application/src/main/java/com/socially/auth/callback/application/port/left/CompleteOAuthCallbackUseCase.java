package com.socially.auth.callback.application.port.left;

import com.socially.auth.callback.application.output.CompleteOAuthCallbackOutcome;
import java.util.Map;

public interface CompleteOAuthCallbackUseCase {

  CompleteOAuthCallbackOutcome execute(
      String code, String state, Map<String, String> requestCookies);
}
