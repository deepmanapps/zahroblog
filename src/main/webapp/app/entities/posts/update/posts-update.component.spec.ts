import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ITags } from 'app/entities/tags/tags.model';
import { TagsService } from 'app/entities/tags/service/tags.service';
import { IPosts } from '../posts.model';
import { PostsService } from '../service/posts.service';
import { PostsFormService } from './posts-form.service';

import { PostsUpdateComponent } from './posts-update.component';

describe('Posts Management Update Component', () => {
  let comp: PostsUpdateComponent;
  let fixture: ComponentFixture<PostsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let postsFormService: PostsFormService;
  let postsService: PostsService;
  let userService: UserService;
  let tagsService: TagsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PostsUpdateComponent],
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
      .overrideTemplate(PostsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PostsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    postsFormService = TestBed.inject(PostsFormService);
    postsService = TestBed.inject(PostsService);
    userService = TestBed.inject(UserService);
    tagsService = TestBed.inject(TagsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const posts: IPosts = { postId: 456 };
      const user: IUser = { id: 32233 };
      posts.user = user;

      const userCollection: IUser[] = [{ id: 8148 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ posts });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Tags query and add missing value', () => {
      const posts: IPosts = { postId: 456 };
      const tags: ITags[] = [{ tagId: 9845 }];
      posts.tags = tags;

      const tagsCollection: ITags[] = [{ tagId: 4430 }];
      jest.spyOn(tagsService, 'query').mockReturnValue(of(new HttpResponse({ body: tagsCollection })));
      const additionalTags = [...tags];
      const expectedCollection: ITags[] = [...additionalTags, ...tagsCollection];
      jest.spyOn(tagsService, 'addTagsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ posts });
      comp.ngOnInit();

      expect(tagsService.query).toHaveBeenCalled();
      expect(tagsService.addTagsToCollectionIfMissing).toHaveBeenCalledWith(tagsCollection, ...additionalTags.map(expect.objectContaining));
      expect(comp.tagsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const posts: IPosts = { postId: 456 };
      const user: IUser = { id: 28530 };
      posts.user = user;
      const tags: ITags = { tagId: 16537 };
      posts.tags = [tags];

      activatedRoute.data = of({ posts });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.tagsSharedCollection).toContain(tags);
      expect(comp.posts).toEqual(posts);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPosts>>();
      const posts = { postId: 123 };
      jest.spyOn(postsFormService, 'getPosts').mockReturnValue(posts);
      jest.spyOn(postsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ posts });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: posts }));
      saveSubject.complete();

      // THEN
      expect(postsFormService.getPosts).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(postsService.update).toHaveBeenCalledWith(expect.objectContaining(posts));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPosts>>();
      const posts = { postId: 123 };
      jest.spyOn(postsFormService, 'getPosts').mockReturnValue({ postId: null });
      jest.spyOn(postsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ posts: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: posts }));
      saveSubject.complete();

      // THEN
      expect(postsFormService.getPosts).toHaveBeenCalled();
      expect(postsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPosts>>();
      const posts = { postId: 123 };
      jest.spyOn(postsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ posts });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(postsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTags', () => {
      it('Should forward to tagsService', () => {
        const entity = { tagId: 123 };
        const entity2 = { tagId: 456 };
        jest.spyOn(tagsService, 'compareTags');
        comp.compareTags(entity, entity2);
        expect(tagsService.compareTags).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
