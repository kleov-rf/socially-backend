package com.socially.donation.find.infrastructure.left.adapter.http.find.output;

public record MetadataResponse(
    String nextCursor, String previousCursor, Boolean hasNext, Integer size) {}
