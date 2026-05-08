package com.socialmedia.post.service;

import com.socialmedia.post.client.UserClient;
import com.socialmedia.post.model.Post;
import com.socialmedia.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService Tests")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostProducer postProducer;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private List<Post> testPosts;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testPost = Post.builder()
                .id(1L)
                .content("This is a test post")
                .userId(1L)
                .imageUrl("https://example.com/image.jpg")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .content("Another test post")
                .userId(1L)
                .imageUrl(null)
                .createdAt(now.minusHours(1))
                .updatedAt(now.minusHours(1))
                .build();

        Post post3 = Post.builder()
                .id(3L)
                .content("Post from another user")
                .userId(2L)
                .imageUrl(null)
                .createdAt(now.minusHours(2))
                .updatedAt(now.minusHours(2))
                .build();

        testPosts = Arrays.asList(testPost, post2, post3);
    }

    @Test
    @DisplayName("createPost saves post and sends event")
    void testCreatePost_SavesPostAndSendsEvent() {
        // Arrange
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Post result = postService.createPost(testPost);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("This is a test post", result.getContent());
        verify(postRepository, times(1)).save(testPost);
        verify(postProducer, times(1)).sendPostCreatedEvent(testPost);
    }

    @Test
    @DisplayName("createPost returns saved post with correct data")
    void testCreatePost_ReturnsSavedPost() {
        // Arrange
        Post newPost = Post.builder()
                .content("New post")
                .userId(1L)
                .build();
        when(postRepository.save(newPost)).thenReturn(testPost);

        // Act
        Post result = postService.createPost(newPost);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("This is a test post", result.getContent());
    }

    @Test
    @DisplayName("getFeed returns posts from user and following users")
    void testGetFeed_ReturnsPostsFromUserAndFollowing() {
        // Arrange
        Long userId = 1L;
        List<Long> followingUsers = Arrays.asList(2L, 3L);
        when(userClient.getFollowing(userId)).thenReturn(followingUsers);
        when(postRepository.findByUserIdInOrderByCreatedAtDesc(any())).thenReturn(testPosts);

        // Act
        List<Post> result = postService.getFeed(userId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(userClient, times(1)).getFollowing(userId);
        verify(postRepository, times(1)).findByUserIdInOrderByCreatedAtDesc(any());
    }

    @Test
    @DisplayName("getFeed includes user's own posts")
    void testGetFeed_IncludesOwnPosts() {
        // Arrange
        Long userId = 1L;
        List<Long> followingUsers = Collections.emptyList();
        when(userClient.getFollowing(userId)).thenReturn(followingUsers);
        List<Post> expectedPosts = Arrays.asList(testPost);
        when(postRepository.findByUserIdInOrderByCreatedAtDesc(eq(Arrays.asList(userId))))
                .thenReturn(expectedPosts);

        // Act
        List<Post> result = postService.getFeed(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(p -> p.getUserId().equals(userId)));
        verify(postRepository, times(1)).findByUserIdInOrderByCreatedAtDesc(any());
    }

    @Test
    @DisplayName("getAllPosts returns all posts ordered by creation date")
    void testGetAllPosts_ReturnsAllPosts() {
        // Arrange
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(testPosts);

        // Act
        List<Post> result = postService.getAllPosts();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(postRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("getAllPosts returns empty list when no posts exist")
    void testGetAllPosts_EmptyList() {
        // Arrange
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        // Act
        List<Post> result = postService.getAllPosts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("getPostsByUserId returns posts for specific user")
    void testGetPostsByUserId_ReturnsUserPosts() {
        // Arrange
        Long userId = 1L;
        List<Post> userPosts = Arrays.asList(testPost);
        when(postRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(userPosts);

        // Act
        List<Post> result = postService.getPostsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        verify(postRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("getPostsByUserId returns empty list when user has no posts")
    void testGetPostsByUserId_NoPostsForUser() {
        // Arrange
        Long userId = 999L;
        when(postRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Collections.emptyList());

        // Act
        List<Post> result = postService.getPostsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getPostById returns post when found")
    void testGetPostById_PostFound() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act
        Post result = postService.getPostById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("This is a test post", result.getContent());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getPostById throws exception when post not found")
    void testGetPostById_PostNotFound() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> postService.getPostById(999L));
        verify(postRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("getPostById throws exception with correct message when not found")
    void testGetPostById_NotFound_HasCorrectMessage() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> postService.getPostById(999L));
        assertEquals("Post not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("deletePost removes post by id")
    void testDeletePost_RemovesPost() {
        // Arrange
        Long postId = 1L;

        // Act
        postService.deletePost(postId);

        // Assert
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    @DisplayName("deletePost with different ids calls repository for each")
    void testDeletePost_MultipleDeletes() {
        // Act
        postService.deletePost(1L);
        postService.deletePost(2L);
        postService.deletePost(3L);

        // Assert
        verify(postRepository, times(1)).deleteById(1L);
        verify(postRepository, times(1)).deleteById(2L);
        verify(postRepository, times(1)).deleteById(3L);
    }

    @Test
    @DisplayName("getFeed with multiple following users includes all their posts")
    void testGetFeed_MultipleFollowingUsers() {
        // Arrange
        Long userId = 1L;
        List<Long> followingUsers = Arrays.asList(2L, 3L, 4L);
        when(userClient.getFollowing(userId)).thenReturn(followingUsers);
        when(postRepository.findByUserIdInOrderByCreatedAtDesc(any())).thenReturn(testPosts);

        // Act
        List<Post> result = postService.getFeed(userId);

        // Assert
        assertNotNull(result);
        verify(postRepository, times(1)).findByUserIdInOrderByCreatedAtDesc(any());
    }

    @Test
    @DisplayName("createPost handles post without image url")
    void testCreatePost_WithoutImageUrl() {
        // Arrange
        Post postWithoutImage = Post.builder()
                .id(4L)
                .content("Post without image")
                .userId(1L)
                .imageUrl(null)
                .build();
        when(postRepository.save(any(Post.class))).thenReturn(postWithoutImage);

        // Act
        Post result = postService.createPost(postWithoutImage);

        // Assert
        assertNotNull(result);
        assertNull(result.getImageUrl());
        verify(postProducer, times(1)).sendPostCreatedEvent(postWithoutImage);
    }
}
