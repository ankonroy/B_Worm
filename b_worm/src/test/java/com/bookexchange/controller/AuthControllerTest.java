// package com.bookexchange.controller;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.security.test.context.support.WithAnonymousUser;
// import org.springframework.test.web.servlet.MockMvc;
// import com.bookexchange.service.UserService;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// @WebMvcTest(AuthController.class)
// @MockBean(CommandLineRunner.class)  // Add this line
// public class AuthControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private UserService userService;

//     @Test
//     @WithAnonymousUser
//     void testLoginPage_ShouldLoad() throws Exception {
//         mockMvc.perform(get("/auth/login"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("layouts/main"));
//     }

//     @Test
//     @WithAnonymousUser
//     void testRegisterPage_ShouldLoad() throws Exception {
//         mockMvc.perform(get("/auth/register"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("layouts/main"));
//     }
// }