import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../tags.test-samples';

import { TagsFormService } from './tags-form.service';

describe('Tags Form Service', () => {
  let service: TagsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TagsFormService);
  });

  describe('Service methods', () => {
    describe('createTagsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTagsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            tagId: expect.any(Object),
            tagName: expect.any(Object),
            posts: expect.any(Object),
          }),
        );
      });

      it('passing ITags should create a new form with FormGroup', () => {
        const formGroup = service.createTagsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            tagId: expect.any(Object),
            tagName: expect.any(Object),
            posts: expect.any(Object),
          }),
        );
      });
    });

    describe('getTags', () => {
      it('should return NewTags for default Tags initial value', () => {
        const formGroup = service.createTagsFormGroup(sampleWithNewData);

        const tags = service.getTags(formGroup) as any;

        expect(tags).toMatchObject(sampleWithNewData);
      });

      it('should return NewTags for empty Tags initial value', () => {
        const formGroup = service.createTagsFormGroup();

        const tags = service.getTags(formGroup) as any;

        expect(tags).toMatchObject({});
      });

      it('should return ITags', () => {
        const formGroup = service.createTagsFormGroup(sampleWithRequiredData);

        const tags = service.getTags(formGroup) as any;

        expect(tags).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITags should not enable tagId FormControl', () => {
        const formGroup = service.createTagsFormGroup();
        expect(formGroup.controls.tagId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.tagId.disabled).toBe(true);
      });

      it('passing NewTags should disable tagId FormControl', () => {
        const formGroup = service.createTagsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.tagId.disabled).toBe(true);

        service.resetForm(formGroup, { tagId: null });

        expect(formGroup.controls.tagId.disabled).toBe(true);
      });
    });
  });
});
