package com.socially.auth.me.infrastructure.left.adapter.http.me.output.mapper;

import com.socially.auth.me.application.output.AuthMeQueryResult;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthMeDonorProfileDto;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthMeProfilesDto;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthMeResponse;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthMeUserDto;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMeResponseMapper {

  public AuthMeResponse toResponse(AuthMeQueryResult result) {
    User user = result.user();
    AuthMeUserDto userDto = new AuthMeUserDto(user.id().value().toString(), user.email().value());
    AuthMeDonorProfileDto donorDto = result.donor().map(this::toDonorProfileDto).orElse(null);
    AuthMeProfilesDto profiles = new AuthMeProfilesDto(donorDto, null);
    return new AuthMeResponse(userDto, profiles);
  }

  private AuthMeDonorProfileDto toDonorProfileDto(Donor donor) {
    return new AuthMeDonorProfileDto(
        donor.id().value().toString(), donor.email(), donor.givenName(), donor.familyName());
  }
}
