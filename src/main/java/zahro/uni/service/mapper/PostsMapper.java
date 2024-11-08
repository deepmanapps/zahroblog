package zahro.uni.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import zahro.uni.domain.Posts;
import zahro.uni.domain.Tags;
import zahro.uni.domain.User;
import zahro.uni.service.dto.PostsDTO;
import zahro.uni.service.dto.TagsDTO;
import zahro.uni.service.dto.UserDTO;

/**
 * Mapper for the entity {@link Posts} and its DTO {@link PostsDTO}.
 */
@Mapper(componentModel = "spring")
public interface PostsMapper extends EntityMapper<PostsDTO, Posts> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsTagIdSet")
    PostsDTO toDto(Posts s);

    @Mapping(target = "removeTags", ignore = true)
    Posts toEntity(PostsDTO postsDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("tagsTagId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "tagId", source = "tagId")
    TagsDTO toDtoTagsTagId(Tags tags);

    @Named("tagsTagIdSet")
    default Set<TagsDTO> toDtoTagsTagIdSet(Set<Tags> tags) {
        return tags.stream().map(this::toDtoTagsTagId).collect(Collectors.toSet());
    }
}
