package zahro.uni.service.mapper;

import static zahro.uni.domain.TagsAsserts.*;
import static zahro.uni.domain.TagsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TagsMapperTest {

    private TagsMapper tagsMapper;

    @BeforeEach
    void setUp() {
        tagsMapper = new TagsMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTagsSample1();
        var actual = tagsMapper.toEntity(tagsMapper.toDto(expected));
        assertTagsAllPropertiesEquals(expected, actual);
    }
}
