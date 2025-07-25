package com.spring.spring_init.admin.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class HomeAdminController {

    @GetMapping()
    public String getHome() {
        return "admin/home";
    }
}
