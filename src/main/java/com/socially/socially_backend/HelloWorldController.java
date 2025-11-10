package com.socially.socially_backend;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

  @GetMapping("/hello")
  public Map<String, String> helloWorld() {
    return Map.of("message", "hello world");
  }
}
