package com.socially.socially_backend;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HelloWorldControllerTest {

  @Test
  void helloWorld_should_return_hello_world_message() {
    HelloWorldController controller = new HelloWorldController();
    Map<String, String> response = controller.helloWorld();
    assertEquals("Buenos días, mundo!", response.get("message"));
  }
}
