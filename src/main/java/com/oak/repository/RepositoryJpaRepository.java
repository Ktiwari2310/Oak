package com.oak.repository;

import com.oak.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryJpaRepository extends JpaRepository<Repository, Long> {
}
