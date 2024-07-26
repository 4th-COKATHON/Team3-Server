package org.main.hackerthon.api.repository;

import org.main.hackerthon.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUniqueId(String uniqueId);


}
