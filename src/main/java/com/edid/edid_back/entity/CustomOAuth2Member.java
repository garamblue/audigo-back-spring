package com.audigo.audigo_back.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CustomOAuth2Member implements OAuth2User{

    private String userId;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2Member(String userId) {
        this.userId = userId;
        this.attributes = Collections.emptyMap();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("GENERAL_USER"));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes != null ? this.attributes : Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities != null ? this.authorities : Collections.singletonList(new SimpleGrantedAuthority("GENERAL_USER"));
    }

    @Override
    public String getName() {
        return this.userId;
    }

    // public String getEmail() {
    //     return this.email;
    // }

    // public String getProvider() {
    //     return this.provider;
    // }
    
}
