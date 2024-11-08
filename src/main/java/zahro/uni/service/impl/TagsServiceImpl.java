package zahro.uni.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zahro.uni.domain.Tags;
import zahro.uni.repository.TagsRepository;
import zahro.uni.service.TagsService;
import zahro.uni.service.dto.TagsDTO;
import zahro.uni.service.mapper.TagsMapper;

/**
 * Service Implementation for managing {@link zahro.uni.domain.Tags}.
 */
@Service
@Transactional
public class TagsServiceImpl implements TagsService {

    private static final Logger LOG = LoggerFactory.getLogger(TagsServiceImpl.class);

    private final TagsRepository tagsRepository;

    private final TagsMapper tagsMapper;

    public TagsServiceImpl(TagsRepository tagsRepository, TagsMapper tagsMapper) {
        this.tagsRepository = tagsRepository;
        this.tagsMapper = tagsMapper;
    }

    @Override
    public TagsDTO save(TagsDTO tagsDTO) {
        LOG.debug("Request to save Tags : {}", tagsDTO);
        Tags tags = tagsMapper.toEntity(tagsDTO);
        tags = tagsRepository.save(tags);
        return tagsMapper.toDto(tags);
    }

    @Override
    public TagsDTO update(TagsDTO tagsDTO) {
        LOG.debug("Request to update Tags : {}", tagsDTO);
        Tags tags = tagsMapper.toEntity(tagsDTO);
        tags = tagsRepository.save(tags);
        return tagsMapper.toDto(tags);
    }

    @Override
    public Optional<TagsDTO> partialUpdate(TagsDTO tagsDTO) {
        LOG.debug("Request to partially update Tags : {}", tagsDTO);

        return tagsRepository
            .findById(tagsDTO.getTagId())
            .map(existingTags -> {
                tagsMapper.partialUpdate(existingTags, tagsDTO);

                return existingTags;
            })
            .map(tagsRepository::save)
            .map(tagsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagsDTO> findAll() {
        LOG.debug("Request to get all Tags");
        return tagsRepository.findAll().stream().map(tagsMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagsDTO> findOne(Long id) {
        LOG.debug("Request to get Tags : {}", id);
        return tagsRepository.findById(id).map(tagsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Tags : {}", id);
        tagsRepository.deleteById(id);
    }
}
