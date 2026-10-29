package com.oak.controller;

import com.oak.entity.Issues;
import com.oak.entity.Repository;
import com.oak.entity.User;
import com.oak.exception.GlobalExceptionHandler;
import com.oak.repository.IssuesRepository;
import com.oak.repository.RepositoryRepository;
import com.oak.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IssuesController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost")
class IssueControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IssuesRepository issuesRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RepositoryRepository repositoryRepository;

    @Test
    void createsIssueWithServerAssignedNumber() throws Exception {
        User poster = new User();
        poster.setId(7L);
        Repository repository = new Repository();
        repository.setId(9L);

        when(userRepository.findById(7L)).thenReturn(Optional.of(poster));
        when(repositoryRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(repository));
        when(issuesRepository.findMaxIssueNumberByRepositoryId(9L)).thenReturn(0);
        when(issuesRepository.save(any(Issues.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryId": 9,
                                  "posterId": 7,
                                  "title": "First issue",
                                  "content": "Issue details"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.issueNumber").value(1))
                .andExpect(jsonPath("$.message").value("Issue created successfully"));
    }

    @Test
    void rejectsInvalidIssueRequest() throws Exception {
        mockMvc.perform(post("/api/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryId": 9,
                                  "posterId": 7,
                                  "title": " ",
                                  "content": "Issue details"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.data.title").exists());

        verify(issuesRepository, never()).save(any(Issues.class));
    }

    @Test
    void updateIssueDoesNotChangePosterOrRepository() throws Exception {
        User originalPoster = new User();
        originalPoster.setId(7L);
        Repository originalRepository = new Repository();
        originalRepository.setId(9L);
        Issues existing = new Issues();
        existing.setId(3);
        existing.setPoster(originalPoster);
        existing.setRepository(originalRepository);
        existing.setTitle("Old title");
        existing.setContent("Old content");

        when(issuesRepository.findById(3)).thenReturn(Optional.of(existing));
        when(issuesRepository.save(any(Issues.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/issues/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated title",
                                  "posterId": 11,
                                  "repositoryId": 13
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated title"))
                .andExpect(jsonPath("$.data.posterId").value(7))
                .andExpect(jsonPath("$.data.repositoryId").value(9));
    }
}
