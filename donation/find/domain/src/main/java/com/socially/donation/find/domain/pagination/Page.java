package com.socially.donation.find.domain.pagination;

import java.util.List;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Page<T>(List<T> items, Metadata metadata) {

  public static <T> Page<T> create(List<T> items, Metadata metadata) {
    return new Page<>(items, metadata);
  }
}
