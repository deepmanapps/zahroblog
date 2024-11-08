package zahro.uni.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Posts.
 */
@Entity
@Table(name = "posts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Posts implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_posts__tags",
        joinColumns = @JoinColumn(name = "posts_post_id"),
        inverseJoinColumns = @JoinColumn(name = "tags_tag_id")
    )
    @JsonIgnoreProperties(value = { "posts" }, allowSetters = true)
    private Set<Tags> tags = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "posts")
    @JsonIgnoreProperties(value = { "posts", "user" }, allowSetters = true)
    private Set<Comments> comments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getPostId() {
        return this.postId;
    }

    public Posts postId(Long postId) {
        this.setPostId(postId);
        return this;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return this.title;
    }

    public Posts title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public Posts content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Posts createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Posts user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Tags> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tags> tags) {
        this.tags = tags;
    }

    public Posts tags(Set<Tags> tags) {
        this.setTags(tags);
        return this;
    }

    public Posts addTags(Tags tags) {
        this.tags.add(tags);
        return this;
    }

    public Posts removeTags(Tags tags) {
        this.tags.remove(tags);
        return this;
    }

    public Set<Comments> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comments> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setPosts(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setPosts(this));
        }
        this.comments = comments;
    }

    public Posts comments(Set<Comments> comments) {
        this.setComments(comments);
        return this;
    }

    public Posts addComments(Comments comments) {
        this.comments.add(comments);
        comments.setPosts(this);
        return this;
    }

    public Posts removeComments(Comments comments) {
        this.comments.remove(comments);
        comments.setPosts(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Posts)) {
            return false;
        }
        return getPostId() != null && getPostId().equals(((Posts) o).getPostId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Posts{" +
            "postId=" + getPostId() +
            ", title='" + getTitle() + "'" +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
