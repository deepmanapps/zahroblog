package zahro.uni.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zahro.uni.domain.ReplyComments;

/**
 * Spring Data JPA repository for the ReplyComments entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReplyCommentsRepository extends JpaRepository<ReplyComments, Long> {
    @Query("select replyComments from ReplyComments replyComments where replyComments.user.login = ?#{authentication.name}")
    List<ReplyComments> findByUserIsCurrentUser();
}
