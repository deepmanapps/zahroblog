package zahro.uni.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zahro.uni.domain.PostsAsserts.*;
import static zahro.uni.web.rest.TestUtil.createUpdateProxyForBean;
import static zahro.uni.web.rest.TestUtil.sameInstant;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import zahro.uni.IntegrationTest;
import zahro.uni.domain.Posts;
import zahro.uni.repository.PostsRepository;
import zahro.uni.repository.UserRepository;
import zahro.uni.service.PostsService;
import zahro.uni.service.dto.PostsDTO;
import zahro.uni.service.mapper.PostsMapper;

/**
 * Integration tests for the {@link PostsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PostsResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{postId}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private PostsRepository postsRepositoryMock;

    @Autowired
    private PostsMapper postsMapper;

    @Mock
    private PostsService postsServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostsMockMvc;

    private Posts posts;

    private Posts insertedPosts;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posts createEntity() {
        return new Posts().title(DEFAULT_TITLE).content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posts createUpdatedEntity() {
        return new Posts().title(UPDATED_TITLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        posts = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPosts != null) {
            postsRepository.delete(insertedPosts);
            insertedPosts = null;
        }
    }

    @Test
    @Transactional
    void createPosts() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Posts
        PostsDTO postsDTO = postsMapper.toDto(posts);
        var returnedPostsDTO = om.readValue(
            restPostsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postsDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PostsDTO.class
        );

        // Validate the Posts in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPosts = postsMapper.toEntity(returnedPostsDTO);
        assertPostsUpdatableFieldsEquals(returnedPosts, getPersistedPosts(returnedPosts));

        insertedPosts = returnedPosts;
    }

    @Test
    @Transactional
    void createPostsWithExistingId() throws Exception {
        // Create the Posts with an existing ID
        posts.setPostId(1L);
        PostsDTO postsDTO = postsMapper.toDto(posts);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPosts() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        // Get all the postsList
        restPostsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=postId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].postId").value(hasItem(posts.getPostId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostsWithEagerRelationshipsIsEnabled() throws Exception {
        when(postsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPostsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(postsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(postsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPostsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(postsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPosts() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        // Get the posts
        restPostsMockMvc
            .perform(get(ENTITY_API_URL_ID, posts.getPostId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.postId").value(posts.getPostId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingPosts() throws Exception {
        // Get the posts
        restPostsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPosts() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the posts
        Posts updatedPosts = postsRepository.findById(posts.getPostId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPosts are not directly saved in db
        em.detach(updatedPosts);
        updatedPosts.title(UPDATED_TITLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
        PostsDTO postsDTO = postsMapper.toDto(updatedPosts);

        restPostsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postsDTO.getPostId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPostsToMatchAllProperties(updatedPosts);
    }

    @Test
    @Transactional
    void putNonExistingPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setPostId(longCount.incrementAndGet());

        // Create the Posts
        PostsDTO postsDTO = postsMapper.toDto(posts);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postsDTO.getPostId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setPostId(longCount.incrementAndGet());

        // Create the Posts
        PostsDTO postsDTO = postsMapper.toDto(posts);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setPostId(longCount.incrementAndGet());

        // Create the Posts
        PostsDTO postsDTO = postsMapper.toDto(posts);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostsWithPatch() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the posts using partial update
        Posts partialUpdatedPosts = new Posts();
        partialUpdatedPosts.setPostId(posts.getPostId());

        partialUpdatedPosts.title(UPDATED_TITLE).content(UPDATED_CONTENT);

        restPostsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPosts.getPostId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPosts))
            )
            .andExpect(status().isOk());

        // Validate the Posts in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostsUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPosts, posts), getPersistedPosts(posts));
    }

    @Test
    @Transactional
    void fullUpdatePostsWithPatch() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the posts using partial update
        Posts partialUpdatedPosts = new Posts();
        partialUpdatedPosts.setPostId(posts.getPostId());

        partialUpdatedPosts.title(UPDATED_TITLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        restPostsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPosts.getPostId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPosts))
            )
            .andExpect(status().isOk());

        // Validate the Posts in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostsUpdatableFieldsEquals(partialUpdatedPosts, getPersistedPosts(partialUpdatedPosts));
    }

    @Test
    @Transactional
    void patchNonExistingPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setPostId(longCount.incrementAndGet());

        // Create the Posts
        PostsDTO postsDTO = postsMapper.toDto(posts);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, postsDTO.getPostId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setPostId(longCount.incrementAndGet());

        // Create the Posts
        PostsDTO postsDTO = postsMapper.toDto(posts);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPosts() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        posts.setPostId(longCount.incrementAndGet());

        // Create the Posts
        PostsDTO postsDTO = postsMapper.toDto(posts);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(postsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Posts in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePosts() throws Exception {
        // Initialize the database
        insertedPosts = postsRepository.saveAndFlush(posts);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the posts
        restPostsMockMvc
            .perform(delete(ENTITY_API_URL_ID, posts.getPostId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return postsRepository.count();
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

    protected Posts getPersistedPosts(Posts posts) {
        return postsRepository.findById(posts.getPostId()).orElseThrow();
    }

    protected void assertPersistedPostsToMatchAllProperties(Posts expectedPosts) {
        assertPostsAllPropertiesEquals(expectedPosts, getPersistedPosts(expectedPosts));
    }

    protected void assertPersistedPostsToMatchUpdatableProperties(Posts expectedPosts) {
        assertPostsAllUpdatablePropertiesEquals(expectedPosts, getPersistedPosts(expectedPosts));
    }
}
