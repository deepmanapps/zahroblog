package zahro.uni.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zahro.uni.domain.PostsTestSamples.*;
import static zahro.uni.domain.TagsTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zahro.uni.web.rest.TestUtil;

class TagsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tags.class);
        Tags tags1 = getTagsSample1();
        Tags tags2 = new Tags();
        assertThat(tags1).isNotEqualTo(tags2);

        tags2.setTagId(tags1.getTagId());
        assertThat(tags1).isEqualTo(tags2);

        tags2 = getTagsSample2();
        assertThat(tags1).isNotEqualTo(tags2);
    }

    @Test
    void postsTest() {
        Tags tags = getTagsRandomSampleGenerator();
        Posts postsBack = getPostsRandomSampleGenerator();

        tags.addPosts(postsBack);
        assertThat(tags.getPosts()).containsOnly(postsBack);
        assertThat(postsBack.getTags()).containsOnly(tags);

        tags.removePosts(postsBack);
        assertThat(tags.getPosts()).doesNotContain(postsBack);
        assertThat(postsBack.getTags()).doesNotContain(tags);

        tags.posts(new HashSet<>(Set.of(postsBack)));
        assertThat(tags.getPosts()).containsOnly(postsBack);
        assertThat(postsBack.getTags()).containsOnly(tags);

        tags.setPosts(new HashSet<>());
        assertThat(tags.getPosts()).doesNotContain(postsBack);
        assertThat(postsBack.getTags()).doesNotContain(tags);
    }
}
