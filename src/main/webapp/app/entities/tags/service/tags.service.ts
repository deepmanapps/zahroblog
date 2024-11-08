import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITags, NewTags } from '../tags.model';

export type PartialUpdateTags = Partial<ITags> & Pick<ITags, 'tagId'>;

export type EntityResponseType = HttpResponse<ITags>;
export type EntityArrayResponseType = HttpResponse<ITags[]>;

@Injectable({ providedIn: 'root' })
export class TagsService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tags');

  create(tags: NewTags): Observable<EntityResponseType> {
    return this.http.post<ITags>(this.resourceUrl, tags, { observe: 'response' });
  }

  update(tags: ITags): Observable<EntityResponseType> {
    return this.http.put<ITags>(`${this.resourceUrl}/${this.getTagsIdentifier(tags)}`, tags, { observe: 'response' });
  }

  partialUpdate(tags: PartialUpdateTags): Observable<EntityResponseType> {
    return this.http.patch<ITags>(`${this.resourceUrl}/${this.getTagsIdentifier(tags)}`, tags, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITags>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITags[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTagsIdentifier(tags: Pick<ITags, 'tagId'>): number {
    return tags.tagId;
  }

  compareTags(o1: Pick<ITags, 'tagId'> | null, o2: Pick<ITags, 'tagId'> | null): boolean {
    return o1 && o2 ? this.getTagsIdentifier(o1) === this.getTagsIdentifier(o2) : o1 === o2;
  }

  addTagsToCollectionIfMissing<Type extends Pick<ITags, 'tagId'>>(
    tagsCollection: Type[],
    ...tagsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tags: Type[] = tagsToCheck.filter(isPresent);
    if (tags.length > 0) {
      const tagsCollectionIdentifiers = tagsCollection.map(tagsItem => this.getTagsIdentifier(tagsItem));
      const tagsToAdd = tags.filter(tagsItem => {
        const tagsIdentifier = this.getTagsIdentifier(tagsItem);
        if (tagsCollectionIdentifiers.includes(tagsIdentifier)) {
          return false;
        }
        tagsCollectionIdentifiers.push(tagsIdentifier);
        return true;
      });
      return [...tagsToAdd, ...tagsCollection];
    }
    return tagsCollection;
  }
}
