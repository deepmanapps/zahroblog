import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReplyComments } from '../reply-comments.model';
import { ReplyCommentsService } from '../service/reply-comments.service';

const replyCommentsResolve = (route: ActivatedRouteSnapshot): Observable<null | IReplyComments> => {
  const id = route.params.replyId;
  if (id) {
    return inject(ReplyCommentsService)
      .find(id)
      .pipe(
        mergeMap((replyComments: HttpResponse<IReplyComments>) => {
          if (replyComments.body) {
            return of(replyComments.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default replyCommentsResolve;
