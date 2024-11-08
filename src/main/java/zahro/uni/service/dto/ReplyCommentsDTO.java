package zahro.uni.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link zahro.uni.domain.ReplyComments} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReplyCommentsDTO implements Serializable {

    private Long replyId;

    private String content;

    private ZonedDateTime createdAt;

    private CommentsDTO parentCommentId;

    private UserDTO user;

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
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

    public CommentsDTO getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(CommentsDTO parentCommentId) {
        this.parentCommentId = parentCommentId;
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
        if (!(o instanceof ReplyCommentsDTO)) {
            return false;
        }

        ReplyCommentsDTO replyCommentsDTO = (ReplyCommentsDTO) o;
        if (this.replyId == null) {
            return false;
        }
        return Objects.equals(this.replyId, replyCommentsDTO.replyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.replyId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReplyCommentsDTO{" +
            "replyId=" + getReplyId() +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", parentCommentId=" + getParentCommentId() +
            ", user=" + getUser() +
            "}";
    }
}
