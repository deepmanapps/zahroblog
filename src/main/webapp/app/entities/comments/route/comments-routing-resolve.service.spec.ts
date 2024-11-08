import { TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IComments } from '../comments.model';
import { CommentsService } from '../service/comments.service';

import commentsResolve from './comments-routing-resolve.service';

describe('Comments routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: CommentsService;
  let resultComments: IComments | null | undefined;

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
    service = TestBed.inject(CommentsService);
    resultComments = undefined;
  });

  describe('resolve', () => {
    it('should return IComments returned by find', () => {
      // GIVEN
      service.find = jest.fn(commentId => of(new HttpResponse({ body: { commentId } })));
      mockActivatedRouteSnapshot.params = { commentId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        commentsResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultComments = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultComments).toEqual({ commentId: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        commentsResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultComments = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultComments).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IComments>({ body: null })));
      mockActivatedRouteSnapshot.params = { commentId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        commentsResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultComments = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultComments).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
