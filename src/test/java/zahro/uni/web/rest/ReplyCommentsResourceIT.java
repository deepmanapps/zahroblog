package zahro.uni.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zahro.uni.domain.ReplyCommentsAsserts.*;
import static zahro.uni.web.rest.TestUtil.createUpdateProxyForBean;
import static zahro.uni.web.rest.TestUtil.sameInstant;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import zahro.uni.domain.ReplyComments;
import zahro.uni.repository.ReplyCommentsRepository;
import zahro.uni.repository.UserRepository;
import zahro.uni.service.dto.ReplyCommentsDTO;
import zahro.uni.service.mapper.ReplyCommentsMapper;

/**
 * Integration tests for the {@link ReplyCommentsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReplyCommentsResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/reply-comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{replyId}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReplyCommentsRepository replyCommentsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReplyCommentsMapper replyCommentsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReplyCommentsMockMvc;

    private ReplyComments replyComments;

    private ReplyComments insertedReplyComments;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReplyComments createEntity() {
        return new ReplyComments().content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReplyComments createUpdatedEntity() {
        return new ReplyComments().content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        replyComments = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReplyComments != null) {
            replyCommentsRepository.delete(insertedReplyComments);
            insertedReplyComments = null;
        }
    }

    @Test
    @Transactional
    void createReplyComments() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReplyComments
        ReplyCommentsDTO replyCommentsDTO = replyCommentsMapper.toDto(replyComments);
        var returnedReplyCommentsDTO = om.readValue(
            restReplyCommentsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(replyCommentsDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReplyCommentsDTO.class
        );

        // Validate the ReplyComments in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReplyComments = replyCommentsMapper.toEntity(returnedReplyCommentsDTO);
        assertReplyCommentsUpdatableFieldsEquals(returnedReplyComments, getPersistedReplyComments(returnedReplyComments));

        insertedReplyComments = returnedReplyComments;
    }

    @Test
    @Transactional
    void createReplyCommentsWithExistingId() throws Exception {
        // Create the ReplyComments with an existing ID
        replyComments.setReplyId(1L);
        ReplyCommentsDTO replyCommentsDTO = replyCommentsMapper.toDto(replyComments);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReplyCommentsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(replyCommentsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReplyComments in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllReplyComments() throws Exception {
        // Initialize the database
        insertedReplyComments = replyCommentsRepository.saveAndFlush(replyComments);

        // Get all the replyCommentsList
        restReplyCommentsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=replyId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].replyId").value(hasItem(replyComments.getReplyId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @Test
    @Transactional
    void getReplyComments() throws Exception {
        // Initialize the database
        insertedReplyComments = replyCommentsRepository.saveAndFlush(replyComments);

        // Get the replyComments
        restReplyCommentsMockMvc
            .perform(get(ENTITY_API_URL_ID, replyComments.getReplyId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.replyId").value(replyComments.getReplyId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingReplyComments() throws Exception {
        // Get the replyComments
        restReplyCommentsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReplyComments() throws Exception {
        // Initialize the database
        insertedReplyComments = replyCommentsRepository.saveAndFlush(replyComments);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the replyComments
        ReplyComments updatedReplyComments = replyCommentsRepository.findById(replyComments.getReplyId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReplyComments are not directly saved in db
        em.detach(updatedReplyComments);
        updatedReplyComments.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        ReplyCommentsDTO replyCommentsDTO = replyCommentsMapper.toDto(updatedReplyComments);

        restReplyCommentsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, replyCommentsDTO.getReplyId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(replyCommentsDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReplyComments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReplyCommentsToMatchAllProperties(updatedReplyComments);
    }

    @Test
    @Transactional
    void putNonExistingReplyComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        replyComments.setReplyId(longCount.incrementAndGet());

        // Create the ReplyComments
        ReplyCommentsDTO replyCommentsDTO = replyCommentsMapper.toDto(replyComments);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReplyCommentsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, replyCommentsDTO.getReplyId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(replyCommentsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReplyComments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReplyComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        replyComments.setReplyId(longCount.incrementAndGet());

        // Create the ReplyComments
        ReplyCommentsDTO replyCommentsDTO = replyCommentsMapper.toDto(replyComments);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReplyCommentsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(replyCommentsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReplyComments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReplyComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        replyComments.setReplyId(longCount.incrementAndGet());

        // Create the ReplyComments
        ReplyCommentsDTO replyCommentsDTO = replyCommentsMapper.toDto(replyComments);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReplyCommentsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(replyCommentsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReplyComments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReplyCommentsWithPatch() throws Exception {
        // Initialize the database
        insertedReplyComments = replyCommentsRepository.saveAndFlush(replyComments);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the replyComments using partial update
        ReplyComments partialUpdatedReplyComments = new ReplyComments();
        partialUpdatedReplyComments.setReplyId(replyComments.getReplyId());

        partialUpdatedReplyComments.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restReplyCommentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReplyComments.getReplyId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReplyComments))
            )
            .andExpect(status().isOk());

        // Validate the ReplyComments in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReplyCommentsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReplyComments, replyComments),
            getPersistedReplyComments(replyComments)
        );
    }

    @Test
    @Transactional
    void fullUpdateReplyCommentsWithPatch() throws Exception {
        // Initialize the database
        insertedReplyComments = replyCommentsRepository.saveAndFlush(replyComments);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the replyComments using partial update
        ReplyComments partialUpdatedReplyComments = new ReplyComments();
        partialUpdatedReplyComments.setReplyId(replyComments.getReplyId());

        partialUpdatedReplyComments.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restReplyCommentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReplyComments.getReplyId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReplyComments))
            )
            .andExpect(status().isOk());

        // Validate the ReplyComments in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReplyCommentsUpdatableFieldsEquals(partialUpdatedReplyComments, getPersistedReplyComments(partialUpdatedReplyComments));
    }

    @Test
    @Transactional
    void patchNonExistingReplyComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        replyComments.setReplyId(longCount.incrementAndGet());

        // Create the ReplyComments
        ReplyCommentsDTO replyCommentsDTO = replyCommentsMapper.toDto(replyComments);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReplyCommentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, replyCommentsDTO.getReplyId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(replyCommentsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReplyComments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReplyComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        replyComments.setReplyId(longCount.incrementAndGet());

        // Create the ReplyComments
        ReplyCommentsDTO replyCommentsDTO = replyCommentsMapper.toDto(replyComments);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReplyCommentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(replyCommentsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReplyComments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReplyComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        replyComments.setReplyId(longCount.incrementAndGet());

        // Create the ReplyComments
        ReplyCommentsDTO replyCommentsDTO = replyCommentsMapper.toDto(replyComments);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReplyCommentsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(replyCommentsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReplyComments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReplyComments() throws Exception {
        // Initialize the database
        insertedReplyComments = replyCommentsRepository.saveAndFlush(replyComments);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the replyComments
        restReplyCommentsMockMvc
            .perform(delete(ENTITY_API_URL_ID, replyComments.getReplyId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return replyCommentsRepository.count();
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

    protected ReplyComments getPersistedReplyComments(ReplyComments replyComments) {
        return replyCommentsRepository.findById(replyComments.getReplyId()).orElseThrow();
    }

    protected void assertPersistedReplyCommentsToMatchAllProperties(ReplyComments expectedReplyComments) {
        assertReplyCommentsAllPropertiesEquals(expectedReplyComments, getPersistedReplyComments(expectedReplyComments));
    }

    protected void assertPersistedReplyCommentsToMatchUpdatableProperties(ReplyComments expectedReplyComments) {
        assertReplyCommentsAllUpdatablePropertiesEquals(expectedReplyComments, getPersistedReplyComments(expectedReplyComments));
    }
}
