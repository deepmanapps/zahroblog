package zahro.uni.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zahro.uni.domain.CommentsTestSamples.*;
import static zahro.uni.domain.ReplyCommentsTestSamples.*;

import org.junit.jupiter.api.Test;
import zahro.uni.web.rest.TestUtil;

class ReplyCommentsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReplyComments.class);
        ReplyComments replyComments1 = getReplyCommentsSample1();
        ReplyComments replyComments2 = new ReplyComments();
        assertThat(replyComments1).isNotEqualTo(replyComments2);

        replyComments2.setReplyId(replyComments1.getReplyId());
        assertThat(replyComments1).isEqualTo(replyComments2);

        replyComments2 = getReplyCommentsSample2();
        assertThat(replyComments1).isNotEqualTo(replyComments2);
    }

    @Test
    void parentCommentIdTest() {
        ReplyComments replyComments = getReplyCommentsRandomSampleGenerator();
        Comments commentsBack = getCommentsRandomSampleGenerator();

        replyComments.setParentCommentId(commentsBack);
        assertThat(replyComments.getParentCommentId()).isEqualTo(commentsBack);

        replyComments.parentCommentId(null);
        assertThat(replyComments.getParentCommentId()).isNull();
    }
}
