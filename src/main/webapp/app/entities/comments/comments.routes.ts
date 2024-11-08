import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CommentsResolve from './route/comments-routing-resolve.service';

const commentsRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/comments.component').then(m => m.CommentsComponent),
    data: {
      defaultSort: `commentId,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':commentId/view',
    loadComponent: () => import('./detail/comments-detail.component').then(m => m.CommentsDetailComponent),
    resolve: {
      comments: CommentsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/comments-update.component').then(m => m.CommentsUpdateComponent),
    resolve: {
      comments: CommentsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':commentId/edit',
    loadComponent: () => import('./update/comments-update.component').then(m => m.CommentsUpdateComponent),
    resolve: {
      comments: CommentsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default commentsRoute;
