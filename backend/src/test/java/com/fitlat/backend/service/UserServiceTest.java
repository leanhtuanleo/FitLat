package com.fitlat.backend.service;

import com.fitlat.backend.entity.User;
import com.fitlat.backend.exception.EmailAlreadyExistsException;
import com.fitlat.backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.createUser(
                "Test User",
                "test@example.com"
        );

        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getFullName());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void createUser_shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        // Act & Assert
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(
                        "Test User",
                        "test@example.com"
                )
        );

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void getUserById_shouldReturnUserWhenIdExists() {
        // Arrange: Tạo dữ liệu giả
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
    
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
    
        // Act: Gọi phương thức cần kiểm thử
        User result = userService.getUserById(1L);
    
        // Assert: Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Test User", result.getFullName());
        assertEquals("test@example.com", result.getEmail());
    
        verify(userRepository).findById(1L);
    }
    
    @Test
    void getUserById_shouldThrow404WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());
    
        // Act + Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.getUserById(999L)
        );
    
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    
        verify(userRepository).findById(999L);
    }

    // ==================== GET ALL USERS ====================
    
    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setFullName("User One");
        user1.setEmail("user1@example.com");
    
        User user2 = new User();
        user2.setFullName("User Two");
        user2.setEmail("user2@example.com");
    
        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));
    
        // Act
        List<User> result = userService.getAllUsers();
    
        // Assert
        assertEquals(2, result.size());
        assertEquals("User One", result.get(0).getFullName());
        assertEquals("User Two", result.get(1).getFullName());
    
        verify(userRepository).findAll();
    }
    
    
    // ==================== UPDATE USER ====================
    
    @Test
    void updateUser_shouldUpdateUserSuccessfully() {
        // Arrange
        User user = new User();
        user.setFullName("Old Name");
        user.setEmail("old@example.com");
    
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
    
        when(userRepository.existsByEmail("new@example.com"))
                .thenReturn(false);
    
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    
        // Act
        User result = userService.updateUser(
                1L,
                "New Name",
                "new@example.com"
        );
    
        // Assert
        assertEquals("New Name", result.getFullName());
        assertEquals("new@example.com", result.getEmail());
    
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }
    
    @Test
    void updateUser_shouldThrow404WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());
    
        // Act + Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.updateUser(
                        999L,
                        "New Name",
                        "new@example.com"
                )
        );
    
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void updateUser_shouldThrow409WhenEmailAlreadyExists() {
        // Arrange
        User user = new User();
        user.setFullName("Old Name");
        user.setEmail("old@example.com");
    
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
    
        when(userRepository.existsByEmail("taken@example.com"))
                .thenReturn(true);
    
        // Act + Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.updateUser(
                        1L,
                        "New Name",
                        "taken@example.com"
                )
        );
    
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    
        verify(userRepository, never()).save(any(User.class));
    }
    
    
    // ==================== DELETE USER ====================
    
    @Test
    void deleteUser_shouldDeleteUserSuccessfully() {
        // Arrange
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
    
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
    
        // Act
        userService.deleteUser(1L);
    
        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }
    
    @Test
    void deleteUser_shouldThrow404WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());
    
        // Act + Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.deleteUser(999L)
        );
    
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    
        verify(userRepository, never()).delete(any(User.class));
    }
}