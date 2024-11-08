package zahro.uni.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zahro.uni.domain.Comments;
import zahro.uni.repository.CommentsRepository;
import zahro.uni.service.CommentsService;
import zahro.uni.service.dto.CommentsDTO;
import zahro.uni.service.mapper.CommentsMapper;

/**
 * Service Implementation for managing {@link zahro.uni.domain.Comments}.
 */
@Service
@Transactional
public class CommentsServiceImpl implements CommentsService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentsServiceImpl.class);

    private final CommentsRepository commentsRepository;

    private final CommentsMapper commentsMapper;

    public CommentsServiceImpl(CommentsRepository commentsRepository, CommentsMapper commentsMapper) {
        this.commentsRepository = commentsRepository;
        this.commentsMapper = commentsMapper;
    }

    @Override
    public CommentsDTO save(CommentsDTO commentsDTO) {
        LOG.debug("Request to save Comments : {}", commentsDTO);
        Comments comments = commentsMapper.toEntity(commentsDTO);
        comments = commentsRepository.save(comments);
        return commentsMapper.toDto(comments);
    }

    @Override
    public CommentsDTO update(CommentsDTO commentsDTO) {
        LOG.debug("Request to update Comments : {}", commentsDTO);
        Comments comments = commentsMapper.toEntity(commentsDTO);
        comments = commentsRepository.save(comments);
        return commentsMapper.toDto(comments);
    }

    @Override
    public Optional<CommentsDTO> partialUpdate(CommentsDTO commentsDTO) {
        LOG.debug("Request to partially update Comments : {}", commentsDTO);

        return commentsRepository
            .findById(commentsDTO.getCommentId())
            .map(existingComments -> {
                commentsMapper.partialUpdate(existingComments, commentsDTO);

                return existingComments;
            })
            .map(commentsRepository::save)
            .map(commentsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentsDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Comments");
        return commentsRepository.findAll(pageable).map(commentsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentsDTO> findOne(Long id) {
        LOG.debug("Request to get Comments : {}", id);
        return commentsRepository.findById(id).map(commentsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Comments : {}", id);
        commentsRepository.deleteById(id);
    }
}
