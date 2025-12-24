package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository;

import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
