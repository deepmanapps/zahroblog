package zahro.uni.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link zahro.uni.domain.Tags} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TagsDTO implements Serializable {

    private Long tagId;

    private String tagName;

    private Set<PostsDTO> posts = new HashSet<>();

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Set<PostsDTO> getPosts() {
        return posts;
    }

    public void setPosts(Set<PostsDTO> posts) {
        this.posts = posts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagsDTO)) {
            return false;
        }

        TagsDTO tagsDTO = (TagsDTO) o;
        if (this.tagId == null) {
            return false;
        }
        return Objects.equals(this.tagId, tagsDTO.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tagId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagsDTO{" +
            "tagId=" + getTagId() +
            ", tagName='" + getTagName() + "'" +
            ", posts=" + getPosts() +
            "}";
    }
}
