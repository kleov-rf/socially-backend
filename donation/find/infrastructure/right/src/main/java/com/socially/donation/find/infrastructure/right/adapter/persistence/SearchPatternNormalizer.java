package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.filter.FilterCriteria;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SearchPatternNormalizer {

  public Optional<String> toSearchPattern(FilterCriteria filterCriteria) {
    return filterCriteria.query().map(q -> "%" + q.toLowerCase(Locale.ROOT) + "%");
  }
}
