package com.socially.auth.me.infrastructure.left.adapter.http.me;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthUserResponse;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.mapper.AuthUserResponseMapper;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GetCurrentAuthUserControllerTest {

  @Mock private GetCurrentAuthUserUseCase useCase;
  @Mock private AuthUserResponseMapper authUserResponseMapper;

  @InjectMocks private GetCurrentAuthUserController sut;

  @Test
  void me_should_call_use_case_with_received_principal() {
    Principal principal = () -> "ignored";
    when(useCase.execute(principal)).thenReturn(new AuthUser("id", "e@x.com", "N"));

    sut.me(principal);

    verify(useCase).execute(principal);
  }

  @Test
  void me_should_call_response_mapper_with_retrieved_user_from_use_case() {
    Principal principal = () -> "ignored";
    AuthUser authUser = new AuthUser("id-1", "a@b.com", "Full");
    when(useCase.execute(principal)).thenReturn(authUser);
    when(authUserResponseMapper.toResponse(authUser))
        .thenReturn(new AuthUserResponse("id-1", "a@b.com", "Full"));

    sut.me(principal);

    verify(authUserResponseMapper).toResponse(authUser);
  }

  @Test
  void me_should_return_response_with_ok_status() {
    Principal principal = () -> "ignored";
    when(useCase.execute(principal)).thenReturn(new AuthUser("id", "e@x.com", null));
    when(authUserResponseMapper.toResponse(any(AuthUser.class)))
        .thenReturn(new AuthUserResponse("id", "e@x.com", null));

    ResponseEntity<AuthUserResponse> response = sut.me(principal);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void me_should_return_response_with_mapped_user_response() {
    Principal principal = () -> "ignored";
    AuthUser authUser = new AuthUser("sub-x", "user@example.com", "Jane Doe");
    AuthUserResponse mapped = new AuthUserResponse("sub-x", "user@example.com", "Jane Doe");
    when(useCase.execute(principal)).thenReturn(authUser);
    when(authUserResponseMapper.toResponse(authUser)).thenReturn(mapped);

    ResponseEntity<AuthUserResponse> response = sut.me(principal);

    assertEquals(mapped, response.getBody());
  }
}
