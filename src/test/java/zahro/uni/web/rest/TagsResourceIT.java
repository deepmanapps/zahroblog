package zahro.uni.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zahro.uni.domain.TagsAsserts.*;
import static zahro.uni.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import zahro.uni.IntegrationTest;
import zahro.uni.domain.Tags;
import zahro.uni.repository.TagsRepository;
import zahro.uni.service.dto.TagsDTO;
import zahro.uni.service.mapper.TagsMapper;

/**
 * Integration tests for the {@link TagsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TagsResourceIT {

    private static final String DEFAULT_TAG_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TAG_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{tagId}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTagsMockMvc;

    private Tags tags;

    private Tags insertedTags;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tags createEntity() {
        return new Tags().tagName(DEFAULT_TAG_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tags createUpdatedEntity() {
        return new Tags().tagName(UPDATED_TAG_NAME);
    }

    @BeforeEach
    public void initTest() {
        tags = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTags != null) {
            tagsRepository.delete(insertedTags);
            insertedTags = null;
        }
    }

    @Test
    @Transactional
    void createTags() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Tags
        TagsDTO tagsDTO = tagsMapper.toDto(tags);
        var returnedTagsDTO = om.readValue(
            restTagsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagsDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagsDTO.class
        );

        // Validate the Tags in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTags = tagsMapper.toEntity(returnedTagsDTO);
        assertTagsUpdatableFieldsEquals(returnedTags, getPersistedTags(returnedTags));

        insertedTags = returnedTags;
    }

    @Test
    @Transactional
    void createTagsWithExistingId() throws Exception {
        // Create the Tags with an existing ID
        tags.setTagId(1L);
        TagsDTO tagsDTO = tagsMapper.toDto(tags);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTagsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTags() throws Exception {
        // Initialize the database
        insertedTags = tagsRepository.saveAndFlush(tags);

        // Get all the tagsList
        restTagsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=tagId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].tagId").value(hasItem(tags.getTagId().intValue())))
            .andExpect(jsonPath("$.[*].tagName").value(hasItem(DEFAULT_TAG_NAME)));
    }

    @Test
    @Transactional
    void getTags() throws Exception {
        // Initialize the database
        insertedTags = tagsRepository.saveAndFlush(tags);

        // Get the tags
        restTagsMockMvc
            .perform(get(ENTITY_API_URL_ID, tags.getTagId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.tagId").value(tags.getTagId().intValue()))
            .andExpect(jsonPath("$.tagName").value(DEFAULT_TAG_NAME));
    }

    @Test
    @Transactional
    void getNonExistingTags() throws Exception {
        // Get the tags
        restTagsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTags() throws Exception {
        // Initialize the database
        insertedTags = tagsRepository.saveAndFlush(tags);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tags
        Tags updatedTags = tagsRepository.findById(tags.getTagId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTags are not directly saved in db
        em.detach(updatedTags);
        updatedTags.tagName(UPDATED_TAG_NAME);
        TagsDTO tagsDTO = tagsMapper.toDto(updatedTags);

        restTagsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tagsDTO.getTagId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTagsToMatchAllProperties(updatedTags);
    }

    @Test
    @Transactional
    void putNonExistingTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setTagId(longCount.incrementAndGet());

        // Create the Tags
        TagsDTO tagsDTO = tagsMapper.toDto(tags);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tagsDTO.getTagId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setTagId(longCount.incrementAndGet());

        // Create the Tags
        TagsDTO tagsDTO = tagsMapper.toDto(tags);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tagsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setTagId(longCount.incrementAndGet());

        // Create the Tags
        TagsDTO tagsDTO = tagsMapper.toDto(tags);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTagsWithPatch() throws Exception {
        // Initialize the database
        insertedTags = tagsRepository.saveAndFlush(tags);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tags using partial update
        Tags partialUpdatedTags = new Tags();
        partialUpdatedTags.setTagId(tags.getTagId());

        restTagsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTags.getTagId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTags))
            )
            .andExpect(status().isOk());

        // Validate the Tags in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTagsUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTags, tags), getPersistedTags(tags));
    }

    @Test
    @Transactional
    void fullUpdateTagsWithPatch() throws Exception {
        // Initialize the database
        insertedTags = tagsRepository.saveAndFlush(tags);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tags using partial update
        Tags partialUpdatedTags = new Tags();
        partialUpdatedTags.setTagId(tags.getTagId());

        partialUpdatedTags.tagName(UPDATED_TAG_NAME);

        restTagsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTags.getTagId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTags))
            )
            .andExpect(status().isOk());

        // Validate the Tags in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTagsUpdatableFieldsEquals(partialUpdatedTags, getPersistedTags(partialUpdatedTags));
    }

    @Test
    @Transactional
    void patchNonExistingTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setTagId(longCount.incrementAndGet());

        // Create the Tags
        TagsDTO tagsDTO = tagsMapper.toDto(tags);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tagsDTO.getTagId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tagsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setTagId(longCount.incrementAndGet());

        // Create the Tags
        TagsDTO tagsDTO = tagsMapper.toDto(tags);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tagsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setTagId(longCount.incrementAndGet());

        // Create the Tags
        TagsDTO tagsDTO = tagsMapper.toDto(tags);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tagsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTags() throws Exception {
        // Initialize the database
        insertedTags = tagsRepository.saveAndFlush(tags);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tags
        restTagsMockMvc
            .perform(delete(ENTITY_API_URL_ID, tags.getTagId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tagsRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Tags getPersistedTags(Tags tags) {
        return tagsRepository.findById(tags.getTagId()).orElseThrow();
    }

    protected void assertPersistedTagsToMatchAllProperties(Tags expectedTags) {
        assertTagsAllPropertiesEquals(expectedTags, getPersistedTags(expectedTags));
    }

    protected void assertPersistedTagsToMatchUpdatableProperties(Tags expectedTags) {
        assertTagsAllUpdatablePropertiesEquals(expectedTags, getPersistedTags(expectedTags));
    }
}
