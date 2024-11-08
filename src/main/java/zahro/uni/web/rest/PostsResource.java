package zahro.uni.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import zahro.uni.repository.PostsRepository;
import zahro.uni.service.PostsService;
import zahro.uni.service.dto.PostsDTO;
import zahro.uni.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zahro.uni.domain.Posts}.
 */
@RestController
@RequestMapping("/api/posts")
public class PostsResource {

    private static final Logger LOG = LoggerFactory.getLogger(PostsResource.class);

    private static final String ENTITY_NAME = "posts";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostsService postsService;

    private final PostsRepository postsRepository;

    public PostsResource(PostsService postsService, PostsRepository postsRepository) {
        this.postsService = postsService;
        this.postsRepository = postsRepository;
    }

    /**
     * {@code POST  /posts} : Create a new posts.
     *
     * @param postsDTO the postsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postsDTO, or with status {@code 400 (Bad Request)} if the posts has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PostsDTO> createPosts(@Valid @RequestBody PostsDTO postsDTO) throws URISyntaxException {
        LOG.debug("REST request to save Posts : {}", postsDTO);
        if (postsDTO.getPostId() != null) {
            throw new BadRequestAlertException("A new posts cannot already have an ID", ENTITY_NAME, "idexists");
        }
        postsDTO = postsService.save(postsDTO);
        return ResponseEntity.created(new URI("/api/posts/" + postsDTO.getPostId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, postsDTO.getPostId().toString()))
            .body(postsDTO);
    }

    /**
     * {@code PUT  /posts/:postId} : Updates an existing posts.
     *
     * @param postId the id of the postsDTO to save.
     * @param postsDTO the postsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postsDTO,
     * or with status {@code 400 (Bad Request)} if the postsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostsDTO> updatePosts(
        @PathVariable(value = "postId", required = false) final Long postId,
        @Valid @RequestBody PostsDTO postsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Posts : {}, {}", postId, postsDTO);
        if (postsDTO.getPostId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(postId, postsDTO.getPostId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postsRepository.existsById(postId)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        postsDTO = postsService.update(postsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postsDTO.getPostId().toString()))
            .body(postsDTO);
    }

    /**
     * {@code PATCH  /posts/:postId} : Partial updates given fields of an existing posts, field will ignore if it is null
     *
     * @param postId the id of the postsDTO to save.
     * @param postsDTO the postsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postsDTO,
     * or with status {@code 400 (Bad Request)} if the postsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the postsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the postsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{postId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PostsDTO> partialUpdatePosts(
        @PathVariable(value = "postId", required = false) final Long postId,
        @NotNull @RequestBody PostsDTO postsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Posts partially : {}, {}", postId, postsDTO);
        if (postsDTO.getPostId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(postId, postsDTO.getPostId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postsRepository.existsById(postId)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PostsDTO> result = postsService.partialUpdate(postsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postsDTO.getPostId().toString())
        );
    }

    /**
     * {@code GET  /posts} : get all the posts.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of posts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PostsDTO>> getAllPosts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Posts");
        Page<PostsDTO> page;
        if (eagerload) {
            page = postsService.findAllWithEagerRelationships(pageable);
        } else {
            page = postsService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /posts/:id} : get the "id" posts.
     *
     * @param id the id of the postsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostsDTO> getPosts(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Posts : {}", id);
        Optional<PostsDTO> postsDTO = postsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postsDTO);
    }

    /**
     * {@code DELETE  /posts/:id} : delete the "id" posts.
     *
     * @param id the id of the postsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosts(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Posts : {}", id);
        postsService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
