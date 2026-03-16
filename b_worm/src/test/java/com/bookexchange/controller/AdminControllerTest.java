// package com.bookexchange.controller;

// import com.bookexchange.model.User;
// import com.bookexchange.service.BookService;
// import com.bookexchange.service.UserService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest(AdminController.class)
// @MockBean(CommandLineRunner.class)  // Add this line
// public class AdminControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private UserService userService;

//     @MockBean
//     private BookService bookService;

//     @Test
//     @WithMockUser(roles = "ADMIN")
//     void testAdminAccess_WithAdminRole_ShouldBeOk() throws Exception {
//         mockMvc.perform(get("/admin/dashboard"))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @WithMockUser(roles = "MEMBER")
//     void testAdminAccess_WithMemberRole_ShouldBeForbidden() throws Exception {
//         mockMvc.perform(get("/admin/dashboard"))
//                 .andExpect(status().isForbidden());
//     }
// }