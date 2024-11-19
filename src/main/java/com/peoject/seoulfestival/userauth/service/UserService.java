package com.peoject.seoulfestival.userauth.service;

import com.peoject.seoulfestival.userauth.dto.LogoutRequestDto;
import com.peoject.seoulfestival.userauth.dto.ReissueRequestDto;
import com.peoject.seoulfestival.userauth.dto.SignupRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UserService {
    @Transactional
    void signup(SignupRequestDto requestDto);

    void reissueToken(ReissueRequestDto requestDto, HttpServletResponse response);

    void logout(LogoutRequestDto requestDto, HttpServletRequest request);

}
