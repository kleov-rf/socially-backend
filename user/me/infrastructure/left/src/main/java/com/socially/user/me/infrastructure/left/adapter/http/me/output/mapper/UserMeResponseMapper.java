package com.socially.user.me.infrastructure.left.adapter.http.me.output.mapper;

import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.me.application.output.UserMeQueryResult;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeDonorProfileDto;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeProfilesDto;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeResponse;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeUserDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserMeResponseMapper {

  public UserMeResponse toResponse(UserMeQueryResult result) {
    User user = result.user();
    UserMeUserDto userDto =
        UserMeUserDto.create(user.id().value().toString(), user.email().value())
            .withName(joinNames(user.givenName(), user.familyName()));
    Optional<UserMeDonorProfileDto> donorDto = result.donor().map(this::toDonorProfileDto);
    UserMeProfilesDto profiles = UserMeProfilesDto.create().withDonor(donorDto);
    return UserMeResponse.create(userDto, profiles);
  }

  private UserMeDonorProfileDto toDonorProfileDto(Donor donor) {
    return UserMeDonorProfileDto.create(donor.id().value().toString(), donor.email())
        .withGivenName(donor.givenName())
        .withFamilyName(donor.familyName());
  }

  private Optional<String> joinNames(Optional<String> givenName, Optional<String> familyName) {
    List<String> values = new ArrayList<>();
    givenName.filter(StringUtils::hasText).ifPresent(values::add);
    familyName.filter(StringUtils::hasText).ifPresent(values::add);
    if (values.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(String.join(" ", values));
  }
}
