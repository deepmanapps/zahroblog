import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITags } from '../tags.model';
import { TagsService } from '../service/tags.service';

const tagsResolve = (route: ActivatedRouteSnapshot): Observable<null | ITags> => {
  const id = route.params.tagId;
  if (id) {
    return inject(TagsService)
      .find(id)
      .pipe(
        mergeMap((tags: HttpResponse<ITags>) => {
          if (tags.body) {
            return of(tags.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default tagsResolve;
