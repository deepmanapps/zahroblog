package zahro.uni.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zahro.uni.domain.Tags;

/**
 * Spring Data JPA repository for the Tags entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {}
