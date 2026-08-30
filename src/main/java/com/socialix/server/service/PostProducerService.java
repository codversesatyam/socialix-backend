package com.socialix.server.service;


import com.socialix.server.config.KafkaTopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostProducerService{

    private final KafkaTemplate<String, String> kafkaTemplate;

    public PostProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPostEvent(String postId) {
        kafkaTemplate.send(KafkaTopicConfig.POST_PUBLISH_TOPIC, postId);
        System.out.println("[Kafka Producer] Dispatched post publishing event for Post ID: " + postId);
    }
}
