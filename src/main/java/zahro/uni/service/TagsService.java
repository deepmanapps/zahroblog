package zahro.uni.service;

import java.util.List;
import java.util.Optional;
import zahro.uni.service.dto.TagsDTO;

/**
 * Service Interface for managing {@link zahro.uni.domain.Tags}.
 */
public interface TagsService {
    /**
     * Save a tags.
     *
     * @param tagsDTO the entity to save.
     * @return the persisted entity.
     */
    TagsDTO save(TagsDTO tagsDTO);

    /**
     * Updates a tags.
     *
     * @param tagsDTO the entity to update.
     * @return the persisted entity.
     */
    TagsDTO update(TagsDTO tagsDTO);

    /**
     * Partially updates a tags.
     *
     * @param tagsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TagsDTO> partialUpdate(TagsDTO tagsDTO);

    /**
     * Get all the tags.
     *
     * @return the list of entities.
     */
    List<TagsDTO> findAll();

    /**
     * Get the "id" tags.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TagsDTO> findOne(Long id);

    /**
     * Delete the "id" tags.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
