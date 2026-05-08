package com.socialmedia.user.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils Tests")
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private UserDetailsImpl userDetails;
    private String validSecret;
    private int expirationMs;

    @BeforeEach
    void setUp() {
        // Setup JWT secret (must be valid Base64 and 256 bits for HS256)
        validSecret = "c3VwZXJzZWNyZXRrZXlmb3Jqd3R0ZXN0aW5ndGhpc2lzdmVyeWxvbmd0b2Vuc3VyZWl0d29ya3M=";
        expirationMs = 3600000; // 1 hour in milliseconds

        // Inject the secret and expiration values using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", validSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", expirationMs);

        // Create test UserDetails
        userDetails = new UserDetailsImpl(
                1L,
                "testuser",
                "test@example.com",
                "password123",
                null
        );
    }

    @Test
    @DisplayName("generateJwtToken creates a valid JWT token")
    void testGenerateJwtToken_CreatesValidToken() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."), "JWT token should contain dots (header.payload.signature)");
    }

    @Test
    @DisplayName("generateJwtToken includes username in token")
    void testGenerateJwtToken_IncludesUsername() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert
        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("getUserNameFromJwtToken extracts username from valid token")
    void testGetUserNameFromJwtToken_ExtractsUsernameFromValidToken() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // Act
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

        // Assert
        assertEquals("testuser", extractedUsername);
    }

    @Test
    @DisplayName("validateJwtToken returns true for valid token")
    void testValidateJwtToken_ValidToken_ReturnsTrue() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String validToken = jwtUtils.generateJwtToken(authentication);

        // Act
        boolean result = jwtUtils.validateJwtToken(validToken);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("validateJwtToken returns false for malformed token")
    void testValidateJwtToken_MalformedToken_ReturnsFalse() {
        // Arrange
        String malformedToken = "invalid.token.format.with.extra.dots";

        // Act
        boolean result = jwtUtils.validateJwtToken(malformedToken);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("validateJwtToken returns false for empty token")
    void testValidateJwtToken_EmptyToken_ReturnsFalse() {
        // Arrange
        String emptyToken = "";

        // Act
        boolean result = jwtUtils.validateJwtToken(emptyToken);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("validateJwtToken returns false for null token")
    void testValidateJwtToken_NullToken_ReturnsFalse() {
        // Act & Assert
        assertFalse(jwtUtils.validateJwtToken(null));
    }

    @Test
    @DisplayName("validateJwtToken returns false for expired token")
    void testValidateJwtToken_ExpiredToken_ReturnsFalse() {
        // Arrange
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(validSecret));
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .claim("id", 1L)
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000))
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // Expired 1 hour ago
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Act
        boolean result = jwtUtils.validateJwtToken(expiredToken);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("validateJwtToken returns false for token signed with different key")
    void testValidateJwtToken_WrongSignature_ReturnsFalse() {
        // Arrange
        String wrongSecret = "d2l0aHdyb25nc2VjcmV0a2V5Zm9yand0dGVzdGluZ3RoaXNpc2RpZmZlcmVudGtleQ==";
        Key wrongKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(wrongSecret));
        String tokenWithWrongSignature = Jwts.builder()
                .setSubject("testuser")
                .claim("id", 1L)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(wrongKey, SignatureAlgorithm.HS256)
                .compact();

        // Act
        boolean result = jwtUtils.validateJwtToken(tokenWithWrongSignature);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("generateJwtToken with different users creates different tokens")
    void testGenerateJwtToken_DifferentUsers_CreateDifferentTokens() {
        // Arrange
        UserDetailsImpl userDetails2 = new UserDetailsImpl(
                2L,
                "anotheruser",
                "another@example.com",
                "password456",
                null
        );
        when(authentication.getPrincipal())
                .thenReturn(userDetails)
                .thenReturn(userDetails2);

        // Act
        String token1 = jwtUtils.generateJwtToken(authentication);
        String token2 = jwtUtils.generateJwtToken(authentication);

        // Assert
        assertNotEquals(token1, token2);
        assertEquals("testuser", jwtUtils.getUserNameFromJwtToken(token1));
        assertEquals("anotheruser", jwtUtils.getUserNameFromJwtToken(token2));
    }

    @Test
    @DisplayName("validateJwtToken handles various invalid formats gracefully")
    void testValidateJwtToken_InvalidFormats_ReturnsFalse() {
        // Arrange
        String[] invalidTokens = {
                "not a jwt at all",
                "onlyonepart",
                "part1.part2",
                "!!!invalid!!!.!!!chars!!!.!!!here!!!",
                " ",
                "\n",
                "\t"
        };

        // Act & Assert
        for (String invalidToken : invalidTokens) {
            assertFalse(jwtUtils.validateJwtToken(invalidToken),
                    "Should return false for invalid token: " + invalidToken);
        }
    }
}
