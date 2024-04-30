package com.douzone.surveymanagement.common.security;

import com.douzone.surveymanagement.user.oauth2.dto.CustomOAuth2User;
import com.douzone.surveymanagement.user.oauth2.dto.UserDTO;
import com.douzone.surveymanagement.user.oauth2.handler.CustomSuccessHandler;
import com.douzone.surveymanagement.user.oauth2.service.CustomOAuth2UserService;
import com.douzone.surveymanagement.user.oauth2.util.JWTFilter;
import com.douzone.surveymanagement.user.oauth2.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity(debug = false)
@AllArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomSuccessHandler customSuccessHandler;

    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().configurationSource(corsConfigurationSource());
        http
            .httpBasic().disable()
            .csrf().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http    .addFilterAfter(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .anonymous(anonymousConfigurer -> {
                    UserDTO anonymousUserDTO = new UserDTO(
                            "Anonymous", // 사용자 이름
                            "anonymous@example.com", // 익명 이메일
                            "ANONYMOUS" // 역할
                    );
                    anonymousConfigurer.principal(new CustomOAuth2User(
                            anonymousUserDTO
                    ));
                });

        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );

        http
                .authorizeHttpRequests((auth) -> auth
                        .antMatchers("/api/surveys/weekly","/api/surveys/recent","/api/surveys/closing","/api/surveys/surveyall"
                                ,"/api/surveys/search","/api/survey/resultall/nonMember","/api/surveys/select-closing","/api/surveys/select-post"
                                ,"/api/surveys/details/**","/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated());


        return http.build();
    }

    /**
     * 프로토콜: http, 호스트: localhost, 포트: 3000번에 대해서
     * 모든 메서드(GET, POST, DELETE, PUT etc..)에 대해 CORS를 허용하기 위한 설정입니다.
     *
     * @return corsConfigurationSource
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
            List.of("http://localhost:3000")
        );
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
