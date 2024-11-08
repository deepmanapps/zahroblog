package zahro.uni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zahro.uni.domain.Comments;

/**
 * Spring Data JPA repository for the Comments entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    @Query("select comments from Comments comments where comments.user.login = ?#{authentication.name}")
    List<Comments> findByUserIsCurrentUser();
}
