package zahro.uni.service.mapper;

import static zahro.uni.domain.CommentsAsserts.*;
import static zahro.uni.domain.CommentsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommentsMapperTest {

    private CommentsMapper commentsMapper;

    @BeforeEach
    void setUp() {
        commentsMapper = new CommentsMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCommentsSample1();
        var actual = commentsMapper.toEntity(commentsMapper.toDto(expected));
        assertCommentsAllPropertiesEquals(expected, actual);
    }
}
