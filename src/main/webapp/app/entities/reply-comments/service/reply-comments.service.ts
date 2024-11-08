import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReplyComments, NewReplyComments } from '../reply-comments.model';

export type PartialUpdateReplyComments = Partial<IReplyComments> & Pick<IReplyComments, 'replyId'>;

type RestOf<T extends IReplyComments | NewReplyComments> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestReplyComments = RestOf<IReplyComments>;

export type NewRestReplyComments = RestOf<NewReplyComments>;

export type PartialUpdateRestReplyComments = RestOf<PartialUpdateReplyComments>;

export type EntityResponseType = HttpResponse<IReplyComments>;
export type EntityArrayResponseType = HttpResponse<IReplyComments[]>;

@Injectable({ providedIn: 'root' })
export class ReplyCommentsService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reply-comments');

  create(replyComments: NewReplyComments): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(replyComments);
    return this.http
      .post<RestReplyComments>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(replyComments: IReplyComments): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(replyComments);
    return this.http
      .put<RestReplyComments>(`${this.resourceUrl}/${this.getReplyCommentsIdentifier(replyComments)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(replyComments: PartialUpdateReplyComments): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(replyComments);
    return this.http
      .patch<RestReplyComments>(`${this.resourceUrl}/${this.getReplyCommentsIdentifier(replyComments)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestReplyComments>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReplyComments[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReplyCommentsIdentifier(replyComments: Pick<IReplyComments, 'replyId'>): number {
    return replyComments.replyId;
  }

  compareReplyComments(o1: Pick<IReplyComments, 'replyId'> | null, o2: Pick<IReplyComments, 'replyId'> | null): boolean {
    return o1 && o2 ? this.getReplyCommentsIdentifier(o1) === this.getReplyCommentsIdentifier(o2) : o1 === o2;
  }

  addReplyCommentsToCollectionIfMissing<Type extends Pick<IReplyComments, 'replyId'>>(
    replyCommentsCollection: Type[],
    ...replyCommentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const replyComments: Type[] = replyCommentsToCheck.filter(isPresent);
    if (replyComments.length > 0) {
      const replyCommentsCollectionIdentifiers = replyCommentsCollection.map(replyCommentsItem =>
        this.getReplyCommentsIdentifier(replyCommentsItem),
      );
      const replyCommentsToAdd = replyComments.filter(replyCommentsItem => {
        const replyCommentsIdentifier = this.getReplyCommentsIdentifier(replyCommentsItem);
        if (replyCommentsCollectionIdentifiers.includes(replyCommentsIdentifier)) {
          return false;
        }
        replyCommentsCollectionIdentifiers.push(replyCommentsIdentifier);
        return true;
      });
      return [...replyCommentsToAdd, ...replyCommentsCollection];
    }
    return replyCommentsCollection;
  }

  protected convertDateFromClient<T extends IReplyComments | NewReplyComments | PartialUpdateReplyComments>(replyComments: T): RestOf<T> {
    return {
      ...replyComments,
      createdAt: replyComments.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReplyComments: RestReplyComments): IReplyComments {
    return {
      ...restReplyComments,
      createdAt: restReplyComments.createdAt ? dayjs(restReplyComments.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReplyComments>): HttpResponse<IReplyComments> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReplyComments[]>): HttpResponse<IReplyComments[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
