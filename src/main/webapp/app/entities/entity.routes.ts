import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'zahroApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'comments',
    data: { pageTitle: 'zahroApp.comments.home.title' },
    loadChildren: () => import('./comments/comments.routes'),
  },
  {
    path: 'posts',
    data: { pageTitle: 'zahroApp.posts.home.title' },
    loadChildren: () => import('./posts/posts.routes'),
  },
  {
    path: 'reply-comments',
    data: { pageTitle: 'zahroApp.replyComments.home.title' },
    loadChildren: () => import('./reply-comments/reply-comments.routes'),
  },
  {
    path: 'tags',
    data: { pageTitle: 'zahroApp.tags.home.title' },
    loadChildren: () => import('./tags/tags.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
