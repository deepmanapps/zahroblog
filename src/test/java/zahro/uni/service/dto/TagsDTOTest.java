package zahro.uni.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zahro.uni.web.rest.TestUtil;

class TagsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TagsDTO.class);
        TagsDTO tagsDTO1 = new TagsDTO();
        tagsDTO1.setTagId(1L);
        TagsDTO tagsDTO2 = new TagsDTO();
        assertThat(tagsDTO1).isNotEqualTo(tagsDTO2);
        tagsDTO2.setTagId(tagsDTO1.getTagId());
        assertThat(tagsDTO1).isEqualTo(tagsDTO2);
        tagsDTO2.setTagId(2L);
        assertThat(tagsDTO1).isNotEqualTo(tagsDTO2);
        tagsDTO1.setTagId(null);
        assertThat(tagsDTO1).isNotEqualTo(tagsDTO2);
    }
}
