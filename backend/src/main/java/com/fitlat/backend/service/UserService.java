package com.fitlat.backend.service;

import com.fitlat.backend.entity.User;
import com.fitlat.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;

import com.fitlat.backend.exception.EmailAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public User createUser(String fullName, String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                "Email is already in use"
            );
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);

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

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + id
                ));
    }

    @Transactional 
    public User updateUser(Long id, String fullName, String email) {

        // Tìm user cần cập nhật
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + id
                ));

        // Kiểm tra email có thuộc user khác không
        if (!user.getEmail().equalsIgnoreCase(email)
                && userRepository.existsByEmail(email)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already in use"
            );
        }

        // Cập nhật thông tin
        user.setFullName(fullName);
        user.setEmail(email);

        // Lưu thay đổi
        return userRepository.save(user);
    }

    @Transactional 
    public void deleteUser(Long id) {

        // Kiểm tra user có tồn tại không
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + id
                ));

        // Xóa user
        userRepository.delete(user);
    }
}