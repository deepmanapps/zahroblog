package zahro.uni.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zahro.uni.web.rest.TestUtil;

class CommentsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommentsDTO.class);
        CommentsDTO commentsDTO1 = new CommentsDTO();
        commentsDTO1.setCommentId(1L);
        CommentsDTO commentsDTO2 = new CommentsDTO();
        assertThat(commentsDTO1).isNotEqualTo(commentsDTO2);
        commentsDTO2.setCommentId(commentsDTO1.getCommentId());
        assertThat(commentsDTO1).isEqualTo(commentsDTO2);
        commentsDTO2.setCommentId(2L);
        assertThat(commentsDTO1).isNotEqualTo(commentsDTO2);
        commentsDTO1.setCommentId(null);
        assertThat(commentsDTO1).isNotEqualTo(commentsDTO2);
    }
}
