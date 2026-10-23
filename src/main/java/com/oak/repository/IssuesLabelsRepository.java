package com.oak.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oak.entity.IssuesLabels;

public interface IssuesLabelsRepository extends JpaRepository<IssuesLabels, Integer> {

    List<IssuesLabels> findByIssueId(Integer issueId);

    List<IssuesLabels> findByLabelId(Integer labelId);
}