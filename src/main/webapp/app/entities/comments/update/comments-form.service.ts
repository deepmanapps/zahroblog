import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IComments, NewComments } from '../comments.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { commentId: unknown }> = Partial<Omit<T, 'commentId'>> & { commentId: T['commentId'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IComments for edit and NewCommentsFormGroupInput for create.
 */
type CommentsFormGroupInput = IComments | PartialWithRequiredKeyOf<NewComments>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IComments | NewComments> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type CommentsFormRawValue = FormValueOf<IComments>;

type NewCommentsFormRawValue = FormValueOf<NewComments>;

type CommentsFormDefaults = Pick<NewComments, 'commentId' | 'createdAt'>;

type CommentsFormGroupContent = {
  commentId: FormControl<CommentsFormRawValue['commentId'] | NewComments['commentId']>;
  content: FormControl<CommentsFormRawValue['content']>;
  createdAt: FormControl<CommentsFormRawValue['createdAt']>;
  posts: FormControl<CommentsFormRawValue['posts']>;
  user: FormControl<CommentsFormRawValue['user']>;
};

export type CommentsFormGroup = FormGroup<CommentsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CommentsFormService {
  createCommentsFormGroup(comments: CommentsFormGroupInput = { commentId: null }): CommentsFormGroup {
    const commentsRawValue = this.convertCommentsToCommentsRawValue({
      ...this.getFormDefaults(),
      ...comments,
    });
    return new FormGroup<CommentsFormGroupContent>({
      commentId: new FormControl(
        { value: commentsRawValue.commentId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      content: new FormControl(commentsRawValue.content),
      createdAt: new FormControl(commentsRawValue.createdAt),
      posts: new FormControl(commentsRawValue.posts),
      user: new FormControl(commentsRawValue.user),
    });
  }

  getComments(form: CommentsFormGroup): IComments | NewComments {
    return this.convertCommentsRawValueToComments(form.getRawValue() as CommentsFormRawValue | NewCommentsFormRawValue);
  }

  resetForm(form: CommentsFormGroup, comments: CommentsFormGroupInput): void {
    const commentsRawValue = this.convertCommentsToCommentsRawValue({ ...this.getFormDefaults(), ...comments });
    form.reset(
      {
        ...commentsRawValue,
        commentId: { value: commentsRawValue.commentId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CommentsFormDefaults {
    const currentTime = dayjs();

    return {
      commentId: null,
      createdAt: currentTime,
    };
  }

  private convertCommentsRawValueToComments(rawComments: CommentsFormRawValue | NewCommentsFormRawValue): IComments | NewComments {
    return {
      ...rawComments,
      createdAt: dayjs(rawComments.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertCommentsToCommentsRawValue(
    comments: IComments | (Partial<NewComments> & CommentsFormDefaults),
  ): CommentsFormRawValue | PartialWithRequiredKeyOf<NewCommentsFormRawValue> {
    return {
      ...comments,
      createdAt: comments.createdAt ? comments.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
