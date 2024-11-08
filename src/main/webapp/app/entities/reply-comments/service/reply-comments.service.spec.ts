import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IReplyComments } from '../reply-comments.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../reply-comments.test-samples';

import { ReplyCommentsService, RestReplyComments } from './reply-comments.service';

const requireRestSample: RestReplyComments = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('ReplyComments Service', () => {
  let service: ReplyCommentsService;
  let httpMock: HttpTestingController;
  let expectedResult: IReplyComments | IReplyComments[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ReplyCommentsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ReplyComments', () => {
      const replyComments = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(replyComments).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ReplyComments', () => {
      const replyComments = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(replyComments).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ReplyComments', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ReplyComments', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ReplyComments', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addReplyCommentsToCollectionIfMissing', () => {
      it('should add a ReplyComments to an empty array', () => {
        const replyComments: IReplyComments = sampleWithRequiredData;
        expectedResult = service.addReplyCommentsToCollectionIfMissing([], replyComments);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(replyComments);
      });

      it('should not add a ReplyComments to an array that contains it', () => {
        const replyComments: IReplyComments = sampleWithRequiredData;
        const replyCommentsCollection: IReplyComments[] = [
          {
            ...replyComments,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addReplyCommentsToCollectionIfMissing(replyCommentsCollection, replyComments);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ReplyComments to an array that doesn't contain it", () => {
        const replyComments: IReplyComments = sampleWithRequiredData;
        const replyCommentsCollection: IReplyComments[] = [sampleWithPartialData];
        expectedResult = service.addReplyCommentsToCollectionIfMissing(replyCommentsCollection, replyComments);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(replyComments);
      });

      it('should add only unique ReplyComments to an array', () => {
        const replyCommentsArray: IReplyComments[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const replyCommentsCollection: IReplyComments[] = [sampleWithRequiredData];
        expectedResult = service.addReplyCommentsToCollectionIfMissing(replyCommentsCollection, ...replyCommentsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const replyComments: IReplyComments = sampleWithRequiredData;
        const replyComments2: IReplyComments = sampleWithPartialData;
        expectedResult = service.addReplyCommentsToCollectionIfMissing([], replyComments, replyComments2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(replyComments);
        expect(expectedResult).toContain(replyComments2);
      });

      it('should accept null and undefined values', () => {
        const replyComments: IReplyComments = sampleWithRequiredData;
        expectedResult = service.addReplyCommentsToCollectionIfMissing([], null, replyComments, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(replyComments);
      });

      it('should return initial array if no ReplyComments is added', () => {
        const replyCommentsCollection: IReplyComments[] = [sampleWithRequiredData];
        expectedResult = service.addReplyCommentsToCollectionIfMissing(replyCommentsCollection, undefined, null);
        expect(expectedResult).toEqual(replyCommentsCollection);
      });
    });

    describe('compareReplyComments', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareReplyComments(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { replyId: 123 };
        const entity2 = null;

        const compareResult1 = service.compareReplyComments(entity1, entity2);
        const compareResult2 = service.compareReplyComments(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { replyId: 123 };
        const entity2 = { replyId: 456 };

        const compareResult1 = service.compareReplyComments(entity1, entity2);
        const compareResult2 = service.compareReplyComments(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { replyId: 123 };
        const entity2 = { replyId: 123 };

        const compareResult1 = service.compareReplyComments(entity1, entity2);
        const compareResult2 = service.compareReplyComments(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
