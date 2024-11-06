import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 31622,
  login: 'yTqLmV@ii7c\\kDkYtfE\\b63Gxa\\?szJ',
};

export const sampleWithPartialData: IUser = {
  id: 10380,
  login: '3@owy\\9mJR',
};

export const sampleWithFullData: IUser = {
  id: 5649,
  login: 'C',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
