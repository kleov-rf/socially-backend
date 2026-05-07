package com.socially.auth.callback.infrastructure.left.adapter.http.callback.input;

import jakarta.validation.constraints.NotBlank;

public record CallbackGoogleRequest(@NotBlank String code, @NotBlank String state) {}
