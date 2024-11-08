import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ReplyCommentsResolve from './route/reply-comments-routing-resolve.service';

const replyCommentsRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/reply-comments.component').then(m => m.ReplyCommentsComponent),
    data: {
      defaultSort: `replyId,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':replyId/view',
    loadComponent: () => import('./detail/reply-comments-detail.component').then(m => m.ReplyCommentsDetailComponent),
    resolve: {
      replyComments: ReplyCommentsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/reply-comments-update.component').then(m => m.ReplyCommentsUpdateComponent),
    resolve: {
      replyComments: ReplyCommentsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':replyId/edit',
    loadComponent: () => import('./update/reply-comments-update.component').then(m => m.ReplyCommentsUpdateComponent),
    resolve: {
      replyComments: ReplyCommentsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default replyCommentsRoute;
