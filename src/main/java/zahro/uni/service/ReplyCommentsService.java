package zahro.uni.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zahro.uni.service.dto.ReplyCommentsDTO;

/**
 * Service Interface for managing {@link zahro.uni.domain.ReplyComments}.
 */
public interface ReplyCommentsService {
    /**
     * Save a replyComments.
     *
     * @param replyCommentsDTO the entity to save.
     * @return the persisted entity.
     */
    ReplyCommentsDTO save(ReplyCommentsDTO replyCommentsDTO);

    /**
     * Updates a replyComments.
     *
     * @param replyCommentsDTO the entity to update.
     * @return the persisted entity.
     */
    ReplyCommentsDTO update(ReplyCommentsDTO replyCommentsDTO);

    /**
     * Partially updates a replyComments.
     *
     * @param replyCommentsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReplyCommentsDTO> partialUpdate(ReplyCommentsDTO replyCommentsDTO);

    /**
     * Get all the replyComments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReplyCommentsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" replyComments.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReplyCommentsDTO> findOne(Long id);

    /**
     * Delete the "id" replyComments.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
