package com.oak.controller;

import com.oak.entity.PullRequest;
import com.oak.exception.GlobalExceptionHandler;
import com.oak.repository.IssuesRepository;
import com.oak.repository.PullRequestRepository;
import com.oak.repository.RepositoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PullRequestController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost")
class PullRequestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PullRequestRepository pullRequestRepository;

    @MockitoBean
    private IssuesRepository issuesRepository;

    @MockitoBean
    private RepositoryRepository repositoryRepository;

    @Test
    void mergingAlreadyMergedPullRequestReturnsConflict() throws Exception {
        PullRequest pullRequest = new PullRequest();
        pullRequest.setMerged(true);
        when(pullRequestRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(pullRequest));

        mockMvc.perform(patch("/api/pull-requests/3/merge"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Pull request has already been merged"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
