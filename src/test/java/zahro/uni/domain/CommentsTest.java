package zahro.uni.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zahro.uni.domain.CommentsTestSamples.*;
import static zahro.uni.domain.PostsTestSamples.*;

import org.junit.jupiter.api.Test;
import zahro.uni.web.rest.TestUtil;

class CommentsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comments.class);
        Comments comments1 = getCommentsSample1();
        Comments comments2 = new Comments();
        assertThat(comments1).isNotEqualTo(comments2);

        comments2.setCommentId(comments1.getCommentId());
        assertThat(comments1).isEqualTo(comments2);

        comments2 = getCommentsSample2();
        assertThat(comments1).isNotEqualTo(comments2);
    }

    @Test
    void postsTest() {
        Comments comments = getCommentsRandomSampleGenerator();
        Posts postsBack = getPostsRandomSampleGenerator();

        comments.setPosts(postsBack);
        assertThat(comments.getPosts()).isEqualTo(postsBack);

        comments.posts(null);
        assertThat(comments.getPosts()).isNull();
    }
}
