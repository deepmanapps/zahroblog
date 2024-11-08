package zahro.uni.service.mapper;

import org.mapstruct.*;
import zahro.uni.domain.Comments;
import zahro.uni.domain.Posts;
import zahro.uni.domain.User;
import zahro.uni.service.dto.CommentsDTO;
import zahro.uni.service.dto.PostsDTO;
import zahro.uni.service.dto.UserDTO;

/**
 * Mapper for the entity {@link Comments} and its DTO {@link CommentsDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentsMapper extends EntityMapper<CommentsDTO, Comments> {
    @Mapping(target = "posts", source = "posts", qualifiedByName = "postsPostId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    CommentsDTO toDto(Comments s);

    @Named("postsPostId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "postId", source = "postId")
    PostsDTO toDtoPostsPostId(Posts posts);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
