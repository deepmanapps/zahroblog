package zahro.uni.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zahro.uni.domain.ReplyComments;
import zahro.uni.repository.ReplyCommentsRepository;
import zahro.uni.service.ReplyCommentsService;
import zahro.uni.service.dto.ReplyCommentsDTO;
import zahro.uni.service.mapper.ReplyCommentsMapper;

/**
 * Service Implementation for managing {@link zahro.uni.domain.ReplyComments}.
 */
@Service
@Transactional
public class ReplyCommentsServiceImpl implements ReplyCommentsService {

    private static final Logger LOG = LoggerFactory.getLogger(ReplyCommentsServiceImpl.class);

    private final ReplyCommentsRepository replyCommentsRepository;

    private final ReplyCommentsMapper replyCommentsMapper;

    public ReplyCommentsServiceImpl(ReplyCommentsRepository replyCommentsRepository, ReplyCommentsMapper replyCommentsMapper) {
        this.replyCommentsRepository = replyCommentsRepository;
        this.replyCommentsMapper = replyCommentsMapper;
    }

    @Override
    public ReplyCommentsDTO save(ReplyCommentsDTO replyCommentsDTO) {
        LOG.debug("Request to save ReplyComments : {}", replyCommentsDTO);
        ReplyComments replyComments = replyCommentsMapper.toEntity(replyCommentsDTO);
        replyComments = replyCommentsRepository.save(replyComments);
        return replyCommentsMapper.toDto(replyComments);
    }

    @Override
    public ReplyCommentsDTO update(ReplyCommentsDTO replyCommentsDTO) {
        LOG.debug("Request to update ReplyComments : {}", replyCommentsDTO);
        ReplyComments replyComments = replyCommentsMapper.toEntity(replyCommentsDTO);
        replyComments = replyCommentsRepository.save(replyComments);
        return replyCommentsMapper.toDto(replyComments);
    }

    @Override
    public Optional<ReplyCommentsDTO> partialUpdate(ReplyCommentsDTO replyCommentsDTO) {
        LOG.debug("Request to partially update ReplyComments : {}", replyCommentsDTO);

        return replyCommentsRepository
            .findById(replyCommentsDTO.getReplyId())
            .map(existingReplyComments -> {
                replyCommentsMapper.partialUpdate(existingReplyComments, replyCommentsDTO);

                return existingReplyComments;
            })
            .map(replyCommentsRepository::save)
            .map(replyCommentsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReplyCommentsDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ReplyComments");
        return replyCommentsRepository.findAll(pageable).map(replyCommentsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReplyCommentsDTO> findOne(Long id) {
        LOG.debug("Request to get ReplyComments : {}", id);
        return replyCommentsRepository.findById(id).map(replyCommentsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ReplyComments : {}", id);
        replyCommentsRepository.deleteById(id);
    }
}
