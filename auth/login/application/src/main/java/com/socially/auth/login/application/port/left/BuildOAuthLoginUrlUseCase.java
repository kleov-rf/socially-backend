package com.socially.auth.login.application.port.left;

import com.socially.auth.login.application.output.BuildOAuthLoginResult;

public interface BuildOAuthLoginUrlUseCase {
  BuildOAuthLoginResult execute();
}
