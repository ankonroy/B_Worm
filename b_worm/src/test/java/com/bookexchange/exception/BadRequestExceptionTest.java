package com.bookexchange.exception;

import com.bookexchange.dto.request.RegisterRequest;
import com.bookexchange.model.User;
import com.bookexchange.repository.UserRepository;
import com.bookexchange.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BadRequestExceptionTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

    @Test
  void findByUsername_ThrowsBadRequestException_WhenUserMissing() {
    assertThatThrownBy(() -> userService.findByUsername("does-not-exist"))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User not found");
    }

    @Test
  void registerUser_ThrowsBadRequestException_WhenUsernameAlreadyExists() {
    userRepository.save(User.builder()
        .username("member")
        .email("member@test.com")
        .password("pw")
        .firstName("Member")
        .lastName("One")
        .dateOfBirth(LocalDate.of(1999, 1, 1))
        .role(com.bookexchange.model.enums.Role.MEMBER)
        .enabled(true)
        .build());

    RegisterRequest request = new RegisterRequest();
    request.setUsername("member");
    request.setEmail("new@test.com");
    request.setPassword("password123");
    request.setFirstName("New");
    request.setLastName("User");
    request.setDateOfBirth(LocalDate.of(2000, 1, 1));

    assertThatThrownBy(() -> userService.registerUser(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Username already exists");
    }

    @Test
  void findByUsername_ReturnsPersistedUser_WhenExists() {
    userRepository.save(User.builder()
        .username("lookup-user")
        .email("lookup-user@test.com")
        .password("pw")
        .firstName("Member")
        .lastName("Two")
        .dateOfBirth(LocalDate.of(1998, 2, 2))
        .role(com.bookexchange.model.enums.Role.MEMBER)
        .enabled(true)
        .build());

    User user = userService.findByUsername("lookup-user");
    assertThat(user.getEmail()).isEqualTo("lookup-user@test.com");
    }
}
