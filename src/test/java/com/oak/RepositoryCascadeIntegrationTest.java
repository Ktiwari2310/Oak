package com.oak;

import com.oak.entity.Comment;
import com.oak.entity.CommentType;
import com.oak.entity.Issues;
import com.oak.entity.IssuesLabels;
import com.oak.entity.Labels;
import com.oak.entity.PullRequest;
import com.oak.entity.Repository;
import com.oak.entity.User;
import com.oak.repository.CommentRepository;
import com.oak.repository.IssuesLabelsRepository;
import com.oak.repository.IssuesRepository;
import com.oak.repository.LabelsRepository;
import com.oak.repository.PullRequestRepository;
import com.oak.repository.RepositoryRepository;
import com.oak.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:oak-cascade;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
class RepositoryCascadeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private IssuesRepository issuesRepository;

    @Autowired
    private LabelsRepository labelsRepository;

    @Autowired
    private IssuesLabelsRepository issuesLabelsRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Test
    void deletingRepositoryRemovesItsIssueAndLabelDependents() throws Exception {
        User owner = new User();
        owner.setUsername("cascade-owner");
        owner.setFull_name("Cascade Owner");
        owner.setEmail("cascade-owner@example.com");
        owner.setPassword_hash("test-hash");
        owner = userRepository.saveAndFlush(owner);

        Repository repository = new Repository();
        repository.setOwner(owner);
        repository.setName("cascade-repo");
        repository.setDescription("Repository for cascade test");
        repository.setIs_private(false);
        repository.setIs_mirror(false);
        repository.setClone_url("https://example.test/cascade-repo.git");
        repository = repositoryRepository.saveAndFlush(repository);

        Issues issue = new Issues();
        issue.setRepository(repository);
        issue.setPoster(owner);
        issue.setIssueNumber(1);
        issue.setTitle("Cascade issue");
        issue.setContent("Issue content");
        issue = issuesRepository.saveAndFlush(issue);

        Labels label = new Labels();
        label.setRepository(repository);
        label.setName("cascade");
        label.setColor("#123456");
        label = labelsRepository.saveAndFlush(label);

        IssuesLabels issueLabel = issuesLabelsRepository.saveAndFlush(new IssuesLabels(issue, label));

        Comment comment = new Comment();
        comment.setIssue(issue);
        comment.setPoster(owner);
        comment.setType(CommentType.COMMENT);
        comment.setContent("Cascade comment");
        comment = commentRepository.saveAndFlush(comment);

        PullRequest pullRequest = new PullRequest();
        pullRequest.setIssues(issue);
        pullRequest.setHeadRepo(repository);
        pullRequest.setBaseRepo(repository);
        pullRequest.setHeadBranch("feature");
        pullRequest.setBaseBranch("main");
        pullRequest.setMergeBranchSha("0123456789012345678901234567890123456789");
        pullRequest = pullRequestRepository.saveAndFlush(pullRequest);

        mockMvc.perform(delete("/api/repositories/{id}", repository.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(repositoryRepository.existsById(repository.getId())).isFalse();
        assertThat(issuesRepository.existsById(issue.getId())).isFalse();
        assertThat(labelsRepository.existsById(label.getId())).isFalse();
        assertThat(issuesLabelsRepository.existsById(issueLabel.getId())).isFalse();
        assertThat(commentRepository.existsById(comment.getId())).isFalse();
        assertThat(pullRequestRepository.existsById(pullRequest.getId())).isFalse();
        assertThat(userRepository.existsById(owner.getId())).isTrue();
    }
}
