package com.oak.repository;

import com.oak.entity.Issues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuesRepository extends JpaRepository<Issues, Long> {
}
