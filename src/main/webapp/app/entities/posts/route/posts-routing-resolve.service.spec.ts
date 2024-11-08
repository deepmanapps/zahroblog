import { TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IPosts } from '../posts.model';
import { PostsService } from '../service/posts.service';

import postsResolve from './posts-routing-resolve.service';

describe('Posts routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: PostsService;
  let resultPosts: IPosts | null | undefined;

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
    service = TestBed.inject(PostsService);
    resultPosts = undefined;
  });

  describe('resolve', () => {
    it('should return IPosts returned by find', () => {
      // GIVEN
      service.find = jest.fn(postId => of(new HttpResponse({ body: { postId } })));
      mockActivatedRouteSnapshot.params = { postId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        postsResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPosts = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultPosts).toEqual({ postId: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        postsResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPosts = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPosts).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IPosts>({ body: null })));
      mockActivatedRouteSnapshot.params = { postId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        postsResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPosts = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultPosts).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
