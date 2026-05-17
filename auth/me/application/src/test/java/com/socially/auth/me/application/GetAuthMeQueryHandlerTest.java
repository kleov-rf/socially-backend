package com.socially.auth.me.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.me.application.output.AuthMeQueryResult;
import com.socially.auth.me.application.port.left.GetAuthenticatedUserUseCase;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAuthMeQueryHandlerTest {

  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
  @Mock private FindDonorByUserIdUseCase findDonorByUserIdUseCase;

  @InjectMocks private GetAuthMeQueryHandler handler;

  @Test
  void execute_should_call_get_authenticated_user_use_case_with_received_principal() {
    Principal principal = () -> "ignored";
    User user = sampleUser();
    when(getAuthenticatedUserUseCase.execute(same(principal))).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    handler.execute(principal);

    verify(getAuthenticatedUserUseCase).execute(same(principal));
  }

  @Test
  void execute_should_call_find_donor_by_user_id_with_user_id_string() {
    Principal principal = () -> "ignored";
    User user = sampleUser();
    when(getAuthenticatedUserUseCase.execute(principal)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    handler.execute(principal);

    verify(findDonorByUserIdUseCase)
        .execute(new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000"));
  }

  @Test
  void execute_should_return_empty_donor_when_no_donor_profile() {
    Principal principal = () -> "ignored";
    User user = sampleUser();
    when(getAuthenticatedUserUseCase.execute(principal)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    AuthMeQueryResult result = handler.execute(principal);

    assertEquals(user, result.user());
    assertTrue(result.donor().isEmpty());
  }

  @Test
  void execute_should_return_donor_when_profile_present() {
    Principal principal = () -> "ignored";
    User user = sampleUser();
    Donor donor = sampleDonor();
    when(getAuthenticatedUserUseCase.execute(principal)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(
            eq(new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000"))))
        .thenReturn(Optional.of(donor));

    AuthMeQueryResult result = handler.execute(principal);

    assertEquals(Optional.of(donor), result.donor());
  }

  private static User sampleUser() {
    return User.create(
        Id.from("550e8400-e29b-41d4-a716-446655440000"),
        Email.from("me@example.com"),
        "Jane",
        "Doe",
        CREATED_AT);
  }

  private static Donor sampleDonor() {
    return Donor.create(
        Id.from("660e8400-e29b-41d4-a716-446655440001"),
        Id.from("550e8400-e29b-41d4-a716-446655440000"),
        "me@example.com",
        "Jane",
        "Doe",
        CREATED_AT);
  }
}
