import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPosts } from 'app/entities/posts/posts.model';
import { PostsService } from 'app/entities/posts/service/posts.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { CommentsService } from '../service/comments.service';
import { IComments } from '../comments.model';
import { CommentsFormGroup, CommentsFormService } from './comments-form.service';

@Component({
  standalone: true,
  selector: 'jhi-comments-update',
  templateUrl: './comments-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CommentsUpdateComponent implements OnInit {
  isSaving = false;
  comments: IComments | null = null;

  postsSharedCollection: IPosts[] = [];
  usersSharedCollection: IUser[] = [];

  protected commentsService = inject(CommentsService);
  protected commentsFormService = inject(CommentsFormService);
  protected postsService = inject(PostsService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CommentsFormGroup = this.commentsFormService.createCommentsFormGroup();

  comparePosts = (o1: IPosts | null, o2: IPosts | null): boolean => this.postsService.comparePosts(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ comments }) => {
      this.comments = comments;
      if (comments) {
        this.updateForm(comments);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const comments = this.commentsFormService.getComments(this.editForm);
    if (comments.commentId !== null) {
      this.subscribeToSaveResponse(this.commentsService.update(comments));
    } else {
      this.subscribeToSaveResponse(this.commentsService.create(comments));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IComments>>): void {
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

  protected updateForm(comments: IComments): void {
    this.comments = comments;
    this.commentsFormService.resetForm(this.editForm, comments);

    this.postsSharedCollection = this.postsService.addPostsToCollectionIfMissing<IPosts>(this.postsSharedCollection, comments.posts);
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, comments.user);
  }

  protected loadRelationshipsOptions(): void {
    this.postsService
      .query()
      .pipe(map((res: HttpResponse<IPosts[]>) => res.body ?? []))
      .pipe(map((posts: IPosts[]) => this.postsService.addPostsToCollectionIfMissing<IPosts>(posts, this.comments?.posts)))
      .subscribe((posts: IPosts[]) => (this.postsSharedCollection = posts));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.comments?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
