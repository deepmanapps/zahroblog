import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TagsResolve from './route/tags-routing-resolve.service';

const tagsRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tags.component').then(m => m.TagsComponent),
    data: {
      defaultSort: `tagId,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':tagId/view',
    loadComponent: () => import('./detail/tags-detail.component').then(m => m.TagsDetailComponent),
    resolve: {
      tags: TagsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tags-update.component').then(m => m.TagsUpdateComponent),
    resolve: {
      tags: TagsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':tagId/edit',
    loadComponent: () => import('./update/tags-update.component').then(m => m.TagsUpdateComponent),
    resolve: {
      tags: TagsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tagsRoute;
