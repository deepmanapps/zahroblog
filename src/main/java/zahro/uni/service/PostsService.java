package zahro.uni.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zahro.uni.service.dto.PostsDTO;

/**
 * Service Interface for managing {@link zahro.uni.domain.Posts}.
 */
public interface PostsService {
    /**
     * Save a posts.
     *
     * @param postsDTO the entity to save.
     * @return the persisted entity.
     */
    PostsDTO save(PostsDTO postsDTO);

    /**
     * Updates a posts.
     *
     * @param postsDTO the entity to update.
     * @return the persisted entity.
     */
    PostsDTO update(PostsDTO postsDTO);

    /**
     * Partially updates a posts.
     *
     * @param postsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PostsDTO> partialUpdate(PostsDTO postsDTO);

    /**
     * Get all the posts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PostsDTO> findAll(Pageable pageable);

    /**
     * Get all the posts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PostsDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" posts.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PostsDTO> findOne(Long id);

    /**
     * Delete the "id" posts.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
