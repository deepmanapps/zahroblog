package zahro.uni.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zahro.uni.web.rest.TestUtil;

class PostsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostsDTO.class);
        PostsDTO postsDTO1 = new PostsDTO();
        postsDTO1.setPostId(1L);
        PostsDTO postsDTO2 = new PostsDTO();
        assertThat(postsDTO1).isNotEqualTo(postsDTO2);
        postsDTO2.setPostId(postsDTO1.getPostId());
        assertThat(postsDTO1).isEqualTo(postsDTO2);
        postsDTO2.setPostId(2L);
        assertThat(postsDTO1).isNotEqualTo(postsDTO2);
        postsDTO1.setPostId(null);
        assertThat(postsDTO1).isNotEqualTo(postsDTO2);
    }
}
