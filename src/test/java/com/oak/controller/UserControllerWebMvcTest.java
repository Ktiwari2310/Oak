package com.oak.controller;

import com.oak.entity.User;
import com.oak.exception.GlobalExceptionHandler;
import com.oak.repository.UserRepository;
import com.oak.config.PasswordEncoderConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class, PasswordEncoderConfig.class})
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost")
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void duplicateUsernameReturnsConflict() throws Exception {
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate username"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "taken",
                                  "fullName": "Existing User",
                                  "email": "taken@example.com",
                                  "password": "valid-password"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The request conflicts with existing data"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void rejectsPasswordShorterThanEightCharactersOnCreate() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "new-user",
                                  "fullName": "New User",
                                  "email": "new@example.com",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.password").exists());
    }

    @Test
    void rejectsPasswordShorterThanEightCharactersOnUpdate() throws Exception {
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.password").exists());
    }

    @Test
    void updateWithoutPasswordSucceedsAndPreservesExistingHash() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("before");
        user.setFull_name("Existing User");
        user.setEmail("existing@example.com");
        user.setPassword_hash("existing-hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "after"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("after"));

        verify(userRepository).save(eq(user));
        assertThat(user.getPassword_hash()).isEqualTo("existing-hash");
    }

    @Test
    void unknownUrlReturnsNotFoundEnvelope() throws Exception {
        mockMvc.perform(get("/api/unknown-route"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void unsupportedMethodReturnsMethodNotAllowedEnvelope() throws Exception {
        mockMvc.perform(patch("/api/users/1"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").value("HTTP method not supported for this endpoint"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void unsupportedContentTypeReturnsUnsupportedMediaTypeEnvelope() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not-json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message").value("Content type is not supported"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void invalidPathVariableTypeReturnsBadRequestEnvelope() throws Exception {
        mockMvc.perform(get("/api/users/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request parameter has an invalid value"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
