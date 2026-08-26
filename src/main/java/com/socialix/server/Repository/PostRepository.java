package com.socialix.server.Repository;

import com.socialix.server.entities.Post;
import com.socialix.server.entities.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByStatusOrderByScheduledTimestampAsc(PostStatus status);
    List<Post> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Post> findByStatusAndScheduledTimestampLessThanEqual(PostStatus status , Long currentTimestamp);


}
