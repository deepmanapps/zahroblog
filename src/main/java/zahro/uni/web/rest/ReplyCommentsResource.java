package zahro.uni.web.rest;

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
import zahro.uni.repository.ReplyCommentsRepository;
import zahro.uni.service.ReplyCommentsService;
import zahro.uni.service.dto.ReplyCommentsDTO;
import zahro.uni.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zahro.uni.domain.ReplyComments}.
 */
@RestController
@RequestMapping("/api/reply-comments")
public class ReplyCommentsResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReplyCommentsResource.class);

    private static final String ENTITY_NAME = "replyComments";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReplyCommentsService replyCommentsService;

    private final ReplyCommentsRepository replyCommentsRepository;

    public ReplyCommentsResource(ReplyCommentsService replyCommentsService, ReplyCommentsRepository replyCommentsRepository) {
        this.replyCommentsService = replyCommentsService;
        this.replyCommentsRepository = replyCommentsRepository;
    }

    /**
     * {@code POST  /reply-comments} : Create a new replyComments.
     *
     * @param replyCommentsDTO the replyCommentsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new replyCommentsDTO, or with status {@code 400 (Bad Request)} if the replyComments has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReplyCommentsDTO> createReplyComments(@RequestBody ReplyCommentsDTO replyCommentsDTO) throws URISyntaxException {
        LOG.debug("REST request to save ReplyComments : {}", replyCommentsDTO);
        if (replyCommentsDTO.getReplyId() != null) {
            throw new BadRequestAlertException("A new replyComments cannot already have an ID", ENTITY_NAME, "idexists");
        }
        replyCommentsDTO = replyCommentsService.save(replyCommentsDTO);
        return ResponseEntity.created(new URI("/api/reply-comments/" + replyCommentsDTO.getReplyId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, replyCommentsDTO.getReplyId().toString()))
            .body(replyCommentsDTO);
    }

    /**
     * {@code PUT  /reply-comments/:replyId} : Updates an existing replyComments.
     *
     * @param replyId the id of the replyCommentsDTO to save.
     * @param replyCommentsDTO the replyCommentsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated replyCommentsDTO,
     * or with status {@code 400 (Bad Request)} if the replyCommentsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the replyCommentsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{replyId}")
    public ResponseEntity<ReplyCommentsDTO> updateReplyComments(
        @PathVariable(value = "replyId", required = false) final Long replyId,
        @RequestBody ReplyCommentsDTO replyCommentsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReplyComments : {}, {}", replyId, replyCommentsDTO);
        if (replyCommentsDTO.getReplyId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(replyId, replyCommentsDTO.getReplyId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!replyCommentsRepository.existsById(replyId)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        replyCommentsDTO = replyCommentsService.update(replyCommentsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, replyCommentsDTO.getReplyId().toString()))
            .body(replyCommentsDTO);
    }

    /**
     * {@code PATCH  /reply-comments/:replyId} : Partial updates given fields of an existing replyComments, field will ignore if it is null
     *
     * @param replyId the id of the replyCommentsDTO to save.
     * @param replyCommentsDTO the replyCommentsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated replyCommentsDTO,
     * or with status {@code 400 (Bad Request)} if the replyCommentsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the replyCommentsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the replyCommentsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{replyId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReplyCommentsDTO> partialUpdateReplyComments(
        @PathVariable(value = "replyId", required = false) final Long replyId,
        @RequestBody ReplyCommentsDTO replyCommentsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReplyComments partially : {}, {}", replyId, replyCommentsDTO);
        if (replyCommentsDTO.getReplyId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(replyId, replyCommentsDTO.getReplyId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!replyCommentsRepository.existsById(replyId)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReplyCommentsDTO> result = replyCommentsService.partialUpdate(replyCommentsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, replyCommentsDTO.getReplyId().toString())
        );
    }

    /**
     * {@code GET  /reply-comments} : get all the replyComments.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of replyComments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReplyCommentsDTO>> getAllReplyComments(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ReplyComments");
        Page<ReplyCommentsDTO> page = replyCommentsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reply-comments/:id} : get the "id" replyComments.
     *
     * @param id the id of the replyCommentsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the replyCommentsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReplyCommentsDTO> getReplyComments(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReplyComments : {}", id);
        Optional<ReplyCommentsDTO> replyCommentsDTO = replyCommentsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(replyCommentsDTO);
    }

    /**
     * {@code DELETE  /reply-comments/:id} : delete the "id" replyComments.
     *
     * @param id the id of the replyCommentsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReplyComments(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReplyComments : {}", id);
        replyCommentsService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
