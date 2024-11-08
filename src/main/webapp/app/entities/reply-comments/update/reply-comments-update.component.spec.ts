import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IComments } from 'app/entities/comments/comments.model';
import { CommentsService } from 'app/entities/comments/service/comments.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IReplyComments } from '../reply-comments.model';
import { ReplyCommentsService } from '../service/reply-comments.service';
import { ReplyCommentsFormService } from './reply-comments-form.service';

import { ReplyCommentsUpdateComponent } from './reply-comments-update.component';

describe('ReplyComments Management Update Component', () => {
  let comp: ReplyCommentsUpdateComponent;
  let fixture: ComponentFixture<ReplyCommentsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let replyCommentsFormService: ReplyCommentsFormService;
  let replyCommentsService: ReplyCommentsService;
  let commentsService: CommentsService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReplyCommentsUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ReplyCommentsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReplyCommentsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    replyCommentsFormService = TestBed.inject(ReplyCommentsFormService);
    replyCommentsService = TestBed.inject(ReplyCommentsService);
    commentsService = TestBed.inject(CommentsService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Comments query and add missing value', () => {
      const replyComments: IReplyComments = { replyId: 456 };
      const parentCommentId: IComments = { commentId: 18424 };
      replyComments.parentCommentId = parentCommentId;

      const commentsCollection: IComments[] = [{ commentId: 30580 }];
      jest.spyOn(commentsService, 'query').mockReturnValue(of(new HttpResponse({ body: commentsCollection })));
      const additionalComments = [parentCommentId];
      const expectedCollection: IComments[] = [...additionalComments, ...commentsCollection];
      jest.spyOn(commentsService, 'addCommentsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ replyComments });
      comp.ngOnInit();

      expect(commentsService.query).toHaveBeenCalled();
      expect(commentsService.addCommentsToCollectionIfMissing).toHaveBeenCalledWith(
        commentsCollection,
        ...additionalComments.map(expect.objectContaining),
      );
      expect(comp.commentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const replyComments: IReplyComments = { replyId: 456 };
      const user: IUser = { id: 29298 };
      replyComments.user = user;

      const userCollection: IUser[] = [{ id: 8557 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ replyComments });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const replyComments: IReplyComments = { replyId: 456 };
      const parentCommentId: IComments = { commentId: 3416 };
      replyComments.parentCommentId = parentCommentId;
      const user: IUser = { id: 21712 };
      replyComments.user = user;

      activatedRoute.data = of({ replyComments });
      comp.ngOnInit();

      expect(comp.commentsSharedCollection).toContain(parentCommentId);
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.replyComments).toEqual(replyComments);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReplyComments>>();
      const replyComments = { replyId: 123 };
      jest.spyOn(replyCommentsFormService, 'getReplyComments').mockReturnValue(replyComments);
      jest.spyOn(replyCommentsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ replyComments });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: replyComments }));
      saveSubject.complete();

      // THEN
      expect(replyCommentsFormService.getReplyComments).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(replyCommentsService.update).toHaveBeenCalledWith(expect.objectContaining(replyComments));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReplyComments>>();
      const replyComments = { replyId: 123 };
      jest.spyOn(replyCommentsFormService, 'getReplyComments').mockReturnValue({ replyId: null });
      jest.spyOn(replyCommentsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ replyComments: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: replyComments }));
      saveSubject.complete();

      // THEN
      expect(replyCommentsFormService.getReplyComments).toHaveBeenCalled();
      expect(replyCommentsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReplyComments>>();
      const replyComments = { replyId: 123 };
      jest.spyOn(replyCommentsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ replyComments });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(replyCommentsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareComments', () => {
      it('Should forward to commentsService', () => {
        const entity = { commentId: 123 };
        const entity2 = { commentId: 456 };
        jest.spyOn(commentsService, 'compareComments');
        comp.compareComments(entity, entity2);
        expect(commentsService.compareComments).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
