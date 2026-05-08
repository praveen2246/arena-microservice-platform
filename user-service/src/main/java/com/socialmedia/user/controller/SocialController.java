package com.socialmedia.user.controller;

import com.socialmedia.user.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/social")
public class SocialController {

    @Autowired
    private SocialService socialService;

    @PostMapping("/follow/{followingId}")
    public ResponseEntity<?> follow(@RequestHeader("userId") Long followerId, @PathVariable Long followingId) {
        socialService.follow(followerId, followingId);
        return ResponseEntity.ok(Map.of("message", "Followed successfully"));
    }

    @PostMapping("/unfollow/{followingId}")
    public ResponseEntity<?> unfollow(@RequestHeader("userId") Long followerId, @PathVariable Long followingId) {
        socialService.unfollow(followerId, followingId);
        return ResponseEntity.ok(Map.of("message", "Unfollowed successfully"));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<Long>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(socialService.getFollowingIds(userId));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<Long>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(socialService.getFollowerIds(userId));
    }

    @GetMapping("/counts/{userId}")
    public ResponseEntity<Map<String, Long>> getCounts(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of(
                "followers", socialService.getFollowerCount(userId),
                "following", socialService.getFollowingCount(userId)
        ));
    }
}
