package com.douzone.surveymanagement.survey.controller;

import com.douzone.surveymanagement.common.response.CommonResponse;
import com.douzone.surveymanagement.survey.dto.response.SurveyDetailInfoDto;
import com.douzone.surveymanagement.survey.dto.response.SurveyDetailsDto;
import com.douzone.surveymanagement.survey.service.QuerySurveyService;
import com.douzone.surveymanagement.user.dto.UserInfo;
import com.douzone.surveymanagement.user.oauth2.dto.CustomOAuth2User;
import com.douzone.surveymanagement.user.oauth2.mapper.OAuth2UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 설문에 대해 조회를 담당하는 역할을 클래스 입니다.
 *
 * @author : 강명관
 * @since : 1.0
 **/
@Slf4j
@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class QuerySurveyController {

    private final QuerySurveyService querySurveyService;
    private final OAuth2UserMapper userMapper;


    /**
     * 설문의 수정에 필요한 설문의 모든 정보를 가져오는 API 입니다.
     *
     * @param surveyNo 설문 번호
     * @return {@link SurveyDetailsDto}
     * @author : 강명관
     */
    @Operation(summary = "설문에 대한 모든 상세 정보 조회", description = "특정 설문의 상세 정보를 조회합니다.")
    @GetMapping("/{surveyNo}")
    public ResponseEntity<CommonResponse<SurveyDetailsDto>> surveyDetails(
        @PathVariable(value = "surveyNo") long surveyNo
    ) {
        SurveyDetailsDto surveyDetails = querySurveyService.findSurveyDetails(surveyNo);
        return ResponseEntity.ok(CommonResponse.successOf(surveyDetails));
    }

    /**
     * 이번 주 내에 등록된 설문 중 참여자가 많은 설문 10개를 가져오는 API입니다.
     *
     * @return 인기 설문 10개
     */
    @Operation(
        summary = "이번 주 인기 설문 조회",
        description = "이번 주에 참여자가 많은 인기 설문 10개를 조회합니다."
    )
    @GetMapping("/weekly")
    public ResponseEntity<List<SurveyDetailInfoDto>> weeklySurveyList(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User

    ) {
        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());

        long userNo = userInfo.map(UserInfo::getUserNo).orElse(0L); // 기본값으로 0을 사용
        List<SurveyDetailInfoDto> weeklySurvey = querySurveyService.readWeeklySurvey(userNo);
            return ResponseEntity.ok(weeklySurvey);
    }

    /**
     * 최근 등록된 설문 10개를 가져오는 API 입니다.
     *
     * @return 최근 등록 설문 10개
     */
    @Operation(summary = "최근 등록된 설문 조회", description = "최근에 등록된 설문 10개를 조회합니다.")
    @GetMapping("/recent")
    public ResponseEntity<List<SurveyDetailInfoDto>> recentSurveyList(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {

        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());

        long userNo = userInfo.map(UserInfo::getUserNo).orElse(0L); // 기본값으로 0을 사용
        List<SurveyDetailInfoDto> recentSurvey = querySurveyService.readRecentSurvey(userNo);
        return ResponseEntity.ok(recentSurvey);
    }

    /**
     * 최근에 마감된 순서로 설문 10개를 가져오는 API입니다.
     *
     * @return 최근 마감 설문 10개
     */
    @Operation(summary = "최근 마감된 설문 조회", description = "최근에 마감된 순서대로 설문 10개를 조회합니다.")
    @GetMapping("/closing")
    public ResponseEntity<List<SurveyDetailInfoDto>> closingSurveyList(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {
        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());

        long userNo = userInfo.map(UserInfo::getUserNo).orElse(0L); // 기본값으로 0을 사용
        List<SurveyDetailInfoDto> closingSurvey = querySurveyService.readClosingSurvey(userNo);
        return ResponseEntity.ok(closingSurvey);
    }

    /**
     * 게시, 마감된 설문 전체를 가져오는 API 입니다.
     *
     * @param page
     * @return 전체 설문
     */
    @Operation(
        summary = "게시 및 마감된 설문 전체 조회",
        description = "게시되거나 마감된 모든 설문을 페이지별로 조회합니다."
    )
    @GetMapping("/surveyall")
    public ResponseEntity<List<SurveyDetailInfoDto>> getAllSurvey(
        @RequestParam("page") int page,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {
        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());

        long userNo = userInfo.map(UserInfo::getUserNo).orElse(0L); // 기본값으로 0을 사용
        List<SurveyDetailInfoDto> allSurvey = querySurveyService.getSurveyAll(page, userNo);
        return ResponseEntity.ok(allSurvey);
    }

    /**
     * 검색에서 마감을 선택할 시 마감된 설문을 20개씩 끊어 가져오는 API입니다.
     *
     * @param page
     * @return 마감 설문 20개
     */
    @GetMapping("/select-closing")
    @Operation(summary = "마감된 설문 페이징 조회", description = "마감된 설문을 페이지별로 20개씩 조회합니다.")
    public ResponseEntity<List<SurveyDetailInfoDto>> selectClosingSurveyList(
        @RequestParam("page") int page,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {
        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());

        long userNo = userInfo.map(UserInfo::getUserNo).orElse(0L); // 기본값으로 0을 사용
        List<SurveyDetailInfoDto> closeSurvey = querySurveyService.selectClosing(page, userNo);
        return ResponseEntity.ok(closeSurvey);
    }

    /**
     * 검색에서 진행을 선택 시 진행중인 설문을 20개씩 가져오는 API입니다.
     *
     * @param page
     * @return 진행중인 설문 20개
     */
    @GetMapping("/select-post")
    @Operation(summary = "진행중인 설문 페이징 조회", description = "진행 중인 설문을 페이지별로 20개씩 조회합니다.")
    public ResponseEntity<List<SurveyDetailInfoDto>> selectPostSurveyList(
        @RequestParam("page") int page,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {
        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());

        long userNo = userInfo.map(UserInfo::getUserNo).orElse(0L); // 기본값으로 0을 사용
        List<SurveyDetailInfoDto> postSurvey = querySurveyService.selectPost(page, userNo);
        return ResponseEntity.ok(postSurvey);
    }

    @GetMapping("/search")
    @Operation(summary = "키워드로 설문 검색", description = "주어진 키워드로 설문을 검색합니다.")
    public ResponseEntity<List<SurveyDetailInfoDto>> findSurveyByKeyword(
        @RequestParam("searchWord") String searchWord,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {
        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());

        long userNo = userInfo.map(UserInfo::getUserNo).orElse(0L); // 기본값으로 0을 사용
        List<SurveyDetailInfoDto> findSurvey =
            querySurveyService.searchSurveyByKeyword(searchWord, userNo);
        return ResponseEntity.ok(findSurvey);
    }

    /**
     * 특정 설문에 대해 메인 페이지에 필요한 모든 정보를 조회하는 API 입니다.
     *
     * @param surveyNo          설문 번호
     * @return {@link SurveyDetailInfoDto}
     * @author : 강명관
     */
    @Operation(
        summary = "특정 설문에 대해 메인 페이지에 필요한 모든 정보",
        description = "특정 설문에 대해 메인 페이지에서 필요한 모든 정보를 조회합니다."
    )
    @GetMapping("/details/{surveyNo}")
    public ResponseEntity<CommonResponse> surveyFindOneDetails(
        @PathVariable("surveyNo") long surveyNo,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {
        Optional<UserInfo> userInfo = userMapper.findByUserEmail(customOAuth2User.getUserEmail());

        SurveyDetailInfoDto surveyDetails =
            querySurveyService.findOneSurveyDetailsBySurveyNo(
                    userInfo.map(UserInfo::getUserNo).orElse(0L),
                surveyNo
            );

        return ResponseEntity.ok(CommonResponse.successOf(surveyDetails));
    }


}
