package com.socially.auth.login.application;

import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.login.application.mapper.LoginAuthorizeUrlMapper;
import com.socially.auth.login.application.output.BuildOAuthLoginResult;
import com.socially.auth.login.application.port.left.BuildOAuthLoginUrlUseCase;
import com.socially.auth.login.application.security.AuthCookieFactory;
import com.socially.auth.login.application.security.OAuthPkceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BuildOAuthLoginUrlCommandHandler implements BuildOAuthLoginUrlUseCase {
  private final AuthProperties authProperties;
  private final OAuthPkceService oAuthPkceService;
  private final AuthCookieFactory authCookieFactory;
  private final LoginAuthorizeUrlMapper loginAuthorizeUrlMapper;

  @Override
  public BuildOAuthLoginResult execute() {
    String stateNonce = oAuthPkceService.generateRandomToken();
    String pkceCodeVerifier = oAuthPkceService.generateRandomToken();
    String pkceCodeChallenge = oAuthPkceService.generateCodeChallenge(pkceCodeVerifier);

    var stateCookieInstruction =
        authCookieFactory.oauthTransientCookie(authProperties.stateCookieName(), stateNonce);
    var pkceCookieInstruction =
        authCookieFactory.oauthTransientCookie(authProperties.pkceCookieName(), pkceCodeVerifier);

    return new BuildOAuthLoginResult(
        loginAuthorizeUrlMapper.toAuthorizeUrl(stateNonce, pkceCodeChallenge),
        List.of(stateCookieInstruction, pkceCookieInstruction));
  }
}
