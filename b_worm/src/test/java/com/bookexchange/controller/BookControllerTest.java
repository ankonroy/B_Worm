package com.bookexchange.controller;

import com.bookexchange.model.Book;
import com.bookexchange.model.User;
import com.bookexchange.model.enums.BookCondition;
import com.bookexchange.model.enums.BookStatus;
import com.bookexchange.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.RequestRepository;
import com.bookexchange.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private RequestRepository requestRepository;

	private User member;
	private Book requestableBook;

	@BeforeEach
	void setUp() {
		requestRepository.deleteAll();
		bookRepository.deleteAll();
		userRepository.deleteAll();

		member = userRepository.save(User.builder()
				.username("member")
				.email("member@test.com")
				.password("pw")
				.firstName("Member")
				.lastName("One")
				.dateOfBirth(LocalDate.of(1999, 1, 1))
				.role(Role.MEMBER)
				.enabled(true)
				.build());

		User donor = userRepository.save(User.builder()
				.username("donor")
				.email("donor@test.com")
				.password("pw")
				.firstName("Donor")
				.lastName("User")
				.dateOfBirth(LocalDate.of(1995, 1, 1))
				.role(Role.MEMBER)
				.enabled(true)
				.build());

		requestableBook = bookRepository.save(Book.builder()
				.title("Existing Book")
				.author("Author")
				.isbn("ISBN-1")
				.description("Desc")
				.condition(BookCondition.GOOD)
				.status(BookStatus.AVAILABLE)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.donatedBy(donor)
				.build());
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void testBooksPage_ShouldBeAccessible() throws Exception {
		mockMvc.perform(get("/books"))
				.andExpect(status().isOk())
				.andExpect(view().name("layouts/main"))
				.andExpect(model().attributeExists("books"))
				.andExpect(model().attribute("content", "books/list"));
	}

	@Test
	void testBooksPage_WithoutAuth_ShouldRedirect() throws Exception {
		mockMvc.perform(get("/books"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("**/auth/login"));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void testSearchBooks_ShouldCallServiceWithQuery() throws Exception {
		mockMvc.perform(get("/books/search").param("q", "gatsby"))
				.andExpect(status().isOk())
				.andExpect(view().name("layouts/main"));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void testDonateBook_ShouldRedirectToBooks_WhenAuthenticated() throws Exception {
		long before = bookRepository.count();

		mockMvc.perform(post("/books/donate").param("title", "A title"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/books"));

		assertThat(bookRepository.count()).isEqualTo(before + 1);
		assertThat(bookRepository.findAllByDonatedBy(member)).isNotEmpty();
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void testRequestBook_ShouldRedirectToBookDetails() throws Exception {
		mockMvc.perform(post("/books/" + requestableBook.getBookId() + "/request"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/books/" + requestableBook.getBookId()));

		assertThat(requestRepository.findAllByBook(requestableBook)).hasSize(1);
	}
}
