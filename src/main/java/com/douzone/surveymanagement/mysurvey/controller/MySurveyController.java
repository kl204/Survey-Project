package com.douzone.surveymanagement.mysurvey.controller;

import com.douzone.surveymanagement.common.response.CommonResponse;
import com.douzone.surveymanagement.common.response.ErrorResponse;
import com.douzone.surveymanagement.mysurvey.dto.request.MySurveyDTO;
import com.douzone.surveymanagement.mysurvey.service.impl.MySurveyServiceImpl;
import com.douzone.surveymanagement.user.dto.UserInfo;
import com.douzone.surveymanagement.user.oauth2.dto.CustomOAuth2User;
import com.douzone.surveymanagement.user.oauth2.mapper.OAuth2UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MyPage Survey API 컨트롤러 클래스입니다.
 *
 * <p>마이페이지 설문목록 관련 API 엔드포인트를 처리합니다.</p>
 *
 * @version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/api/my-surveys")
@RequiredArgsConstructor
public class MySurveyController {

    private final MySurveyServiceImpl mySurveyServiceImpl;
    private final OAuth2UserMapper userMapper;

    /**
     * 사용자가 작성한 설문 목록을 가져옵니다.
     *
     * @return 설문 목록과 상태 정보를 포함한 응답
     */
    @Operation(
        summary = "사용자가 작성한 설문 목록 가져오기",
        description = "로그인한 사용자가 작성한 설문 목록을 가져옵니다."
    )
    @GetMapping("/write-surveys")
    public ResponseEntity<CommonResponse<List<MySurveyDTO>>> selectMySurvey(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
       ) {
        if (customOAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());
        List<MySurveyDTO> myWriteSurveys =
            mySurveyServiceImpl.selectMySurveysWithSorting(userInfo.map(UserInfo::getUserNo).orElse(0L));

        return ResponseEntity.ok(CommonResponse.successOf(myWriteSurveys));
    }


    /**
     * 사용자가 참여한 설문 목록을 가져옵니다.
     *
     * @return 설문 목록과 상태 정보를 포함한 응답
     */
    @Operation(
        summary = "사용자가 참여한 설문 목록 가져오기",
        description = "로그인한 사용자가 참여한 설문 목록을 가져옵니다."
    )
    @GetMapping("/attend-surveys")
    public ResponseEntity<CommonResponse<List<MySurveyDTO>>> selectAttendSurvey(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
        ) {
        if (customOAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());
        List<MySurveyDTO> myAttendSurveys =
            mySurveyServiceImpl.selectMyParticipatedSurveys(userInfo.map(UserInfo::getUserNo).orElse(0L));

        return ResponseEntity.ok(CommonResponse.successOf(myAttendSurveys));
    }

    @Operation(
        summary = "사용자가 작성한 설문 수정하기",
        description = "로그인한 사용자가 작성한 특정 설문을 수정합니다."
    )
    @PutMapping("/update-write-surveys")
    public ResponseEntity<CommonResponse> updateMySurvey(
        @RequestBody MySurveyDTO mySurveyDTO,
                    @AuthenticationPrincipal CustomOAuth2User customOAuth2User

    ) {
        if (customOAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());

        mySurveyDTO.setUserNo(userInfo.map(UserInfo::getUserNo).orElse(0L));
        boolean isDeleted = mySurveyServiceImpl.deleteMySurveyInProgress(mySurveyDTO);
        if (isDeleted) {
            return ResponseEntity.ok(CommonResponse.successOf("Survey deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(ErrorResponse.of("Failed to delete survey")));
        }
    }
}