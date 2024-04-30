package com.douzone.surveymanagement.user.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
@Slf4j
public class LogIOController {

    @GetMapping("/api/oauth2/logout")
    public void Logout(HttpServletResponse response) throws IOException {

        log.info("로그아웃됨");

        response.addCookie(createCookie("Authorization", null));
        response.addCookie(createCookie("loginSession", null));

        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // 쿠키의 최대 수명을 0초로 설정
        return cookie;
    }


}
