package zahro.uni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zahro.uni.domain.Posts;

/**
 * Spring Data JPA repository for the Posts entity.
 *
 * When extending this class, extend PostsRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface PostsRepository extends PostsRepositoryWithBagRelationships, JpaRepository<Posts, Long> {
    @Query("select posts from Posts posts where posts.user.login = ?#{authentication.name}")
    List<Posts> findByUserIsCurrentUser();

    default Optional<Posts> findOneWithEagerRelationships(Long postId) {
        return this.fetchBagRelationships(this.findById(postId));
    }

    default List<Posts> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Posts> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
