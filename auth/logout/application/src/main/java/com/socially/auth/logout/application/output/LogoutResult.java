package com.socially.auth.logout.application.output;

import com.socially.auth.kernel.domain.CookieInstruction;
import java.util.List;

public record LogoutResult(List<CookieInstruction> cookieInstructions) {}
