package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository;

import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyJpaRepository extends JpaRepository<PropertyEntity, String> {
}
