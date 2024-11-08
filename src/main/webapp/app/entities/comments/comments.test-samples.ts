import dayjs from 'dayjs/esm';

import { IComments, NewComments } from './comments.model';

export const sampleWithRequiredData: IComments = {
  commentId: 16161,
};

export const sampleWithPartialData: IComments = {
  commentId: 28864,
};

export const sampleWithFullData: IComments = {
  commentId: 11601,
  content: 'tant préférer derrière',
  createdAt: dayjs('2024-11-08T04:49'),
};

export const sampleWithNewData: NewComments = {
  commentId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
