package com.socially.app.config;

import com.socially.app.infrastructure.left.adapter.http.logging.RequestResponseLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcLoggingConfiguration implements WebMvcConfigurer {

  private final RequestResponseLoggingInterceptor requestResponseLoggingInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestResponseLoggingInterceptor);
  }
}
