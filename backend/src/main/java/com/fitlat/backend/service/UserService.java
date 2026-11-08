
package com.fitlat.backend.service;

import com.fitlat.backend.entity.User;
import com.fitlat.backend.repository.UserRepository;
import com.fitlat.backend.exception.EmailAlreadyExistsException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

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

    // Module 1: Create user without login credentials
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

    // Module 2: Register a new account
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

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + id
                ));

        if (!user.getEmail().equalsIgnoreCase(email)
                && userRepository.existsByEmail(email)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already in use"
            );
        }

        user.setFullName(fullName);
        user.setEmail(email);

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + id
                ));

        userRepository.delete(user);
    }
}
