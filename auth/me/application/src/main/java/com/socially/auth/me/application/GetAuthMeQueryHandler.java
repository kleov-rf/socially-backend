package com.socially.auth.me.application;

import com.socially.auth.me.application.output.AuthMeQueryResult;
import com.socially.auth.me.application.port.left.GetAuthMeUseCase;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public final class GetAuthMeQueryHandler implements GetAuthMeUseCase {

  private final GetCurrentAuthUserUseCase getCurrentAuthUserUseCase;
  private final FindDonorByUserIdUseCase findDonorByUserIdUseCase;

  @Override
  public AuthMeQueryResult execute(Principal principal) {
    User user = getCurrentAuthUserUseCase.execute(principal);
    Optional<Donor> donor =
        findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(user.id().value().toString()));
    return new AuthMeQueryResult(user, donor);
  }
}
