package zahro.uni.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zahro.uni.web.rest.TestUtil;

class ReplyCommentsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReplyCommentsDTO.class);
        ReplyCommentsDTO replyCommentsDTO1 = new ReplyCommentsDTO();
        replyCommentsDTO1.setReplyId(1L);
        ReplyCommentsDTO replyCommentsDTO2 = new ReplyCommentsDTO();
        assertThat(replyCommentsDTO1).isNotEqualTo(replyCommentsDTO2);
        replyCommentsDTO2.setReplyId(replyCommentsDTO1.getReplyId());
        assertThat(replyCommentsDTO1).isEqualTo(replyCommentsDTO2);
        replyCommentsDTO2.setReplyId(2L);
        assertThat(replyCommentsDTO1).isNotEqualTo(replyCommentsDTO2);
        replyCommentsDTO1.setReplyId(null);
        assertThat(replyCommentsDTO1).isNotEqualTo(replyCommentsDTO2);
    }
}
