package com.socialmedia.user.security;

import com.socialmedia.user.model.User;
import com.socialmedia.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword123")
                .roles(Set.of("ROLE_USER"))
                .build();
    }

    @Test
    @DisplayName("loadUserByUsername returns UserDetails when user exists")
    void testLoadUserByUsername_UserExists() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("loadUserByUsername returns UserDetails with correct authorities")
    void testLoadUserByUsername_ReturnsUserDetailsWithAuthorities() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertTrue(result.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .anyMatch(auth -> auth.equals("ROLE_USER")));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("loadUserByUsername throws UsernameNotFoundException when user not found")
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("loadUserByUsername throws exception with correct message when user not found")
    void testLoadUserByUsername_NotFound_HasCorrectMessage() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));
        assertEquals("User Not Found with username: nonexistent", exception.getMessage());
    }

    @Test
    @DisplayName("loadUserByUsername with admin role returns correct authorities")
    void testLoadUserByUsername_AdminRole() {
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .username("admin")
                .email("admin@example.com")
                .password("adminPassword123")
                .roles(Set.of("ROLE_ADMIN", "ROLE_USER"))
                .build();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("admin");

        // Assert
        assertNotNull(result);
        assertTrue(result.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .anyMatch(auth -> auth.equals("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .anyMatch(auth -> auth.equals("ROLE_USER")));
    }

    @Test
    @DisplayName("loadUserByUsername returns UserDetails with correct password")
    void testLoadUserByUsername_CorrectPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals("encodedPassword123", result.getPassword());
    }

    @Test
    @DisplayName("loadUserByUsername returns UserDetails that is enabled")
    void testLoadUserByUsername_UserIsEnabled() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("loadUserByUsername is case-sensitive for username")
    void testLoadUserByUsername_CaseSensitive() {
        // Arrange
        when(userRepository.findByUsername("TestUser")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("TestUser"));
        assertNotNull(userDetailsService.loadUserByUsername("testuser"));
        verify(userRepository, times(1)).findByUsername("TestUser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("loadUserByUsername handles multiple role mappings correctly")
    void testLoadUserByUsername_MultipleRoles() {
        // Arrange
        User multiRoleUser = User.builder()
                .id(3L)
                .username("multiuser")
                .email("multi@example.com")
                .password("password123")
                .roles(Set.of("ROLE_USER", "ROLE_MODERATOR", "ROLE_ADMIN"))
                .build();
        when(userRepository.findByUsername("multiuser")).thenReturn(Optional.of(multiRoleUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("multiuser");

        // Assert
        assertEquals(3, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .allMatch(auth -> auth.startsWith("ROLE_")));
    }

    @Test
    @DisplayName("loadUserByUsername with whitespace in username queries correctly")
    void testLoadUserByUsername_WithWhitespaceInQuery() {
        // Arrange
        when(userRepository.findByUsername("  testuser  ")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("  testuser  "));
        verify(userRepository, times(1)).findByUsername("  testuser  ");
    }

    @Test
    @DisplayName("loadUserByUsername with special characters in username")
    void testLoadUserByUsername_SpecialCharactersInUsername() {
        // Arrange
        User specialUser = User.builder()
                .id(4L)
                .username("user@domain.com")
                .email("user@domain.com")
                .password("password123")
                .roles(Set.of("ROLE_USER"))
                .build();
        when(userRepository.findByUsername("user@domain.com")).thenReturn(Optional.of(specialUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("user@domain.com");

        // Assert
        assertNotNull(result);
        assertEquals("user@domain.com", result.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername calls repository exactly once per call")
    void testLoadUserByUsername_RepositoryCalledOnce() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        userDetailsService.loadUserByUsername("testuser");

        // Assert
        verify(userRepository, times(1)).findByUsername("testuser");
    }
}
