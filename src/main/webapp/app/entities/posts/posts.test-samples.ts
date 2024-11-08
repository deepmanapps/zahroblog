import dayjs from 'dayjs/esm';

import { IPosts, NewPosts } from './posts.model';

export const sampleWithRequiredData: IPosts = {
  postId: 25984,
};

export const sampleWithPartialData: IPosts = {
  postId: 816,
  title: 'guetter féliciter',
};

export const sampleWithFullData: IPosts = {
  postId: 22475,
  title: 'ah coac coac membre du personnel',
  content: 'embrasser en vérité',
  createdAt: dayjs('2024-11-08T19:18'),
};

export const sampleWithNewData: NewPosts = {
  postId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
