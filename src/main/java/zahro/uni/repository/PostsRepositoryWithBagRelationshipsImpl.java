package zahro.uni.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import zahro.uni.domain.Posts;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PostsRepositoryWithBagRelationshipsImpl implements PostsRepositoryWithBagRelationships {

    private static final String POSTID_PARAMETER = "postId";
    private static final String POSTS_PARAMETER = "posts";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Posts> fetchBagRelationships(Optional<Posts> posts) {
        return posts.map(this::fetchTags);
    }

    @Override
    public Page<Posts> fetchBagRelationships(Page<Posts> posts) {
        return new PageImpl<>(fetchBagRelationships(posts.getContent()), posts.getPageable(), posts.getTotalElements());
    }

    @Override
    public List<Posts> fetchBagRelationships(List<Posts> posts) {
        return Optional.of(posts).map(this::fetchTags).orElse(Collections.emptyList());
    }

    Posts fetchTags(Posts result) {
        return entityManager
            .createQuery("select posts from Posts posts left join fetch posts.tags where posts.postId = :postId", Posts.class)
            .setParameter(POSTID_PARAMETER, result.getPostId())
            .getSingleResult();
    }

    List<Posts> fetchTags(List<Posts> posts) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, posts.size()).forEach(index -> order.put(posts.get(index).getPostId(), index));
        List<Posts> result = entityManager
            .createQuery("select posts from Posts posts left join fetch posts.tags where posts in :posts", Posts.class)
            .setParameter(POSTS_PARAMETER, posts)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getPostId()), order.get(o2.getPostId())));
        return result;
    }
}
