package com.oak.repository;

import com.oak.entity.Labels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelsRepository extends JpaRepository<Labels, Integer> {

    List<Labels> findByRepositoryId(Integer repositoryId);

    List<Labels> findByName(String name);
}