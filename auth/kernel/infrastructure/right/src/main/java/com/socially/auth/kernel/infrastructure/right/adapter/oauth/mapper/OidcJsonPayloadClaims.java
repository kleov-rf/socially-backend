package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public final class OidcJsonPayloadClaims {
  String text(JsonNode payload, String name) {
    JsonNode node = payload.path(name);
    if (node.isMissingNode() || node.isNull()) {
      return null;
    }
    String value = node.asText();
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
