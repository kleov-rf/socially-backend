package com.socially.socially_backend;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldControllerTest {

    @Test
    void helloWorld_should_return_hello_world_message() {
        HelloWorldController controller = new HelloWorldController();
        Map<String, String> response = controller.helloWorld();
        assertEquals("hello world", response.get("message"));
    }

}