package com.bookexchange.controller;

import com.bookexchange.model.User;
import com.bookexchange.model.enums.Role;
import com.bookexchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
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
class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Test
	@WithMockUser(roles = "ADMIN")
	void testAdminAccess_WithAdminRole_ShouldBeOk() throws Exception {
		mockMvc.perform(get("/admin/dashboard"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/dashboard"));
	}

	@Test
	@WithMockUser(roles = "MEMBER")
	void testAdminAccess_WithMemberRole_ShouldBeForbidden() throws Exception {
		mockMvc.perform(get("/admin/dashboard"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testUsersPage_ShouldLoadModel() throws Exception {
		userRepository.save(User.builder()
				.username("member-a")
				.email("member-a@test.com")
				.password("pw")
				.firstName("Member")
				.lastName("A")
				.dateOfBirth(LocalDate.of(1992, 1, 1))
				.role(Role.MEMBER)
				.enabled(true)
				.build());

		mockMvc.perform(get("/admin/users"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/users"))
				.andExpect(model().attributeExists("users"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDisableUser_ShouldRedirect() throws Exception {
		User user = userRepository.save(User.builder()
				.username("member-disable")
				.email("member-disable@test.com")
				.password("pw")
				.firstName("Member")
				.lastName("Disable")
				.dateOfBirth(LocalDate.of(1993, 1, 1))
				.role(Role.MEMBER)
				.enabled(true)
				.build());

		mockMvc.perform(post("/admin/users/" + user.getUserId() + "/disable"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/users"));

		User updated = userRepository.findById(user.getUserId()).orElseThrow();
		assertThat(updated.isEnabled()).isFalse();
		assertThat(updated.getDisabledAt()).isNotNull();
	}
}
