package org.main.hackerthon.api.service;

import lombok.RequiredArgsConstructor;
import org.main.hackerthon.api.domain.User;
import org.main.hackerthon.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public Optional<User> findByUniqueId(String uniqueId) {
    return userRepository.findByUniqueId(uniqueId);
  }
}