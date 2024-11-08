package zahro.uni.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zahro.uni.domain.CommentsTestSamples.*;
import static zahro.uni.domain.PostsTestSamples.*;
import static zahro.uni.domain.TagsTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zahro.uni.web.rest.TestUtil;

class PostsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Posts.class);
        Posts posts1 = getPostsSample1();
        Posts posts2 = new Posts();
        assertThat(posts1).isNotEqualTo(posts2);

        posts2.setPostId(posts1.getPostId());
        assertThat(posts1).isEqualTo(posts2);

        posts2 = getPostsSample2();
        assertThat(posts1).isNotEqualTo(posts2);
    }

    @Test
    void tagsTest() {
        Posts posts = getPostsRandomSampleGenerator();
        Tags tagsBack = getTagsRandomSampleGenerator();

        posts.addTags(tagsBack);
        assertThat(posts.getTags()).containsOnly(tagsBack);

        posts.removeTags(tagsBack);
        assertThat(posts.getTags()).doesNotContain(tagsBack);

        posts.tags(new HashSet<>(Set.of(tagsBack)));
        assertThat(posts.getTags()).containsOnly(tagsBack);

        posts.setTags(new HashSet<>());
        assertThat(posts.getTags()).doesNotContain(tagsBack);
    }

    @Test
    void commentsTest() {
        Posts posts = getPostsRandomSampleGenerator();
        Comments commentsBack = getCommentsRandomSampleGenerator();

        posts.addComments(commentsBack);
        assertThat(posts.getComments()).containsOnly(commentsBack);
        assertThat(commentsBack.getPosts()).isEqualTo(posts);

        posts.removeComments(commentsBack);
        assertThat(posts.getComments()).doesNotContain(commentsBack);
        assertThat(commentsBack.getPosts()).isNull();

        posts.comments(new HashSet<>(Set.of(commentsBack)));
        assertThat(posts.getComments()).containsOnly(commentsBack);
        assertThat(commentsBack.getPosts()).isEqualTo(posts);

        posts.setComments(new HashSet<>());
        assertThat(posts.getComments()).doesNotContain(commentsBack);
        assertThat(commentsBack.getPosts()).isNull();
    }
}
