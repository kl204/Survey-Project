package com.douzone.surveymanagement.user.oauth2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String role;
    private String name;
    private String username;
    private String userEmail;

    public UserDTO() {
    }

    public UserDTO(String anonymous, String mail, String anonymous1) {
        this.role = anonymous;
        this.userEmail = mail;
        this.username = anonymous1;
    }
}
