import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PostsResolve from './route/posts-routing-resolve.service';

const postsRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/posts.component').then(m => m.PostsComponent),
    data: {
      defaultSort: `postId,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':postId/view',
    loadComponent: () => import('./detail/posts-detail.component').then(m => m.PostsDetailComponent),
    resolve: {
      posts: PostsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/posts-update.component').then(m => m.PostsUpdateComponent),
    resolve: {
      posts: PostsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':postId/edit',
    loadComponent: () => import('./update/posts-update.component').then(m => m.PostsUpdateComponent),
    resolve: {
      posts: PostsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default postsRoute;
