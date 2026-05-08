package com.socialmedia.post.service;

import com.socialmedia.post.model.Post;
import com.socialmedia.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostProducer postProducer;

    @Autowired
    private com.socialmedia.post.client.UserClient userClient;

    public Post createPost(Post post) {
        Post savedPost = postRepository.save(post);
        postProducer.sendPostCreatedEvent(savedPost);
        return savedPost;
    }

    public List<Post> getFeed(Long userId) {
        List<Long> userIds = new java.util.ArrayList<>(userClient.getFollowing(userId));
        userIds.add(userId);
        return postRepository.findByUserIdInOrderByCreatedAtDesc(userIds);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
