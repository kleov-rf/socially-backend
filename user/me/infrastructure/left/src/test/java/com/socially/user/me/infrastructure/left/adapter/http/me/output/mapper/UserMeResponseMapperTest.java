package com.socially.user.me.infrastructure.left.adapter.http.me.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.me.application.output.UserMeQueryResult;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeDonorProfileDto;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeResponse;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UserMeResponseMapperTest {

  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  private final UserMeResponseMapper sut = new UserMeResponseMapper();

  @Test
  void toResponse_should_map_user_id() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("a@b.com"),
            "J",
            "D",
            CREATED_AT);
    UserMeResponse response = sut.toResponse(new UserMeQueryResult(user, Optional.empty()));

    assertEquals("550e8400-e29b-41d4-a716-446655440000", response.user().id());
  }

  @Test
  void toResponse_should_map_user_email() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("a@b.com"),
            "J",
            "D",
            CREATED_AT);
    UserMeResponse response = sut.toResponse(new UserMeQueryResult(user, Optional.empty()));

    assertEquals("a@b.com", response.user().email());
  }

  @Test
  void toResponse_should_set_profiles_organization_to_null() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("a@b.com"),
            "J",
            "D",
            CREATED_AT);
    UserMeResponse response = sut.toResponse(new UserMeQueryResult(user, Optional.empty()));

    assertNull(response.profiles().organization());
  }

  @Test
  void toResponse_should_set_donor_to_null_when_absent() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("a@b.com"),
            "J",
            "D",
            CREATED_AT);
    UserMeResponse response = sut.toResponse(new UserMeQueryResult(user, Optional.empty()));

    assertNull(response.profiles().donor());
  }

  @Test
  void toResponse_should_map_donor_id_when_present() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("a@b.com"),
            "J",
            "D",
            CREATED_AT);
    Donor donor =
        Donor.create(
            Id.from("660e8400-e29b-41d4-a716-446655440001"),
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            "d@e.com",
            "X",
            "Y",
            CREATED_AT);
    UserMeResponse response = sut.toResponse(new UserMeQueryResult(user, Optional.of(donor)));

    UserMeDonorProfileDto donorDto = response.profiles().donor();
    assertEquals("660e8400-e29b-41d4-a716-446655440001", donorDto.id());
  }

  @Test
  void toResponse_should_map_donor_email_when_present() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("a@b.com"),
            "J",
            "D",
            CREATED_AT);
    Donor donor =
        Donor.create(
            Id.from("660e8400-e29b-41d4-a716-446655440001"),
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            "d@e.com",
            "X",
            "Y",
            CREATED_AT);
    UserMeResponse response = sut.toResponse(new UserMeQueryResult(user, Optional.of(donor)));

    assertEquals("d@e.com", response.profiles().donor().email());
  }

  @Test
  void toResponse_should_map_donor_given_name_when_present() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("a@b.com"),
            "J",
            "D",
            CREATED_AT);
    Donor donor =
        Donor.create(
            Id.from("660e8400-e29b-41d4-a716-446655440001"),
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            "d@e.com",
            "X",
            "Y",
            CREATED_AT);
    UserMeResponse response = sut.toResponse(new UserMeQueryResult(user, Optional.of(donor)));

    assertEquals("X", response.profiles().donor().givenName());
  }

  @Test
  void toResponse_should_map_donor_family_name_when_present() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("a@b.com"),
            "J",
            "D",
            CREATED_AT);
    Donor donor =
        Donor.create(
            Id.from("660e8400-e29b-41d4-a716-446655440001"),
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            "d@e.com",
            "X",
            "Y",
            CREATED_AT);
    UserMeResponse response = sut.toResponse(new UserMeQueryResult(user, Optional.of(donor)));

    assertEquals("Y", response.profiles().donor().familyName());
  }
}
