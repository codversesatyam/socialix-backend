package com.socialix.server.service;

import com.socialix.server.Repository.PostRepository;
import com.socialix.server.entities.Post;
import com.socialix.server.entities.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostPublisherService {

    private final PostRepository postRepository;

    @Scheduled(fixedRate = 30000)
    public void publicDuePost(){
        long currentMills = System.currentTimeMillis();
        List<Post> duePosts = postRepository.findByStatusAndScheduledTimestampLessThanEqual(
                PostStatus.SCHEDULED , currentMills);

        if(!duePosts.isEmpty()){
            for(Post post : duePosts){
                post.setStatus(PostStatus.PUBLISHED);
                postRepository.save(post);
                System.out.println("Published Post ID : " + post.getId() + " | Platforms: " + post.getPlatforms());
            }
        }
    }
}
