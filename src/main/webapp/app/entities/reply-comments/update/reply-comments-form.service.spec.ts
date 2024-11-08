import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../reply-comments.test-samples';

import { ReplyCommentsFormService } from './reply-comments-form.service';

describe('ReplyComments Form Service', () => {
  let service: ReplyCommentsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReplyCommentsFormService);
  });

  describe('Service methods', () => {
    describe('createReplyCommentsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReplyCommentsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            replyId: expect.any(Object),
            content: expect.any(Object),
            createdAt: expect.any(Object),
            parentCommentId: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IReplyComments should create a new form with FormGroup', () => {
        const formGroup = service.createReplyCommentsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            replyId: expect.any(Object),
            content: expect.any(Object),
            createdAt: expect.any(Object),
            parentCommentId: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getReplyComments', () => {
      it('should return NewReplyComments for default ReplyComments initial value', () => {
        const formGroup = service.createReplyCommentsFormGroup(sampleWithNewData);

        const replyComments = service.getReplyComments(formGroup) as any;

        expect(replyComments).toMatchObject(sampleWithNewData);
      });

      it('should return NewReplyComments for empty ReplyComments initial value', () => {
        const formGroup = service.createReplyCommentsFormGroup();

        const replyComments = service.getReplyComments(formGroup) as any;

        expect(replyComments).toMatchObject({});
      });

      it('should return IReplyComments', () => {
        const formGroup = service.createReplyCommentsFormGroup(sampleWithRequiredData);

        const replyComments = service.getReplyComments(formGroup) as any;

        expect(replyComments).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReplyComments should not enable replyId FormControl', () => {
        const formGroup = service.createReplyCommentsFormGroup();
        expect(formGroup.controls.replyId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.replyId.disabled).toBe(true);
      });

      it('passing NewReplyComments should disable replyId FormControl', () => {
        const formGroup = service.createReplyCommentsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.replyId.disabled).toBe(true);

        service.resetForm(formGroup, { replyId: null });

        expect(formGroup.controls.replyId.disabled).toBe(true);
      });
    });
  });
});
