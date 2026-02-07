package org.post_hub.service;

import org.post_hub.model.Post;
import org.post_hub.model.PostStatus;
import org.post_hub.repository.PostRepository;
import org.post_hub.repository.impl.JdbcPostRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

public class PostService {

    private final PostRepository postRepository;

    public PostService() {
        this.postRepository = new JdbcPostRepositoryImpl();
    }

    public Post createPost(Post post) {
        LocalDateTime now = LocalDateTime.now();
        post.setCreated(now);
        post.setUpdated(now);

        if (post.getStatus() == null) {
            post.setStatus(PostStatus.ACTIVE);
        }

        return postRepository.save(post);
    }

    public Post getById(Long id) {
        return postRepository.getById(id);
    }

    public Post updatePost(Post post) {
        post.setUpdated(LocalDateTime.now());
        return postRepository.update(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.getById(id);
        if (post != null) {
            post.setStatus(PostStatus.DELETED);
            post.setUpdated(LocalDateTime.now());
            postRepository.update(post);
        }

    }

    public List<Post> getAll() {
        return postRepository.getAll();
    }
}