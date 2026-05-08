package com.socialmedia.user.service;

import com.socialmedia.user.model.Follow;
import com.socialmedia.user.repository.FollowRepository;
import com.socialmedia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SocialService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationProducer notificationProducer;

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("You cannot follow yourself.");
        }

        if (!userRepository.existsById(followingId)) {
            throw new RuntimeException("User to follow not found.");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("Already following this user.");
        }

        Follow follow = Follow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();
        followRepository.save(follow);

        // Send Notification
        userRepository.findById(followerId).ifPresent(follower -> {
            notificationProducer.sendFollowNotification(followerId, followingId, follower.getUsername());
        });
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new RuntimeException("Follow relationship not found."));
        followRepository.delete(follow);
    }

    public List<Long> getFollowingIds(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(Follow::getFollowingId)
                .collect(Collectors.toList());
    }

    public List<Long> getFollowerIds(Long userId) {
        return followRepository.findByFollowingId(userId).stream()
                .map(Follow::getFollowerId)
                .collect(Collectors.toList());
    }

    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    public long getFollowerCount(Long userId) {
        return followRepository.countByFollowingId(userId);
    }
}
