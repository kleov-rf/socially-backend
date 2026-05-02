package com.socially.app.infrastructure.left.adapter.http.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class HttpBodyCachingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    ContentCachingRequestWrapper wrappedRequest =
        request instanceof ContentCachingRequestWrapper requestWrapper
            ? requestWrapper
            : new ContentCachingRequestWrapper(request, 4096);

    ContentCachingResponseWrapper wrappedResponse =
        response instanceof ContentCachingResponseWrapper responseWrapper
            ? responseWrapper
            : new ContentCachingResponseWrapper(response);

    try {
      filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
      wrappedResponse.copyBodyToResponse();
    }
  }
}
