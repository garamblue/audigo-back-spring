package com.audigo.audigo_back.entity;

import com.audigo.audigo_back.dto.request.app.auth.SignUpRequestDto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user")
@Table(name = "user")
public class UserEntity {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long userId;

    @Id
    private String email;
    private String password;
    private String nickname;
    private String telNumber;
    private String address;
    private String addressDetail;
    private String profileImage;
    private boolean agreedPersonal;

    public UserEntity(SignUpRequestDto dto) {
        this.email = dto.getEmail();
        this.password = "pass"; //dto.getPassword();
        this.nickname = dto.getNickname();
        this.telNumber = "telNumber"; //dto.getTelNumber();
        this.address = "address"; //dto.getAddress();
        this.addressDetail = "addressDetail"; //dto.getAddressDetail();
        this.agreedPersonal = true; //dto.getAgreedPersonal();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "[email: " + email + "]"
                + "[password: " + password + "]"
                + "[nickname: " + nickname + "]"
                + "[telNumber: " + telNumber + "]"
                + "[address: " + address + "]"
                + "[addressDetail: " + addressDetail + "]";
    }
}