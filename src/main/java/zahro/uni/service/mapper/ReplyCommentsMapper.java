package zahro.uni.service.mapper;

import org.mapstruct.*;
import zahro.uni.domain.Comments;
import zahro.uni.domain.ReplyComments;
import zahro.uni.domain.User;
import zahro.uni.service.dto.CommentsDTO;
import zahro.uni.service.dto.ReplyCommentsDTO;
import zahro.uni.service.dto.UserDTO;

/**
 * Mapper for the entity {@link ReplyComments} and its DTO {@link ReplyCommentsDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReplyCommentsMapper extends EntityMapper<ReplyCommentsDTO, ReplyComments> {
    @Mapping(target = "parentCommentId", source = "parentCommentId", qualifiedByName = "commentsCommentId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    ReplyCommentsDTO toDto(ReplyComments s);

    @Named("commentsCommentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "commentId", source = "commentId")
    CommentsDTO toDtoCommentsCommentId(Comments comments);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
