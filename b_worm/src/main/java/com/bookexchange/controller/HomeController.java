package com.bookexchange.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String welcome(Model model) {
        model.addAttribute("content", "welcome");
        return "layouts/main";
    }

    @GetMapping("/feed")
    public String feed(Model model) {
        model.addAttribute("content", "feed");
        return "layouts/main";
    }
}