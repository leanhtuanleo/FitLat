
package com.fitlat.backend.controller;

import com.fitlat.backend.dto.profile.UpdateUserProfileRequest;
import com.fitlat.backend.dto.profile.UserProfileResponse;
import com.fitlat.backend.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/profile")
@PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
public class UserProfileController {

    private final UserProfileService profileService;

    public UserProfileController(UserProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public UserProfileResponse getProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.parseLong(jwt.getSubject());

        return profileService.getProfile(userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponse updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        Long userId = Long.parseLong(jwt.getSubject());

        return profileService.updateProfile(userId, request);
    }
}
