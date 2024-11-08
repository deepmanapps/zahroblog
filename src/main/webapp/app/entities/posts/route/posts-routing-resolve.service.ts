import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPosts } from '../posts.model';
import { PostsService } from '../service/posts.service';

const postsResolve = (route: ActivatedRouteSnapshot): Observable<null | IPosts> => {
  const id = route.params.postId;
  if (id) {
    return inject(PostsService)
      .find(id)
      .pipe(
        mergeMap((posts: HttpResponse<IPosts>) => {
          if (posts.body) {
            return of(posts.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default postsResolve;
