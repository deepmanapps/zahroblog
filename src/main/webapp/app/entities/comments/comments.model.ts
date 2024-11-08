import dayjs from 'dayjs/esm';
import { IPosts } from 'app/entities/posts/posts.model';
import { IUser } from 'app/entities/user/user.model';

export interface IComments {
  commentId: number;
  content?: string | null;
  createdAt?: dayjs.Dayjs | null;
  posts?: Pick<IPosts, 'postId'> | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewComments = Omit<IComments, 'commentId'> & { commentId: null };
