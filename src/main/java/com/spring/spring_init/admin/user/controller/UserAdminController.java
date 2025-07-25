package com.spring.spring_init.admin.user.controller;

import com.spring.spring_init.admin.user.dto.response.UserAdminInfo;
import com.spring.spring_init.admin.user.service.UserAdminService;
import com.spring.spring_init.common.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    //로그인 페이지 이동
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    //회원 관리 > 회원 정보 페이지 이동
    @GetMapping("/users")
    public String userPage(
        Model model
    ) {
        PageResponseDTO<UserAdminInfo> userInfoList =
            userAdminService.getAllUser();

        model.addAttribute("data", userInfoList);
        return "admin/users";
    }
}
