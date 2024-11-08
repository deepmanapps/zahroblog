import dayjs from 'dayjs/esm';

import { IReplyComments, NewReplyComments } from './reply-comments.model';

export const sampleWithRequiredData: IReplyComments = {
  replyId: 2515,
};

export const sampleWithPartialData: IReplyComments = {
  replyId: 18632,
  content: 'avex',
  createdAt: dayjs('2024-11-08T00:11'),
};

export const sampleWithFullData: IReplyComments = {
  replyId: 5193,
  content: 'police avex',
  createdAt: dayjs('2024-11-08T08:51'),
};

export const sampleWithNewData: NewReplyComments = {
  replyId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
