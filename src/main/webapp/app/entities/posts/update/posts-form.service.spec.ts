import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../posts.test-samples';

import { PostsFormService } from './posts-form.service';

describe('Posts Form Service', () => {
  let service: PostsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PostsFormService);
  });

  describe('Service methods', () => {
    describe('createPostsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPostsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            postId: expect.any(Object),
            title: expect.any(Object),
            content: expect.any(Object),
            createdAt: expect.any(Object),
            user: expect.any(Object),
            tags: expect.any(Object),
          }),
        );
      });

      it('passing IPosts should create a new form with FormGroup', () => {
        const formGroup = service.createPostsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            postId: expect.any(Object),
            title: expect.any(Object),
            content: expect.any(Object),
            createdAt: expect.any(Object),
            user: expect.any(Object),
            tags: expect.any(Object),
          }),
        );
      });
    });

    describe('getPosts', () => {
      it('should return NewPosts for default Posts initial value', () => {
        const formGroup = service.createPostsFormGroup(sampleWithNewData);

        const posts = service.getPosts(formGroup) as any;

        expect(posts).toMatchObject(sampleWithNewData);
      });

      it('should return NewPosts for empty Posts initial value', () => {
        const formGroup = service.createPostsFormGroup();

        const posts = service.getPosts(formGroup) as any;

        expect(posts).toMatchObject({});
      });

      it('should return IPosts', () => {
        const formGroup = service.createPostsFormGroup(sampleWithRequiredData);

        const posts = service.getPosts(formGroup) as any;

        expect(posts).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPosts should not enable postId FormControl', () => {
        const formGroup = service.createPostsFormGroup();
        expect(formGroup.controls.postId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.postId.disabled).toBe(true);
      });

      it('passing NewPosts should disable postId FormControl', () => {
        const formGroup = service.createPostsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.postId.disabled).toBe(true);

        service.resetForm(formGroup, { postId: null });

        expect(formGroup.controls.postId.disabled).toBe(true);
      });
    });
  });
});
