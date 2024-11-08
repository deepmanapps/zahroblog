import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ITags } from 'app/entities/tags/tags.model';
import { TagsService } from 'app/entities/tags/service/tags.service';
import { PostsService } from '../service/posts.service';
import { IPosts } from '../posts.model';
import { PostsFormGroup, PostsFormService } from './posts-form.service';

@Component({
  standalone: true,
  selector: 'jhi-posts-update',
  templateUrl: './posts-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PostsUpdateComponent implements OnInit {
  isSaving = false;
  posts: IPosts | null = null;

  usersSharedCollection: IUser[] = [];
  tagsSharedCollection: ITags[] = [];

  protected postsService = inject(PostsService);
  protected postsFormService = inject(PostsFormService);
  protected userService = inject(UserService);
  protected tagsService = inject(TagsService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PostsFormGroup = this.postsFormService.createPostsFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareTags = (o1: ITags | null, o2: ITags | null): boolean => this.tagsService.compareTags(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ posts }) => {
      this.posts = posts;
      if (posts) {
        this.updateForm(posts);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const posts = this.postsFormService.getPosts(this.editForm);
    if (posts.postId !== null) {
      this.subscribeToSaveResponse(this.postsService.update(posts));
    } else {
      this.subscribeToSaveResponse(this.postsService.create(posts));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPosts>>): void {
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

  protected updateForm(posts: IPosts): void {
    this.posts = posts;
    this.postsFormService.resetForm(this.editForm, posts);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, posts.user);
    this.tagsSharedCollection = this.tagsService.addTagsToCollectionIfMissing<ITags>(this.tagsSharedCollection, ...(posts.tags ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.posts?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.tagsService
      .query()
      .pipe(map((res: HttpResponse<ITags[]>) => res.body ?? []))
      .pipe(map((tags: ITags[]) => this.tagsService.addTagsToCollectionIfMissing<ITags>(tags, ...(this.posts?.tags ?? []))))
      .subscribe((tags: ITags[]) => (this.tagsSharedCollection = tags));
  }
}
