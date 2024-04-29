package com.douzone.surveymanagement.user.oauth2.mapper;


import com.douzone.surveymanagement.user.dto.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2UserMapper {
    UserInfo findByUserEmail(String userEmail);

    void registUser(UserInfo userInfo);

}