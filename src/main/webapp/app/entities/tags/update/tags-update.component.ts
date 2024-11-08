import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPosts } from 'app/entities/posts/posts.model';
import { PostsService } from 'app/entities/posts/service/posts.service';
import { ITags } from '../tags.model';
import { TagsService } from '../service/tags.service';
import { TagsFormGroup, TagsFormService } from './tags-form.service';

@Component({
  standalone: true,
  selector: 'jhi-tags-update',
  templateUrl: './tags-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TagsUpdateComponent implements OnInit {
  isSaving = false;
  tags: ITags | null = null;

  postsSharedCollection: IPosts[] = [];

  protected tagsService = inject(TagsService);
  protected tagsFormService = inject(TagsFormService);
  protected postsService = inject(PostsService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TagsFormGroup = this.tagsFormService.createTagsFormGroup();

  comparePosts = (o1: IPosts | null, o2: IPosts | null): boolean => this.postsService.comparePosts(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tags }) => {
      this.tags = tags;
      if (tags) {
        this.updateForm(tags);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tags = this.tagsFormService.getTags(this.editForm);
    if (tags.tagId !== null) {
      this.subscribeToSaveResponse(this.tagsService.update(tags));
    } else {
      this.subscribeToSaveResponse(this.tagsService.create(tags));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITags>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(tags: ITags): void {
    this.tags = tags;
    this.tagsFormService.resetForm(this.editForm, tags);

    this.postsSharedCollection = this.postsService.addPostsToCollectionIfMissing<IPosts>(this.postsSharedCollection, ...(tags.posts ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.postsService
      .query()
      .pipe(map((res: HttpResponse<IPosts[]>) => res.body ?? []))
      .pipe(map((posts: IPosts[]) => this.postsService.addPostsToCollectionIfMissing<IPosts>(posts, ...(this.tags?.posts ?? []))))
      .subscribe((posts: IPosts[]) => (this.postsSharedCollection = posts));
  }
}
