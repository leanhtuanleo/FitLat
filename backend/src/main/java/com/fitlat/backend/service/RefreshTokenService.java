package com.fitlat.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitlat.backend.entity.RefreshToken;
import com.fitlat.backend.entity.User;
import com.fitlat.backend.repository.RefreshTokenRepository;

import com.fitlat.backend.exception.InvalidRefreshTokenException;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    private final long refreshTokenExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.jwt.refresh-token-expiration}")
            long refreshTokenExpiration
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Transactional
    public String createRefreshToken(User user) {

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusSeconds(
                        refreshTokenExpiration / 1000
                )
        );
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    public RefreshToken verifyRefreshToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (refreshToken.isRevoked()) {
                throw new InvalidRefreshTokenException();
        }

        if (!refreshToken.getExpiresAt().isAfter(LocalDateTime.now())) {
                throw new InvalidRefreshTokenException();
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {

        RefreshToken refreshToken = verifyRefreshToken(rawToken);

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    private String hashToken(String rawToken) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getEncoder()
                    .encodeToString(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable", e
            );
        }
    }

    @Transactional
    public String rotateRefreshToken(String rawToken) {

        // Xác minh Refresh Token cũ
        RefreshToken oldToken = verifyRefreshToken(rawToken);

        // Thu hồi token cũ
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        // Cấp token mới cho cùng User
        return createRefreshToken(oldToken.getUser());
    }
}