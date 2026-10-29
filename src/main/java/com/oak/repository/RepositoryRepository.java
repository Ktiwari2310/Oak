package com.oak.repository;

import com.oak.entity.Repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select repository from Repository repository where repository.id = :id")
    Optional<Repository> findByIdForUpdate(@Param("id") Long id);
}