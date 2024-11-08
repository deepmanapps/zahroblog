import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReplyComments, NewReplyComments } from '../reply-comments.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { replyId: unknown }> = Partial<Omit<T, 'replyId'>> & { replyId: T['replyId'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReplyComments for edit and NewReplyCommentsFormGroupInput for create.
 */
type ReplyCommentsFormGroupInput = IReplyComments | PartialWithRequiredKeyOf<NewReplyComments>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReplyComments | NewReplyComments> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type ReplyCommentsFormRawValue = FormValueOf<IReplyComments>;

type NewReplyCommentsFormRawValue = FormValueOf<NewReplyComments>;

type ReplyCommentsFormDefaults = Pick<NewReplyComments, 'replyId' | 'createdAt'>;

type ReplyCommentsFormGroupContent = {
  replyId: FormControl<ReplyCommentsFormRawValue['replyId'] | NewReplyComments['replyId']>;
  content: FormControl<ReplyCommentsFormRawValue['content']>;
  createdAt: FormControl<ReplyCommentsFormRawValue['createdAt']>;
  parentCommentId: FormControl<ReplyCommentsFormRawValue['parentCommentId']>;
  user: FormControl<ReplyCommentsFormRawValue['user']>;
};

export type ReplyCommentsFormGroup = FormGroup<ReplyCommentsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReplyCommentsFormService {
  createReplyCommentsFormGroup(replyComments: ReplyCommentsFormGroupInput = { replyId: null }): ReplyCommentsFormGroup {
    const replyCommentsRawValue = this.convertReplyCommentsToReplyCommentsRawValue({
      ...this.getFormDefaults(),
      ...replyComments,
    });
    return new FormGroup<ReplyCommentsFormGroupContent>({
      replyId: new FormControl(
        { value: replyCommentsRawValue.replyId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      content: new FormControl(replyCommentsRawValue.content),
      createdAt: new FormControl(replyCommentsRawValue.createdAt),
      parentCommentId: new FormControl(replyCommentsRawValue.parentCommentId),
      user: new FormControl(replyCommentsRawValue.user),
    });
  }

  getReplyComments(form: ReplyCommentsFormGroup): IReplyComments | NewReplyComments {
    return this.convertReplyCommentsRawValueToReplyComments(form.getRawValue() as ReplyCommentsFormRawValue | NewReplyCommentsFormRawValue);
  }

  resetForm(form: ReplyCommentsFormGroup, replyComments: ReplyCommentsFormGroupInput): void {
    const replyCommentsRawValue = this.convertReplyCommentsToReplyCommentsRawValue({ ...this.getFormDefaults(), ...replyComments });
    form.reset(
      {
        ...replyCommentsRawValue,
        replyId: { value: replyCommentsRawValue.replyId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReplyCommentsFormDefaults {
    const currentTime = dayjs();

    return {
      replyId: null,
      createdAt: currentTime,
    };
  }

  private convertReplyCommentsRawValueToReplyComments(
    rawReplyComments: ReplyCommentsFormRawValue | NewReplyCommentsFormRawValue,
  ): IReplyComments | NewReplyComments {
    return {
      ...rawReplyComments,
      createdAt: dayjs(rawReplyComments.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertReplyCommentsToReplyCommentsRawValue(
    replyComments: IReplyComments | (Partial<NewReplyComments> & ReplyCommentsFormDefaults),
  ): ReplyCommentsFormRawValue | PartialWithRequiredKeyOf<NewReplyCommentsFormRawValue> {
    return {
      ...replyComments,
      createdAt: replyComments.createdAt ? replyComments.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
