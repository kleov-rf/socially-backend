package com.socially.user.me.infrastructure.left.adapter.http.me.output.mapper;

import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.me.application.output.UserMeQueryResult;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeDonorProfileDto;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeProfilesDto;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeResponse;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeUserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMeResponseMapper {

  public UserMeResponse toResponse(UserMeQueryResult result) {
    User user = result.user();
    UserMeUserDto userDto = new UserMeUserDto(user.id().value().toString(), user.email().value());
    UserMeDonorProfileDto donorDto = result.donor().map(this::toDonorProfileDto).orElse(null);
    UserMeProfilesDto profiles = new UserMeProfilesDto(donorDto, null);
    return new UserMeResponse(userDto, profiles);
  }

  private UserMeDonorProfileDto toDonorProfileDto(Donor donor) {
    return new UserMeDonorProfileDto(
        donor.id().value().toString(), donor.email(), donor.givenName(), donor.familyName());
  }
}
