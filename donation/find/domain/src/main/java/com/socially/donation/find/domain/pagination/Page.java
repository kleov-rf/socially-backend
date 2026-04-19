package com.socially.donation.find.domain.pagination;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Page<T> {
  private final List<T> items;
  private final Metadata metadata;

  public static <T> Page<T> create(List<T> items, Metadata metadata) {
    validate(items, metadata);
    return new Page<>(items, metadata);
  }

  private static <T> void validate(List<T> items, Metadata metadata) {
    if (items == null) {
      throw new IllegalArgumentException("Items cannot be null");
    }
    if (metadata == null) {
      throw new IllegalArgumentException("Metadata cannot be null");
    }
  }

  public List<T> items() {
    return items;
  }

  public Metadata metadata() {
    return metadata;
  }
}
