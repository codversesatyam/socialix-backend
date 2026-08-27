package com.socialix.server.controller;

import com.socialix.server.dto.AnalyticsResponse;
import com.socialix.server.entities.Post;
import com.socialix.server.entities.User;
import com.socialix.server.Repository.PostRepository;
import com.socialix.server.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public AnalyticsController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics(
            Authentication authentication,
            @RequestParam(defaultValue = "7D") String range) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        int days = switch (range.toUpperCase()) {
            case "30D" -> 30;
            case "90D" -> 90;
            case "1Y" -> 365;
            default -> 7;
        };

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(startDate))
                .collect(Collectors.toList());

        DateTimeFormatter formatter = days <= 30 ? DateTimeFormatter.ofPattern("dd MMM") : DateTimeFormatter.ofPattern("MMM yyyy");
        Map<String, Long> pointsMap = new LinkedHashMap<>();
        for (int i = days; i >= 0; i -= Math.max(1, days / 7)) {
            pointsMap.put(LocalDateTime.now().minusDays(i).format(formatter), 0L);
        }

        Map<String, Long> platformStats = new HashMap<>();
        for (Post p : posts) {
            String key = p.getCreatedAt().format(formatter);
            pointsMap.put(key, pointsMap.getOrDefault(key, 0L) + 1);

            if (p.getPlatforms() != null) {
                for (String platform : p.getPlatforms()) {
                    platformStats.put(platform, platformStats.getOrDefault(platform, 0L) + 1);
                }
            }
        }

        List<AnalyticsResponse.DataPoint> chartPoints = pointsMap.entrySet().stream()
                .map(e -> new AnalyticsResponse.DataPoint(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        long totalCount = posts.size();
        double growth = totalCount > 0 ? (totalCount * 12.5) : 0.0;

        return ResponseEntity.ok(new AnalyticsResponse(totalCount, growth, chartPoints, platformStats));
    }
}