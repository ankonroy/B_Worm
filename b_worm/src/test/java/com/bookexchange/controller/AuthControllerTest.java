package com.bookexchange.controller;

import com.bookexchange.model.User;
import com.bookexchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Test
	@WithAnonymousUser
	void testLoginPage_ShouldLoad() throws Exception {
		mockMvc.perform(get("/auth/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/login"));
	}

	@Test
	@WithAnonymousUser
	void testRegisterPage_ShouldLoad() throws Exception {
		mockMvc.perform(get("/auth/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/register"))
				.andExpect(model().attributeExists("registerRequest"));
	}

	@Test
	@WithAnonymousUser
	void testRegister_ShouldRedirectToLogin_WhenValid() throws Exception {
		userRepository.deleteAll();

		mockMvc.perform(post("/auth/register")
						.param("username", "newuser")
						.param("email", "newuser@test.com")
						.param("password", "password123")
						.param("firstName", "New")
						.param("lastName", "User")
						.param("dateOfBirth", "2000-01-01"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/auth/login"));

		assertThat(userRepository.findByUsername("newuser")).isPresent();
	}

	@Test
	@WithAnonymousUser
	void testRegister_ShouldReturnRegisterPage_WhenServiceThrows() throws Exception {
		userRepository.deleteAll();
		User existing = User.builder()
				.username("newuser")
				.email("existing@test.com")
				.password("pw")
				.firstName("Existing")
				.lastName("User")
				.dateOfBirth(LocalDate.of(1990, 1, 1))
				.role(com.bookexchange.model.enums.Role.MEMBER)
				.enabled(true)
				.build();
		userRepository.save(existing);

		mockMvc.perform(post("/auth/register")
						.param("username", "newuser")
						.param("email", "newuser2@test.com")
						.param("password", "password123")
						.param("firstName", "New")
						.param("lastName", "User")
						.param("dateOfBirth", "2000-01-01"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/register"))
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attributeExists("registerRequest"));
	}
}
