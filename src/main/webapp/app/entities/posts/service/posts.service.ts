import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPosts, NewPosts } from '../posts.model';

export type PartialUpdatePosts = Partial<IPosts> & Pick<IPosts, 'postId'>;

type RestOf<T extends IPosts | NewPosts> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestPosts = RestOf<IPosts>;

export type NewRestPosts = RestOf<NewPosts>;

export type PartialUpdateRestPosts = RestOf<PartialUpdatePosts>;

export type EntityResponseType = HttpResponse<IPosts>;
export type EntityArrayResponseType = HttpResponse<IPosts[]>;

@Injectable({ providedIn: 'root' })
export class PostsService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/posts');

  create(posts: NewPosts): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(posts);
    return this.http.post<RestPosts>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(posts: IPosts): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(posts);
    return this.http
      .put<RestPosts>(`${this.resourceUrl}/${this.getPostsIdentifier(posts)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(posts: PartialUpdatePosts): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(posts);
    return this.http
      .patch<RestPosts>(`${this.resourceUrl}/${this.getPostsIdentifier(posts)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPosts>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPosts[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPostsIdentifier(posts: Pick<IPosts, 'postId'>): number {
    return posts.postId;
  }

  comparePosts(o1: Pick<IPosts, 'postId'> | null, o2: Pick<IPosts, 'postId'> | null): boolean {
    return o1 && o2 ? this.getPostsIdentifier(o1) === this.getPostsIdentifier(o2) : o1 === o2;
  }

  addPostsToCollectionIfMissing<Type extends Pick<IPosts, 'postId'>>(
    postsCollection: Type[],
    ...postsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const posts: Type[] = postsToCheck.filter(isPresent);
    if (posts.length > 0) {
      const postsCollectionIdentifiers = postsCollection.map(postsItem => this.getPostsIdentifier(postsItem));
      const postsToAdd = posts.filter(postsItem => {
        const postsIdentifier = this.getPostsIdentifier(postsItem);
        if (postsCollectionIdentifiers.includes(postsIdentifier)) {
          return false;
        }
        postsCollectionIdentifiers.push(postsIdentifier);
        return true;
      });
      return [...postsToAdd, ...postsCollection];
    }
    return postsCollection;
  }

  protected convertDateFromClient<T extends IPosts | NewPosts | PartialUpdatePosts>(posts: T): RestOf<T> {
    return {
      ...posts,
      createdAt: posts.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPosts: RestPosts): IPosts {
    return {
      ...restPosts,
      createdAt: restPosts.createdAt ? dayjs(restPosts.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPosts>): HttpResponse<IPosts> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPosts[]>): HttpResponse<IPosts[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
