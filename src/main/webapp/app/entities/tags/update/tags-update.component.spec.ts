import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPosts } from 'app/entities/posts/posts.model';
import { PostsService } from 'app/entities/posts/service/posts.service';
import { TagsService } from '../service/tags.service';
import { ITags } from '../tags.model';
import { TagsFormService } from './tags-form.service';

import { TagsUpdateComponent } from './tags-update.component';

describe('Tags Management Update Component', () => {
  let comp: TagsUpdateComponent;
  let fixture: ComponentFixture<TagsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tagsFormService: TagsFormService;
  let tagsService: TagsService;
  let postsService: PostsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TagsUpdateComponent],
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
      .overrideTemplate(TagsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TagsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tagsFormService = TestBed.inject(TagsFormService);
    tagsService = TestBed.inject(TagsService);
    postsService = TestBed.inject(PostsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Posts query and add missing value', () => {
      const tags: ITags = { tagId: 456 };
      const posts: IPosts[] = [{ postId: 16408 }];
      tags.posts = posts;

      const postsCollection: IPosts[] = [{ postId: 17515 }];
      jest.spyOn(postsService, 'query').mockReturnValue(of(new HttpResponse({ body: postsCollection })));
      const additionalPosts = [...posts];
      const expectedCollection: IPosts[] = [...additionalPosts, ...postsCollection];
      jest.spyOn(postsService, 'addPostsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tags });
      comp.ngOnInit();

      expect(postsService.query).toHaveBeenCalled();
      expect(postsService.addPostsToCollectionIfMissing).toHaveBeenCalledWith(
        postsCollection,
        ...additionalPosts.map(expect.objectContaining),
      );
      expect(comp.postsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const tags: ITags = { tagId: 456 };
      const posts: IPosts = { postId: 77 };
      tags.posts = [posts];

      activatedRoute.data = of({ tags });
      comp.ngOnInit();

      expect(comp.postsSharedCollection).toContain(posts);
      expect(comp.tags).toEqual(tags);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITags>>();
      const tags = { tagId: 123 };
      jest.spyOn(tagsFormService, 'getTags').mockReturnValue(tags);
      jest.spyOn(tagsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tags });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tags }));
      saveSubject.complete();

      // THEN
      expect(tagsFormService.getTags).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tagsService.update).toHaveBeenCalledWith(expect.objectContaining(tags));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITags>>();
      const tags = { tagId: 123 };
      jest.spyOn(tagsFormService, 'getTags').mockReturnValue({ tagId: null });
      jest.spyOn(tagsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tags: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tags }));
      saveSubject.complete();

      // THEN
      expect(tagsFormService.getTags).toHaveBeenCalled();
      expect(tagsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITags>>();
      const tags = { tagId: 123 };
      jest.spyOn(tagsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tags });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tagsService.update).toHaveBeenCalled();
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
  });
});
