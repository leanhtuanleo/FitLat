
package com.fitlat.backend.service;

import com.fitlat.backend.dto.profile.UpdateUserProfileRequest;
import com.fitlat.backend.dto.profile.UserProfileResponse;
import com.fitlat.backend.entity.User;
import com.fitlat.backend.entity.UserProfile;
import com.fitlat.backend.repository.UserProfileRepository;
import com.fitlat.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;

    public UserProfileService(
            UserProfileRepository profileRepository,
            UserRepository userRepository
    ) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User profile not found"
                ));

        return toResponse(profile);
    }

    @Transactional
    public UserProfileResponse updateProfile(
            Long userId,
            UpdateUserProfileRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setDateOfBirth(request.dateOfBirth());
        profile.setBiologicalSex(request.biologicalSex());
        profile.setHeightCm(request.heightCm());
        profile.setCurrentWeightKg(request.currentWeightKg());
        profile.setActivityLevel(request.activityLevel());

        UserProfile savedProfile = profileRepository.save(profile);

        return toResponse(savedProfile);
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getDateOfBirth(),
                profile.getBiologicalSex(),
                profile.getHeightCm(),
                profile.getCurrentWeightKg(),
                profile.getActivityLevel(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
