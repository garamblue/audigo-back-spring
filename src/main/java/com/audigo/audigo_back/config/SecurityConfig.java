package com.audigo.audigo_back.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.audigo.audigo_back.handler.OAuth2SuccessHandler;
import com.audigo.audigo_back.jwt.JWTAuthFilter;
import com.audigo.audigo_back.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    // DI for addFilterBefore
    private final JWTAuthFilter jwtAuthFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // public SecurityConfig(JWTAuthFilter jwtAuthFilter, OAuth2UserService oAuth2UserService) {
    //     this.jwtAuthFilter = jwtAuthFilter;
    //     this.oAuth2UserService = oAuth2UserService;
    // }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS set first
        http.cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration corsConfig = new CorsConfiguration();

                corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                corsConfig.setAllowedMethods(Collections.singletonList("*"));
                corsConfig.setAllowCredentials(true);
                corsConfig.setAllowedHeaders(Collections.singletonList("*"));
                corsConfig.setMaxAge(3600L);
                corsConfig.setExposedHeaders(Collections.singletonList("Authorization"));

                return corsConfig;
            }
        }));
        http.csrf((auth) -> auth.disable());

        http.formLogin((auth) -> auth.disable());

        http.httpBasic((auth) -> auth.disable());

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/main", "/api/v1/board/**", "/api/v1/auth/**", "/api/v1/user/**", "/api/v1/push/**",
                        "/api/adm/auth/**", "/file/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/oauth2/**")
                .permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/auth/oauth2"))
                .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
        );
        //http.oauth2Login(Customizer.withDefaults());
        
        // JWT 인증 필터 추가 (기존 필터와 새 필터 모두 사용)
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));// 제일 중요한 부분!

        return http.build();
    }
}