package com.socialix.server.service;

import com.socialix.server.Repository.PostRepository;
import com.socialix.server.entities.Post;
import com.socialix.server.entities.PostStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostScheduleWorker {

    private final PostRepository postRepository;
    private final PostProducerService producerService;

    public PostScheduleWorker(PostRepository postRepository, PostProducerService producerService) {
        this.postRepository = postRepository;
        this.producerService = producerService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkAndDispatchScheduledPosts() {
        long currentEpochMillis = System.currentTimeMillis();

        List<Post> scheduledPosts = postRepository.findAll().stream()
                .filter(p -> p.getStatus() == PostStatus.SCHEDULED)
                .filter(p -> p.getScheduledTimestamp() != null && p.getScheduledTimestamp() <= currentEpochMillis)
                .toList();

        for (Post post : scheduledPosts) {
            producerService.publishPostEvent(post.getId());
        }
    }
}
