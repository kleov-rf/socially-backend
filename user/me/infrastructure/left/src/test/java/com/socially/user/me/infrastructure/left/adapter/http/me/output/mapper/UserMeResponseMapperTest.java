package com.socially.user.me.infrastructure.left.adapter.http.me.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

  private static User sampleUser() {
    return User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"), Email.from("a@b.com"), CREATED_AT)
        .withGivenName(Optional.of("J"))
        .withFamilyName(Optional.of("D"));
  }

  private static Donor sampleDonor() {
    return Donor.create(
            Id.from("660e8400-e29b-41d4-a716-446655440001"),
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            "d@e.com",
            CREATED_AT)
        .withGivenName(Optional.of("X"))
        .withFamilyName(Optional.of("Y"));
  }

  @Test
  void toResponse_should_map_user_id() {
    User user = sampleUser();
    UserMeResponse response = sut.toResponse(UserMeQueryResult.create(user));

    assertEquals("550e8400-e29b-41d4-a716-446655440000", response.user().id());
  }

  @Test
  void toResponse_should_map_user_email() {
    User user = sampleUser();
    UserMeResponse response = sut.toResponse(UserMeQueryResult.create(user));

    assertEquals("a@b.com", response.user().email());
  }

  @Test
  void toResponse_should_map_user_name() {
    User user =
        User.create(
                Id.from("550e8400-e29b-41d4-a716-446655440000"), Email.from("a@b.com"), CREATED_AT)
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    UserMeResponse response = sut.toResponse(UserMeQueryResult.create(user));

    assertEquals(Optional.of("Jane Doe"), response.user().name());
  }

  @Test
  void toResponse_should_set_user_name_to_empty_when_no_names() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"), Email.from("a@b.com"), CREATED_AT);
    UserMeResponse response = sut.toResponse(UserMeQueryResult.create(user));

    assertEquals(Optional.empty(), response.user().name());
  }

  @Test
  void toResponse_should_set_profiles_organization_to_empty() {
    User user = sampleUser();
    UserMeResponse response = sut.toResponse(UserMeQueryResult.create(user));

    assertEquals(Optional.empty(), response.profiles().organization());
  }

  @Test
  void toResponse_should_set_donor_to_empty_when_absent() {
    User user = sampleUser();
    UserMeResponse response = sut.toResponse(UserMeQueryResult.create(user));

    assertEquals(Optional.empty(), response.profiles().donor());
  }

  @Test
  void toResponse_should_map_donor_id_when_present() {
    User user = sampleUser();
    Donor donor = sampleDonor();
    UserMeResponse response =
        sut.toResponse(UserMeQueryResult.create(user).withDonor(Optional.of(donor)));

    assertEquals(
        Optional.of("660e8400-e29b-41d4-a716-446655440001"),
        response.profiles().donor().map(UserMeDonorProfileDto::id));
  }

  @Test
  void toResponse_should_map_donor_email_when_present() {
    User user = sampleUser();
    Donor donor = sampleDonor();
    UserMeResponse response =
        sut.toResponse(UserMeQueryResult.create(user).withDonor(Optional.of(donor)));

    assertEquals(
        Optional.of("d@e.com"), response.profiles().donor().map(UserMeDonorProfileDto::email));
  }

  @Test
  void toResponse_should_map_donor_given_name_when_present() {
    User user = sampleUser();
    Donor donor = sampleDonor();
    UserMeResponse response =
        sut.toResponse(UserMeQueryResult.create(user).withDonor(Optional.of(donor)));

    assertEquals(
        Optional.of("X"), response.profiles().donor().flatMap(UserMeDonorProfileDto::givenName));
  }

  @Test
  void toResponse_should_map_donor_family_name_when_present() {
    User user = sampleUser();
    Donor donor = sampleDonor();
    UserMeResponse response =
        sut.toResponse(UserMeQueryResult.create(user).withDonor(Optional.of(donor)));

    assertEquals(
        Optional.of("Y"), response.profiles().donor().flatMap(UserMeDonorProfileDto::familyName));
  }
}
