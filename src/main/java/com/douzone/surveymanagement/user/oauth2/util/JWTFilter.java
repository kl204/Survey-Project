package com.douzone.surveymanagement.user.oauth2.util;


import com.douzone.surveymanagement.user.oauth2.dto.CustomOAuth2User;
import com.douzone.surveymanagement.user.oauth2.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
        String authorization = null;
        Cookie[] cookies = request.getCookies();

        if(cookies==null) {
            log.info("No Cookies!");
            return;
        }

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("Authorization")) {

                log.info("쿠키 자체는 존재함");
                authorization = cookie.getValue();
            }
        }

        //Authorization 헤더 검증
        if (authorization == null) {
            log.info("No valid JWT token found, treating as anonymous");

            UserDTO anonymousUserDTO = new UserDTO("Anonymous", "anonymous@example.com", "ANONYMOUS");
            CustomOAuth2User anonymousPrincipal = new CustomOAuth2User(anonymousUserDTO);

//            AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
//                    "key", anonymousPrincipal, anonymousPrincipal.getAuthorities());

            Authentication authToken = new UsernamePasswordAuthenticationToken(anonymousPrincipal, null, anonymousPrincipal.getAuthorities());


            SecurityContextHolder.getContext().setAuthentication(authToken);

        } else {

            //토큰 소멸 시간 검증
            if (jwtUtil.isExpired(authorization)) {

                System.out.println("token expired");
                filterChain.doFilter(request, response);

                //조건이 해당되면 메소드 종료 (필수)
                return;
            }

            //토큰에서 username과 role 획득
            String username = jwtUtil.getUsername(authorization);
            String userEmail = jwtUtil.getUserEmail(authorization);
            String role = jwtUtil.getRole(authorization);

            log.info("username : " + username);
            log.info("role : " + role);

            //userDTO를 생성하여 값 set
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setUserEmail(userEmail);
            userDTO.setRole(role);

            //UserDetails에 회원 정보 객체 담기
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

            //스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
            //세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);

        }

        filterChain.doFilter(request, response);


    }

}