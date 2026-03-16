// package com.bookexchange.controller;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import com.bookexchange.service.BookService;
// import com.bookexchange.service.UserService;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest(BookController.class)
// @MockBean(CommandLineRunner.class)  // Add this line
// public class BookControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private BookService bookService;

//     @MockBean
//     private UserService userService;

//     @Test
//     @WithMockUser
//     void testBooksPage_ShouldBeAccessible() throws Exception {
//         mockMvc.perform(get("/books"))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void testBooksPage_WithoutAuth_ShouldRedirect() throws Exception {
//         mockMvc.perform(get("/books"))
//                 .andExpect(status().is3xxRedirection());
//     }
// }