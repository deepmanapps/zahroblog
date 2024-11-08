import { IPosts } from 'app/entities/posts/posts.model';

export interface ITags {
  tagId: number;
  tagName?: string | null;
  posts?: Pick<IPosts, 'postId'>[] | null;
}

export type NewTags = Omit<ITags, 'tagId'> & { tagId: null };
