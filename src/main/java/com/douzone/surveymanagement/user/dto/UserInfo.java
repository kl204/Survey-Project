package com.douzone.surveymanagement.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * 유저 정보 dto 입니다
 *
 * @author 김선규
 */
@Getter
@Setter
@Schema(description = "사용자 정보 DTO")
public class UserInfo {
    @Schema(description = "사용자 번호", example = "1")
    private long userNo;
    @Schema(description = "사용자 닉네임", example = "nickname123")
    private String userNickname;
    @Schema(description = "사용자 구별 이름", example = "naver sdlkfjslkdfjs")
    private String userName;
    @Schema(description = "사용자 권한", example = "user")
    private String userRole;
    @Schema(description = "사용자 성별", example = "남성")
    private String userGender;
    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String userEmail;
    @Schema(description = "사용자 생년월일", example = "1990-01-01")
    private Date userBirth;
    @Schema(description = "사용자 프로필 이미지 URL", example = "https://example.com/image.jpg")
    private String userImage;

}