package zahro.uni.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Tags.
 */
@Entity
@Table(name = "tags")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tags implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "tag_name")
    private String tagName;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    @JsonIgnoreProperties(value = { "user", "tags", "comments" }, allowSetters = true)
    private Set<Posts> posts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getTagId() {
        return this.tagId;
    }

    public Tags tagId(Long tagId) {
        this.setTagId(tagId);
        return this;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return this.tagName;
    }

    public Tags tagName(String tagName) {
        this.setTagName(tagName);
        return this;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Set<Posts> getPosts() {
        return this.posts;
    }

    public void setPosts(Set<Posts> posts) {
        if (this.posts != null) {
            this.posts.forEach(i -> i.removeTags(this));
        }
        if (posts != null) {
            posts.forEach(i -> i.addTags(this));
        }
        this.posts = posts;
    }

    public Tags posts(Set<Posts> posts) {
        this.setPosts(posts);
        return this;
    }

    public Tags addPosts(Posts posts) {
        this.posts.add(posts);
        posts.getTags().add(this);
        return this;
    }

    public Tags removePosts(Posts posts) {
        this.posts.remove(posts);
        posts.getTags().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tags)) {
            return false;
        }
        return getTagId() != null && getTagId().equals(((Tags) o).getTagId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tags{" +
            "tagId=" + getTagId() +
            ", tagName='" + getTagName() + "'" +
            "}";
    }
}
