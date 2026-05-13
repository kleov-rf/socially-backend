package com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.UserResponseDto;
import com.socially.user.kernel.domain.entity.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserResponseDtoMapper {

  public UserResponseDto toResponse(User user) {
    return new UserResponseDto(
        user.id().value().toString(),
        user.email().value(),
        joinNames(user.givenName(), user.familyName()));
  }

  public UserResponseDto toResponse(AuthUser authUser) {
    return new UserResponseDto(
        authUser.id(), authUser.email(), joinNames(authUser.givenName(), authUser.familyName()));
  }

  private String joinNames(String givenName, String familyName) {
    List<String> values = new ArrayList<>();
    if (StringUtils.hasText(givenName)) {
      values.add(givenName);
    }
    if (StringUtils.hasText(familyName)) {
      values.add(familyName);
    }
    if (values.isEmpty()) {
      return null;
    }
    return String.join(" ", values);
  }
}
