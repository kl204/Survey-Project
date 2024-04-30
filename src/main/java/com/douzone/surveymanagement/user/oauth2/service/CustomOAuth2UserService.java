package com.douzone.surveymanagement.user.oauth2.service;

import com.douzone.surveymanagement.user.dto.UserInfo;
import com.douzone.surveymanagement.user.oauth2.dto.*;
import com.douzone.surveymanagement.user.oauth2.mapper.OAuth2UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("oAuth2User : " + oAuth2User.toString());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        log.info("check registrationId : " + registrationId);

        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }
        String userEmail = oAuth2Response.getEmail();

        log.info("네이버 서버 이메일 : " + userEmail);
        Optional<UserInfo> existData = userMapper.findByUserEmail(userEmail);

        if (existData.isEmpty()) {
            log.info("회원이 존재하지 않음");
            //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
            String userName = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(userName);
            userInfo.setUserEmail(oAuth2Response.getEmail());
            userInfo.setUserNickname(oAuth2Response.getName());
            userInfo.setUserRole("ROLE_USER");

            userMapper.registUser(userInfo);

            Optional<UserInfo> checkUser = userMapper.findByUserEmail(userEmail);

            String userCheck = checkUser.map(UserInfo::getUserNickname).orElse("Anonymous member");
            log.info("제대로 들어갔는지 확인 : "+ userCheck);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(userName);
            userDTO.setUserEmail(userEmail);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        }
        else {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(oAuth2Response.getName());
            userDTO.setUserEmail(oAuth2Response.getEmail());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.map(UserInfo::getUserRole).orElse("Anonymous_user"));

            return new CustomOAuth2User(userDTO);
        }
    }
}
