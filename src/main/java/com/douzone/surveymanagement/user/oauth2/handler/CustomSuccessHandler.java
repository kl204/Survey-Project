package com.douzone.surveymanagement.user.oauth2.handler;

import com.douzone.surveymanagement.user.dto.UserInfo;
import com.douzone.surveymanagement.user.oauth2.dto.CustomOAuth2User;
import com.douzone.surveymanagement.user.oauth2.mapper.OAuth2UserMapper;
import com.douzone.surveymanagement.user.oauth2.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@AllArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final OAuth2UserMapper oAuth2UserMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String userEmail = customUserDetails.getUserEmail();
        String username = customUserDetails.getUsername();
        Optional<UserInfo> userInfo = oAuth2UserMapper.findByUserEmail(userEmail);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, userEmail, role, 60*60*60L);
        String loginSession = "true";

        System.out.println(userInfo.map(UserInfo::getUserNickname).orElse("Anonymous_user"));

        response.addCookie(createCookie("Authorization", token, true));
        response.addCookie(createCookie("loginSession",loginSession , false));
        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value, boolean httpOnly) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);

        return cookie;
    }
}