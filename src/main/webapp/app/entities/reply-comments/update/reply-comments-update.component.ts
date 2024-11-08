import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IComments } from 'app/entities/comments/comments.model';
import { CommentsService } from 'app/entities/comments/service/comments.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ReplyCommentsService } from '../service/reply-comments.service';
import { IReplyComments } from '../reply-comments.model';
import { ReplyCommentsFormGroup, ReplyCommentsFormService } from './reply-comments-form.service';

@Component({
  standalone: true,
  selector: 'jhi-reply-comments-update',
  templateUrl: './reply-comments-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReplyCommentsUpdateComponent implements OnInit {
  isSaving = false;
  replyComments: IReplyComments | null = null;

  commentsSharedCollection: IComments[] = [];
  usersSharedCollection: IUser[] = [];

  protected replyCommentsService = inject(ReplyCommentsService);
  protected replyCommentsFormService = inject(ReplyCommentsFormService);
  protected commentsService = inject(CommentsService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ReplyCommentsFormGroup = this.replyCommentsFormService.createReplyCommentsFormGroup();

  compareComments = (o1: IComments | null, o2: IComments | null): boolean => this.commentsService.compareComments(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ replyComments }) => {
      this.replyComments = replyComments;
      if (replyComments) {
        this.updateForm(replyComments);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const replyComments = this.replyCommentsFormService.getReplyComments(this.editForm);
    if (replyComments.replyId !== null) {
      this.subscribeToSaveResponse(this.replyCommentsService.update(replyComments));
    } else {
      this.subscribeToSaveResponse(this.replyCommentsService.create(replyComments));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReplyComments>>): void {
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

  protected updateForm(replyComments: IReplyComments): void {
    this.replyComments = replyComments;
    this.replyCommentsFormService.resetForm(this.editForm, replyComments);

    this.commentsSharedCollection = this.commentsService.addCommentsToCollectionIfMissing<IComments>(
      this.commentsSharedCollection,
      replyComments.parentCommentId,
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, replyComments.user);
  }

  protected loadRelationshipsOptions(): void {
    this.commentsService
      .query()
      .pipe(map((res: HttpResponse<IComments[]>) => res.body ?? []))
      .pipe(
        map((comments: IComments[]) =>
          this.commentsService.addCommentsToCollectionIfMissing<IComments>(comments, this.replyComments?.parentCommentId),
        ),
      )
      .subscribe((comments: IComments[]) => (this.commentsSharedCollection = comments));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.replyComments?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
