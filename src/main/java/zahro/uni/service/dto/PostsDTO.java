package zahro.uni.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link zahro.uni.domain.Posts} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostsDTO implements Serializable {

    @NotNull
    private Long postId;

    private String title;

    private String content;

    private ZonedDateTime createdAt;

    private UserDTO user;

    private Set<TagsDTO> tags = new HashSet<>();

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<TagsDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagsDTO> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostsDTO)) {
            return false;
        }

        PostsDTO postsDTO = (PostsDTO) o;
        if (this.postId == null) {
            return false;
        }
        return Objects.equals(this.postId, postsDTO.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.postId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostsDTO{" +
            "postId=" + getPostId() +
            ", title='" + getTitle() + "'" +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            ", tags=" + getTags() +
            "}";
    }
}
