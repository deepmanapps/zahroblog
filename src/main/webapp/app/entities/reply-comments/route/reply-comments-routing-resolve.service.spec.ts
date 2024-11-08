import { TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IReplyComments } from '../reply-comments.model';
import { ReplyCommentsService } from '../service/reply-comments.service';

import replyCommentsResolve from './reply-comments-routing-resolve.service';

describe('ReplyComments routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: ReplyCommentsService;
  let resultReplyComments: IReplyComments | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(ReplyCommentsService);
    resultReplyComments = undefined;
  });

  describe('resolve', () => {
    it('should return IReplyComments returned by find', () => {
      // GIVEN
      service.find = jest.fn(replyId => of(new HttpResponse({ body: { replyId } })));
      mockActivatedRouteSnapshot.params = { replyId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        replyCommentsResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultReplyComments = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultReplyComments).toEqual({ replyId: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        replyCommentsResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultReplyComments = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultReplyComments).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IReplyComments>({ body: null })));
      mockActivatedRouteSnapshot.params = { replyId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        replyCommentsResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultReplyComments = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultReplyComments).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
