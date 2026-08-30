package com.socialix.server.service;

import com.socialix.server.Repository.PostRepository;
import com.socialix.server.config.KafkaTopicConfig;
import com.socialix.server.entities.PostStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class PostConsumerService {

    private final PostRepository postRepository;

    public PostConsumerService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @KafkaListener(topics = KafkaTopicConfig.POST_PUBLISH_TOPIC, groupId = "socialix-publisher-group")
    public void consumePostEvent(String postId) {
        System.out.println(" [Kafka Consumer] Received publish event for Post ID: " + postId);

        postRepository.findById(postId).ifPresent(post -> {
            try {
                // Simulated third-party platform API publishing latency
                Thread.sleep(1500);

                post.setStatus(PostStatus.PUBLISHED);
                postRepository.save(post);

                System.out.println("[Kafka Consumer] Successfully published post: " + post.getId());
            } catch (Exception e) {
                System.err.println(" [Kafka Consumer] Failed to publish post: " + e.getMessage());
            }
        });

    }
}
