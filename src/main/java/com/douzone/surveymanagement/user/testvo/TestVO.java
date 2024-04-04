package com.douzone.surveymanagement.user.testvo;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
public class TestVO {
    private final long userNo;
    private final String userNickname;
    private final String userGender;
    private final String userEmail;
    private final Date userBirth;
    private final String userImage;

    public TestVO(){
        this.userNo= 1;
        this.userNickname = "admin";
        this.userGender= "M";
        this.userEmail= "kl204@naver.com";
        this.userBirth= new Date();
        this.userImage="";
    }

}