package com.peoject.seoulfestival.userauth.service;

import com.peoject.seoulfestival.exception.CustomException;
import com.peoject.seoulfestival.exception.ErrorCode;
import com.peoject.seoulfestival.security.JwtUtil;
import com.peoject.seoulfestival.security.TokenResponseHandler;
import com.peoject.seoulfestival.userauth.dto.LogoutRequestDto;
import com.peoject.seoulfestival.userauth.dto.ReissueRequestDto;
import com.peoject.seoulfestival.userauth.dto.SignupRequestDto;
import com.peoject.seoulfestival.userauth.entity.BlacklistedToken;
import com.peoject.seoulfestival.userauth.entity.RefreshToken;
import com.peoject.seoulfestival.userauth.entity.User;
import com.peoject.seoulfestival.userauth.repository.BlacklistedTokenRepository;
import com.peoject.seoulfestival.userauth.repository.RefreshTokenRepository;
import com.peoject.seoulfestival.userauth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenResponseHandler tokenResponseHandler;


        @Override
        @Transactional
        public void signup(SignupRequestDto requestDto) {

            verifyEmailIsUnique(requestDto.getEmail());

            String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

            User user = new User(requestDto, encodedPassword);
            userRepository.save(user);
        }


    @Override
    public void reissueToken(ReissueRequestDto requestDto, HttpServletResponse response) {
        String refreshToken = requestDto.getRefreshToken();
        validateToken(refreshToken);

        RefreshToken refreshTokenEntity = findRefreshTokenEntity(refreshToken);
        User user = findUserById(refreshTokenEntity.getUserId());

        tokenResponseHandler.addTokensToResponse(user, response);
        refreshTokenRepository.delete(refreshTokenEntity);
    }


    @Override
    public void logout(LogoutRequestDto requestDto, HttpServletRequest request) {
        String refreshToken = requestDto.getRefreshToken();
        validateToken(refreshToken);

        RefreshToken refreshTokenEntity = findRefreshTokenEntity(refreshToken);
        refreshTokenRepository.delete(refreshTokenEntity);

        String accessToken = jwtUtil.extractTokenFromRequest(request);
        blacklistToken(accessToken);
    }


    private void validateToken(String token) {
        if (!jwtUtil.isValidToken(token)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    private RefreshToken findRefreshTokenEntity(String refreshToken) {
        return refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    private void verifyEmailIsUnique(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void blacklistToken(String token) {
        long expirationTime = jwtUtil.getRemainingTime(token);
        blacklistedTokenRepository.save(new BlacklistedToken(token, expirationTime / 1000));
    }
}
