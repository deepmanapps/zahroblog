import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITags, NewTags } from '../tags.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { tagId: unknown }> = Partial<Omit<T, 'tagId'>> & { tagId: T['tagId'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITags for edit and NewTagsFormGroupInput for create.
 */
type TagsFormGroupInput = ITags | PartialWithRequiredKeyOf<NewTags>;

type TagsFormDefaults = Pick<NewTags, 'tagId' | 'posts'>;

type TagsFormGroupContent = {
  tagId: FormControl<ITags['tagId'] | NewTags['tagId']>;
  tagName: FormControl<ITags['tagName']>;
  posts: FormControl<ITags['posts']>;
};

export type TagsFormGroup = FormGroup<TagsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TagsFormService {
  createTagsFormGroup(tags: TagsFormGroupInput = { tagId: null }): TagsFormGroup {
    const tagsRawValue = {
      ...this.getFormDefaults(),
      ...tags,
    };
    return new FormGroup<TagsFormGroupContent>({
      tagId: new FormControl(
        { value: tagsRawValue.tagId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tagName: new FormControl(tagsRawValue.tagName),
      posts: new FormControl(tagsRawValue.posts ?? []),
    });
  }

  getTags(form: TagsFormGroup): ITags | NewTags {
    return form.getRawValue() as ITags | NewTags;
  }

  resetForm(form: TagsFormGroup, tags: TagsFormGroupInput): void {
    const tagsRawValue = { ...this.getFormDefaults(), ...tags };
    form.reset(
      {
        ...tagsRawValue,
        tagId: { value: tagsRawValue.tagId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TagsFormDefaults {
    return {
      tagId: null,
      posts: [],
    };
  }
}
