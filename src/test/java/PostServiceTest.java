import org.post_hub.model.Post;
import org.post_hub.model.PostStatus;
import org.post_hub.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.post_hub.service.PostService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void createPost_ShouldSetDatesAndStatus() {
        Post inputPost = new Post();
        inputPost.setContent("Hello World");

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.createPost(inputPost);

        assertNotNull(result.getCreated());
        assertNotNull(result.getUpdated());
        assertEquals(PostStatus.ACTIVE, result.getStatus());
        verify(postRepository).save(inputPost);
    }

    @Test
    void deletePost_ShouldChangeStatusToDeleted() {
        Long postId = 1L;
        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setStatus(PostStatus.ACTIVE);

        when(postRepository.getById(postId)).thenReturn(existingPost);

        postService.deletePost(postId);

        assertEquals(PostStatus.DELETED, existingPost.getStatus());
        verify(postRepository).update(existingPost);
    }
}