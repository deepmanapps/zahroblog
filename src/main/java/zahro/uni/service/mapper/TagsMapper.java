package zahro.uni.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import zahro.uni.domain.Posts;
import zahro.uni.domain.Tags;
import zahro.uni.service.dto.PostsDTO;
import zahro.uni.service.dto.TagsDTO;

/**
 * Mapper for the entity {@link Tags} and its DTO {@link TagsDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagsMapper extends EntityMapper<TagsDTO, Tags> {
    @Mapping(target = "posts", source = "posts", qualifiedByName = "postsPostIdSet")
    TagsDTO toDto(Tags s);

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "removePosts", ignore = true)
    Tags toEntity(TagsDTO tagsDTO);

    @Named("postsPostId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "postId", source = "postId")
    PostsDTO toDtoPostsPostId(Posts posts);

    @Named("postsPostIdSet")
    default Set<PostsDTO> toDtoPostsPostIdSet(Set<Posts> posts) {
        return posts.stream().map(this::toDtoPostsPostId).collect(Collectors.toSet());
    }
}
