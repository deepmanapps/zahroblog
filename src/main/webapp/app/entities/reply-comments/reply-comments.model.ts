import dayjs from 'dayjs/esm';
import { IComments } from 'app/entities/comments/comments.model';
import { IUser } from 'app/entities/user/user.model';

export interface IReplyComments {
  replyId: number;
  content?: string | null;
  createdAt?: dayjs.Dayjs | null;
  parentCommentId?: Pick<IComments, 'commentId'> | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewReplyComments = Omit<IReplyComments, 'replyId'> & { replyId: null };
