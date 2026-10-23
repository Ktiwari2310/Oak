package com.oak.repository;

import com.oak.entity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {

}