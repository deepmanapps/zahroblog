package zahro.uni.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zahro.uni.domain.Posts;
import zahro.uni.repository.PostsRepository;
import zahro.uni.service.PostsService;
import zahro.uni.service.dto.PostsDTO;
import zahro.uni.service.mapper.PostsMapper;

/**
 * Service Implementation for managing {@link zahro.uni.domain.Posts}.
 */
@Service
@Transactional
public class PostsServiceImpl implements PostsService {

    private static final Logger LOG = LoggerFactory.getLogger(PostsServiceImpl.class);

    private final PostsRepository postsRepository;

    private final PostsMapper postsMapper;

    public PostsServiceImpl(PostsRepository postsRepository, PostsMapper postsMapper) {
        this.postsRepository = postsRepository;
        this.postsMapper = postsMapper;
    }

    @Override
    public PostsDTO save(PostsDTO postsDTO) {
        LOG.debug("Request to save Posts : {}", postsDTO);
        Posts posts = postsMapper.toEntity(postsDTO);
        posts = postsRepository.save(posts);
        return postsMapper.toDto(posts);
    }

    @Override
    public PostsDTO update(PostsDTO postsDTO) {
        LOG.debug("Request to update Posts : {}", postsDTO);
        Posts posts = postsMapper.toEntity(postsDTO);
        posts = postsRepository.save(posts);
        return postsMapper.toDto(posts);
    }

    @Override
    public Optional<PostsDTO> partialUpdate(PostsDTO postsDTO) {
        LOG.debug("Request to partially update Posts : {}", postsDTO);

        return postsRepository
            .findById(postsDTO.getPostId())
            .map(existingPosts -> {
                postsMapper.partialUpdate(existingPosts, postsDTO);

                return existingPosts;
            })
            .map(postsRepository::save)
            .map(postsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostsDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Posts");
        return postsRepository.findAll(pageable).map(postsMapper::toDto);
    }

    public Page<PostsDTO> findAllWithEagerRelationships(Pageable pageable) {
        return postsRepository.findAllWithEagerRelationships(pageable).map(postsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostsDTO> findOne(Long id) {
        LOG.debug("Request to get Posts : {}", id);
        return postsRepository.findOneWithEagerRelationships(id).map(postsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Posts : {}", id);
        postsRepository.deleteById(id);
    }
}
