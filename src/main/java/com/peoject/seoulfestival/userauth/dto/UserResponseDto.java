package com.peoject.seoulfestival.userauth.dto;


import com.peoject.seoulfestival.userauth.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long id;
    private String nickname;


    public UserResponseDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
    }
}
