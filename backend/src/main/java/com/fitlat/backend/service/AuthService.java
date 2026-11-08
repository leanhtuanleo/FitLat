
package com.fitlat.backend.service;

import com.fitlat.backend.dto.LoginRequest;
import com.fitlat.backend.dto.LoginResponse;
import com.fitlat.backend.dto.UserResponse;
import com.fitlat.backend.dto.RegisterRequest;

import com.fitlat.backend.entity.User;
import com.fitlat.backend.entity.Role;

import com.fitlat.backend.mapper.UserMapper;
import com.fitlat.backend.repository.UserRepository;
import com.fitlat.backend.security.JwtService;
import com.fitlat.backend.exception.EmailAlreadyExistsException;

import com.fitlat.backend.dto.RefreshTokenRequest;
import com.fitlat.backend.dto.RefreshTokenResponse;
import com.fitlat.backend.entity.RefreshToken;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private final long accessTokenExpiration;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            @Value("${app.jwt.access-token-expiration}")
            long accessTokenExpiration
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.accessTokenExpiration = accessTokenExpiration;
    }

    private boolean isEmailUniqueViolation(Throwable ex) {
        Throwable cause = ex;

        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
                return "users_email_key".equals(cve.getConstraintName());
            }

            cause = cause.getCause();
        }

        return false;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (user.getPasswordHash() == null) {
            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        // Tạo Access Token
        String accessToken = jwtService.generateAccessToken(user);

        // Tạo Refresh Token và lưu hash vào database
        String refreshToken = refreshTokenService.createRefreshToken(user);

        UserResponse userResponse = UserMapper.toResponse(user);

        return new LoginResponse(
                userResponse,
                accessToken,
                refreshToken,
                "Bearer",
                accessTokenExpiration / 1000
        );
    }

    @Transactional
    public User register(RegisterRequest request) {

        String email = request.getEmail().trim();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already in use"
            );
        }

        User user = new User();

        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.MEMBER);

        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            if (isEmailUniqueViolation(ex)) {
                throw new EmailAlreadyExistsException(
                        "Email is already in use"
                );
            }

            throw ex;
        }
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
    
        // Xác minh và xoay vòng Refresh Token
        String newRefreshToken = refreshTokenService.rotateRefreshToken(
                request.getRefreshToken()
        );
    
        // Lấy User từ Refresh Token mới
        RefreshToken newToken = refreshTokenService.verifyRefreshToken(
                newRefreshToken
        );
    
        User user = newToken.getUser();
    
        // Tạo Access Token mới
        String newAccessToken = jwtService.generateAccessToken(user);
    
        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                accessTokenExpiration / 1000
        );
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(
                request.getRefreshToken()
        );
    }
}
