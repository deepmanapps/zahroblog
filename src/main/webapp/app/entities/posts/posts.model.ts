import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ITags } from 'app/entities/tags/tags.model';

export interface IPosts {
  postId: number;
  title?: string | null;
  content?: string | null;
  createdAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  tags?: Pick<ITags, 'tagId'>[] | null;
}

export type NewPosts = Omit<IPosts, 'postId'> & { postId: null };
