import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPosts } from 'app/entities/posts/posts.model';
import { PostsService } from 'app/entities/posts/service/posts.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IComments } from '../comments.model';
import { CommentsService } from '../service/comments.service';
import { CommentsFormService } from './comments-form.service';

import { CommentsUpdateComponent } from './comments-update.component';

describe('Comments Management Update Component', () => {
  let comp: CommentsUpdateComponent;
  let fixture: ComponentFixture<CommentsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let commentsFormService: CommentsFormService;
  let commentsService: CommentsService;
  let postsService: PostsService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommentsUpdateComponent],
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
      .overrideTemplate(CommentsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CommentsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    commentsFormService = TestBed.inject(CommentsFormService);
    commentsService = TestBed.inject(CommentsService);
    postsService = TestBed.inject(PostsService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Posts query and add missing value', () => {
      const comments: IComments = { commentId: 456 };
      const posts: IPosts = { postId: 5206 };
      comments.posts = posts;

      const postsCollection: IPosts[] = [{ postId: 30489 }];
      jest.spyOn(postsService, 'query').mockReturnValue(of(new HttpResponse({ body: postsCollection })));
      const additionalPosts = [posts];
      const expectedCollection: IPosts[] = [...additionalPosts, ...postsCollection];
      jest.spyOn(postsService, 'addPostsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ comments });
      comp.ngOnInit();

      expect(postsService.query).toHaveBeenCalled();
      expect(postsService.addPostsToCollectionIfMissing).toHaveBeenCalledWith(
        postsCollection,
        ...additionalPosts.map(expect.objectContaining),
      );
      expect(comp.postsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const comments: IComments = { commentId: 456 };
      const user: IUser = { id: 24715 };
      comments.user = user;

      const userCollection: IUser[] = [{ id: 7498 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ comments });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const comments: IComments = { commentId: 456 };
      const posts: IPosts = { postId: 19243 };
      comments.posts = posts;
      const user: IUser = { id: 15843 };
      comments.user = user;

      activatedRoute.data = of({ comments });
      comp.ngOnInit();

      expect(comp.postsSharedCollection).toContain(posts);
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.comments).toEqual(comments);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IComments>>();
      const comments = { commentId: 123 };
      jest.spyOn(commentsFormService, 'getComments').mockReturnValue(comments);
      jest.spyOn(commentsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ comments });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: comments }));
      saveSubject.complete();

      // THEN
      expect(commentsFormService.getComments).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(commentsService.update).toHaveBeenCalledWith(expect.objectContaining(comments));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IComments>>();
      const comments = { commentId: 123 };
      jest.spyOn(commentsFormService, 'getComments').mockReturnValue({ commentId: null });
      jest.spyOn(commentsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ comments: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: comments }));
      saveSubject.complete();

      // THEN
      expect(commentsFormService.getComments).toHaveBeenCalled();
      expect(commentsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IComments>>();
      const comments = { commentId: 123 };
      jest.spyOn(commentsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ comments });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(commentsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePosts', () => {
      it('Should forward to postsService', () => {
        const entity = { postId: 123 };
        const entity2 = { postId: 456 };
        jest.spyOn(postsService, 'comparePosts');
        comp.comparePosts(entity, entity2);
        expect(postsService.comparePosts).toHaveBeenCalledWith(entity, entity2);
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
