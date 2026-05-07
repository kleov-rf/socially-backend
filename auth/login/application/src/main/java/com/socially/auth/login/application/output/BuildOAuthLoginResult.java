package com.socially.auth.login.application.output;

import com.socially.auth.kernel.domain.CookieInstruction;
import java.util.List;

public record BuildOAuthLoginResult(
    String authorizeUrl, List<CookieInstruction> cookieInstructions) {}
