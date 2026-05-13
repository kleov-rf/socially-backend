package com.socially.user.findbyemail.application;

import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.application.port.left.FindUserByEmailUseCase;
import com.socially.user.findbyemail.domain.port.right.FindUserByEmailRepository;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class FindUserByEmailQueryHandler implements FindUserByEmailUseCase {

  private final FindUserByEmailRepository findUserByEmailRepository;

  @Override
  public Optional<User> execute(FindUserByEmailQuery query) {
    Email email = Email.from(query.email());
    return findUserByEmailRepository.findByEmail(email);
  }
}
