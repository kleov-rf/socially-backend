package com.socially.donation.find.infrastructure.left.adapter.http.find.output;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PageResponse<T>(@NotNull List<T> items, @NotNull MetadataResponse page) {}
