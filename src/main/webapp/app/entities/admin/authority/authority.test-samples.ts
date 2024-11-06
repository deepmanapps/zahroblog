import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'cf3e3615-7138-4b03-9201-d492ff3e40ac',
};

export const sampleWithPartialData: IAuthority = {
  name: '89f858bb-6d66-4e47-bad2-554ecd7879c1',
};

export const sampleWithFullData: IAuthority = {
  name: 'e0159b31-687c-4e4f-b595-d13cc3075916',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
