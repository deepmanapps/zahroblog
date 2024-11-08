package zahro.uni.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link zahro.uni.domain.Comments} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommentsDTO implements Serializable {

    private Long commentId;

    private String content;

    private ZonedDateTime createdAt;

    private PostsDTO posts;

    private UserDTO user;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
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

    public PostsDTO getPosts() {
        return posts;
    }

    public void setPosts(PostsDTO posts) {
        this.posts = posts;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommentsDTO)) {
            return false;
        }

        CommentsDTO commentsDTO = (CommentsDTO) o;
        if (this.commentId == null) {
            return false;
        }
        return Objects.equals(this.commentId, commentsDTO.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.commentId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommentsDTO{" +
            "commentId=" + getCommentId() +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", posts=" + getPosts() +
            ", user=" + getUser() +
            "}";
    }
}
