package com.oak.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oak.entity.Labels;

public interface LabelsRepository extends JpaRepository<Labels, Integer> {

    List<Labels> findByRepositoryId(Long repositoryId);

    List<Labels> findByName(String name);
}