//package com.douzone.surveymanagement.user.filter;
//
//import com.douzone.surveymanagement.user.util.CustomAuthentication;
//import com.douzone.surveymanagement.user.util.CustomAuthenticationToken;
//import java.io.IOException;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
///**
// * CustomOAuth2Filter 입니다.
// *
// * @author 김선규
// */
//@Slf4j
//public class CustomOAuth2Filter extends AbstractAuthenticationProcessingFilter {
//
//    public CustomOAuth2Filter(String defaultFilterProcessesUrl) {
//        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
//    }
//
//    @Override
//    public CustomAuthentication attemptAuthentication(HttpServletRequest request,
//                                                      HttpServletResponse response)
//        throws AuthenticationException {
//
//        Authentication authenticationCheck = SecurityContextHolder.getContext().getAuthentication();
//        if (authenticationCheck != null && authenticationCheck.isAuthenticated()) {
//            return (CustomAuthentication) authenticationCheck;
//        } else {
//            String accessToken = extractAccessTokenFromRequest(request);
//            String requestServletPath = request.getServletPath();
//            CustomAuthenticationToken authRequest =
//                new CustomAuthenticationToken(accessToken, null, requestServletPath);
//            CustomAuthentication authentication =
//                (CustomAuthentication) getAuthenticationManager().authenticate(authRequest);
//            if (authentication == null) {
//                return null;
//            }
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            return authentication;
//        }
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request,
//                                            HttpServletResponse response,
//                                            FilterChain chain, Authentication authResult)
//        throws IOException, ServletException {
//        String changedAccessToken = (String) authResult.getCredentials();
//        response.setHeader("Access-Control-Expose-Headers", "Accesstoken");
//        response.setHeader("Accesstoken", "Bearer " + changedAccessToken);
//        chain.doFilter(request, response);
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request,
//                                              HttpServletResponse response,
//                                              AuthenticationException failed) throws IOException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.getWriter().write("Authentication failed");
//
//    }
//
//    private String extractAccessTokenFromRequest(HttpServletRequest request) {
//        String authorizationHeader = request.getHeader("Authorization");
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            return authorizationHeader.substring(7);
//        }
//        return null;
//    }
//
//}
