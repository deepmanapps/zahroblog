package zahro.uni.service.mapper;

import static zahro.uni.domain.ReplyCommentsAsserts.*;
import static zahro.uni.domain.ReplyCommentsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReplyCommentsMapperTest {

    private ReplyCommentsMapper replyCommentsMapper;

    @BeforeEach
    void setUp() {
        replyCommentsMapper = new ReplyCommentsMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReplyCommentsSample1();
        var actual = replyCommentsMapper.toEntity(replyCommentsMapper.toDto(expected));
        assertReplyCommentsAllPropertiesEquals(expected, actual);
    }
}
