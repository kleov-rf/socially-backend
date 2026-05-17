package com.socially.user.me.application;

import com.socially.auth.kernel.application.port.left.GetAuthenticatedUserUseCase;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.me.application.output.UserMeQueryResult;
import com.socially.user.me.application.port.left.GetUserMeUseCase;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public final class GetUserMeQueryHandler implements GetUserMeUseCase {

  private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
  private final FindDonorByUserIdUseCase findDonorByUserIdUseCase;

  @Override
  public UserMeQueryResult execute(Principal principal) {
    User user = getAuthenticatedUserUseCase.execute(principal);
    Optional<Donor> donor =
        findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(user.id().value().toString()));
    return new UserMeQueryResult(user, donor);
  }
}
