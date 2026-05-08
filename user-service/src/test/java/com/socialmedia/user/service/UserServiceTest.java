package com.socialmedia.user.service;

import com.socialmedia.user.dto.SignupRequest;
import com.socialmedia.user.model.User;
import com.socialmedia.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword123")
                .roles(new HashSet<>(Set.of("ROLE_USER")))
                .build();

        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password123");
    }

    @Test
    @DisplayName("existsByUsername returns true when user exists")
    void testExistsByUsername_UserExists() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean result = userService.existsByUsername("testuser");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    @DisplayName("existsByUsername returns false when user does not exist")
    void testExistsByUsername_UserNotExists() {
        // Arrange
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        boolean result = userService.existsByUsername("nonexistent");

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsByUsername("nonexistent");
    }

    @Test
    @DisplayName("existsByEmail returns true when email exists")
    void testExistsByEmail_EmailExists() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean result = userService.existsByEmail("test@example.com");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("existsByEmail returns false when email does not exist")
    void testExistsByEmail_EmailNotExists() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("registerUser with null roles assigns ROLE_USER by default")
    void testRegisterUser_WithNullRoles_AssignsDefaultRole() {
        // Arrange
        signupRequest.setRoles(null);
        when(encoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.registerUser(signupRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoles().contains("ROLE_USER"));
        assertEquals(1, result.getRoles().size());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser with admin role maps to ROLE_ADMIN")
    void testRegisterUser_WithAdminRole_MapsCorrectly() {
        // Arrange
        signupRequest.setRoles(Set.of("admin"));
        User expectedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .password("encodedPassword123")
                .roles(Set.of("ROLE_ADMIN"))
                .build();
        when(encoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User result = userService.registerUser(signupRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoles().contains("ROLE_ADMIN"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser with mod role maps to ROLE_MODERATOR")
    void testRegisterUser_WithModRole_MapsCorrectly() {
        // Arrange
        signupRequest.setRoles(Set.of("mod"));
        User expectedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .password("encodedPassword123")
                .roles(Set.of("ROLE_MODERATOR"))
                .build();
        when(encoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User result = userService.registerUser(signupRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoles().contains("ROLE_MODERATOR"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser with unknown role defaults to ROLE_USER")
    void testRegisterUser_WithUnknownRole_DefaultsToUser() {
        // Arrange
        signupRequest.setRoles(Set.of("unknown_role"));
        User expectedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .password("encodedPassword123")
                .roles(Set.of("ROLE_USER"))
                .build();
        when(encoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User result = userService.registerUser(signupRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoles().contains("ROLE_USER"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser with multiple roles maps all roles correctly")
    void testRegisterUser_WithMultipleRoles_MapsAllCorrectly() {
        // Arrange
        signupRequest.setRoles(Set.of("admin", "mod"));
        User expectedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .password("encodedPassword123")
                .roles(Set.of("ROLE_ADMIN", "ROLE_MODERATOR"))
                .build();
        when(encoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User result = userService.registerUser(signupRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoles().contains("ROLE_ADMIN"));
        assertTrue(result.getRoles().contains("ROLE_MODERATOR"));
        assertEquals(2, result.getRoles().size());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser encodes password before saving")
    void testRegisterUser_EncodesPasswordBeforeSaving() {
        // Arrange
        signupRequest.setRoles(null);
        when(encoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user;
        });

        // Act
        User result = userService.registerUser(signupRequest);

        // Assert
        assertEquals("encodedPassword123", result.getPassword());
        verify(encoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("findByUsername returns user when found")
    void testFindByUsername_UserFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("findByUsername throws exception when user not found")
    void testFindByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.findByUsername("nonexistent"));
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("findByUsername throws exception with correct error message")
    void testFindByUsername_NotFound_HasCorrectMessage() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.findByUsername("nonexistent"));
        assertEquals("Error: User not found.", exception.getMessage());
    }
}
