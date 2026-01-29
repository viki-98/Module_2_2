package org.post_hub.controller;

import org.post_hub.model.Post;
import org.post_hub.service.PostService;
import java.util.List;

public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    public Post createPost(String content, Long writerId) {
        Post post = new Post();
        post.setContent(content);
        post.setWriterId(writerId);
        return postService.createPost(post);
    }

    public Post getById(Long id) {
        return postService.getById(id);
    }

    public List<Post> getAll() {
        return postService.getAll();
    }

    public Post updatePost(Long id, String content) {
        Post post = postService.getById(id);
        if (post != null) {
            post.setContent(content);
            return postService.updatePost(post);
        }
        return null;
    }

    public void deletePost(Long id) {
        postService.deletePost(id);
    }
}