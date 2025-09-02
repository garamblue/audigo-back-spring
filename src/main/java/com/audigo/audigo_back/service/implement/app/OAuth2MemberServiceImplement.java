package com.audigo.audigo_back.service.implement.app;

import java.util.*;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.audigo.audigo_back.entity.CustomOAuth2Member;
import com.audigo.audigo_back.repository.app.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2MemberServiceImplement extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;//DI

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        String oAuthClientName = userRequest.getClientRegistration().getClientName();

        String id = "";
        String email = "";
        String nickname = "";
        var authorities = List.of(new SimpleGrantedAuthority("GENERAL_USER"));// 권한(필요 시 ROLE_USER 등 부여)
        Map<String, Object> mapped = new HashMap<>();

        if (oAuthClientName.equals("kakao")) {
            // Kakao 응답 구조 파싱
            id = (attributes.get("id")).toString();//(Number) .longValue()

            @SuppressWarnings("unchecked")
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.getOrDefault("kakao_account", Collections.emptyMap());
            email = (String) kakaoAccount.get("email") == null ? "" : (String) kakaoAccount.get("email");

            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.getOrDefault("profile", Collections.emptyMap());
            nickname = (String) profile.get("nickname");
            //String profileImage = (String) profile.get("profile_image_url");

            log.info("★ ★ ★ ======================================");
            log.info("=== kakaoAccount: " + kakaoAccount.toString());
            log.info("=== profile: " + profile.toString());
            log.info("★ ★ ★ ======================================");
            // 우리 서비스에서 쓰기 좋은 형태로 가공한 Map mapped.put("", "");
            mapped.put("provider", "kakao");
            mapped.put("id", id);
            mapped.put("status", "1");
            mapped.put("email", (email == "" || email == null || email.isEmpty()) ? nickname : email);
            mapped.put("nickname", nickname);
            mapped.put("birth_dt", "20250829");
            mapped.put("sns_div", "5");
            mapped.put("os_vers", "android 10");
            mapped.put("os_name", "android");
            mapped.put("mobile_numb", "00000000000");
            mapped.put("sns_val", "kakao_" + id);
            mapped.put("sns_id", id);
            
            // PostgreSQL FUNCTION 호출 및 결과 반환
            // snsDiv => 1:일반, 2:구글, 3:애플, 4:페이스북, 5:카카오톡
            Map<String, Object> result = memberRepository.registerMember(
                email,
                nickname,
                "20250829",
                "",
                "5",
                "",
                "",
                "N",
                "push_token",
                "kakao_" + id,
                "",
                "1.0.0",
                "Android 10",
                "Android",
                "",
                "kr",
                "00000000000",
                "",
                "Y",
                id
            );
            log.info("★ ★ ★ ======================================");
            log.info("=== registerMember mapped: " + mapped.toString());
            log.info("★ ★ ★ ======================================");
        }

        if (oAuthClientName.equals("naver")) {
            @SuppressWarnings("unchecked")
            Map<String, String> responseMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
            id = "naver_" + responseMap.get("id").substring(0, 14);
            email = responseMap.get("email");
        }
        
        // CustomOAuth2Member 반환
        return new CustomOAuth2Member(id, mapped, authorities);
    }
}