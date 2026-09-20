package com.edid.edid_back.service.implement.app;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.edid.edid_back.dto.response.ResponseDto;
import com.edid.edid_back.dto.response.app.user.GetSignInUserResponseDto;
import com.edid.edid_back.entity.UserEntity;
import com.edid.edid_back.repository.app.UserRepository;
import com.edid.edid_back.service.app.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ResponseEntity<? super GetSignInUserResponseDto> getSignInUser(String email) {

        UserEntity userEntity = null;

        try {
            userEntity = userRepository.findByEmail(email);

            if (userEntity == null)
                return GetSignInUserResponseDto.noMatchedUser();

            log.info("=== UserServiceImpl findByEmail result : " + userEntity.toString());

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetSignInUserResponseDto.success(userEntity);
    }

}