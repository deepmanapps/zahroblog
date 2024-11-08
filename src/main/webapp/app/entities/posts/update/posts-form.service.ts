import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPosts, NewPosts } from '../posts.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { postId: unknown }> = Partial<Omit<T, 'postId'>> & { postId: T['postId'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPosts for edit and NewPostsFormGroupInput for create.
 */
type PostsFormGroupInput = IPosts | PartialWithRequiredKeyOf<NewPosts>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPosts | NewPosts> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type PostsFormRawValue = FormValueOf<IPosts>;

type NewPostsFormRawValue = FormValueOf<NewPosts>;

type PostsFormDefaults = Pick<NewPosts, 'postId' | 'createdAt' | 'tags'>;

type PostsFormGroupContent = {
  postId: FormControl<PostsFormRawValue['postId'] | NewPosts['postId']>;
  title: FormControl<PostsFormRawValue['title']>;
  content: FormControl<PostsFormRawValue['content']>;
  createdAt: FormControl<PostsFormRawValue['createdAt']>;
  user: FormControl<PostsFormRawValue['user']>;
  tags: FormControl<PostsFormRawValue['tags']>;
};

export type PostsFormGroup = FormGroup<PostsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PostsFormService {
  createPostsFormGroup(posts: PostsFormGroupInput = { postId: null }): PostsFormGroup {
    const postsRawValue = this.convertPostsToPostsRawValue({
      ...this.getFormDefaults(),
      ...posts,
    });
    return new FormGroup<PostsFormGroupContent>({
      postId: new FormControl(
        { value: postsRawValue.postId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(postsRawValue.title),
      content: new FormControl(postsRawValue.content),
      createdAt: new FormControl(postsRawValue.createdAt),
      user: new FormControl(postsRawValue.user),
      tags: new FormControl(postsRawValue.tags ?? []),
    });
  }

  getPosts(form: PostsFormGroup): IPosts | NewPosts {
    return this.convertPostsRawValueToPosts(form.getRawValue() as PostsFormRawValue | NewPostsFormRawValue);
  }

  resetForm(form: PostsFormGroup, posts: PostsFormGroupInput): void {
    const postsRawValue = this.convertPostsToPostsRawValue({ ...this.getFormDefaults(), ...posts });
    form.reset(
      {
        ...postsRawValue,
        postId: { value: postsRawValue.postId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PostsFormDefaults {
    const currentTime = dayjs();

    return {
      postId: null,
      createdAt: currentTime,
      tags: [],
    };
  }

  private convertPostsRawValueToPosts(rawPosts: PostsFormRawValue | NewPostsFormRawValue): IPosts | NewPosts {
    return {
      ...rawPosts,
      createdAt: dayjs(rawPosts.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertPostsToPostsRawValue(
    posts: IPosts | (Partial<NewPosts> & PostsFormDefaults),
  ): PostsFormRawValue | PartialWithRequiredKeyOf<NewPostsFormRawValue> {
    return {
      ...posts,
      createdAt: posts.createdAt ? posts.createdAt.format(DATE_TIME_FORMAT) : undefined,
      tags: posts.tags ?? [],
    };
  }
}
