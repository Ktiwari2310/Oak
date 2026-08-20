package com.oak.repository;

import com.oak.entity.IssuesLabels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssuesLabelsRepository extends JpaRepository<IssuesLabels, Integer> {

    List<IssuesLabels> findByIssueId(Integer issueId);

    List<IssuesLabels> findByLabelId(Integer labelId);
}