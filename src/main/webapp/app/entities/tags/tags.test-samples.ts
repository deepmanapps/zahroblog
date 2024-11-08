import { ITags, NewTags } from './tags.model';

export const sampleWithRequiredData: ITags = {
  tagId: 10146,
};

export const sampleWithPartialData: ITags = {
  tagId: 15871,
  tagName: 'envers miaou cadre',
};

export const sampleWithFullData: ITags = {
  tagId: 17677,
  tagName: 'atchoum',
};

export const sampleWithNewData: NewTags = {
  tagId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
