package zahro.uni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import zahro.uni.domain.Posts;

public interface PostsRepositoryWithBagRelationships {
    Optional<Posts> fetchBagRelationships(Optional<Posts> posts);

    List<Posts> fetchBagRelationships(List<Posts> posts);

    Page<Posts> fetchBagRelationships(Page<Posts> posts);
}
