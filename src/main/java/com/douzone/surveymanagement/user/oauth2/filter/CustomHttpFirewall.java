package com.douzone.surveymanagement.user.oauth2.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class CustomHttpFirewall extends StrictHttpFirewall {
    @Override
    public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {

            log.info("getFirewalledRequest 실행");
            log.info("어디서 실행된 것인지 : " + request.toString());

            FirewalledRequest firewalledRequest = super.getFirewalledRequest(request);

            log.info("firewalledRequest : " + firewalledRequest.toString());

            return firewalledRequest;

    }

    @Override
    public HttpServletResponse getFirewalledResponse(HttpServletResponse response) {
        log.info("getFirewalledResponse 실행");

        HttpServletResponse httpServletResponse = super.getFirewalledResponse(response);

        log.info("httpServeletResponse : " + httpServletResponse.getStatus());

        int responseFail = httpServletResponse.getStatus();

        if(responseFail == 500){
            log.info("서버 실패로 쿠키 초기화");
            response.addCookie(createCookie("Authorization", null));
            response.addCookie(createCookie("loginSession", null));
            return response;
        }

        return httpServletResponse;
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(0);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
