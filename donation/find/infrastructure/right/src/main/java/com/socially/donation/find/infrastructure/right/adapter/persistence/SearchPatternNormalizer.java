package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.filter.FilterCriteria;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class SearchPatternNormalizer {

  public String toSearchPattern(FilterCriteria filterCriteria) {
    if (Objects.isNull(filterCriteria) || Objects.isNull(filterCriteria.query())) {
      return null;
    }
    return "%" + filterCriteria.query().toLowerCase(Locale.ROOT) + "%";
  }
}
