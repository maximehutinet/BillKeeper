import {Routes} from '@angular/router';
import {canActivate} from './services/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./front/pages/bills/bills-list-page/bills-list-page.component')
      .then(m => m.BillsListPageComponent),
    canActivate: [canActivate]
  },
  {
    path: 'bill/:billId',
    loadComponent: () => import('./front/pages/bills/bill-detail-page/bill-detail-page.component')
      .then(m => m.BillDetailPageComponent),
    canActivate: [canActivate]
  },
  {
    path: 'bill/:billId/edit',
    loadComponent: () => import('./front/pages/bills/edit-bill-page/edit-bill-page.component')
      .then(m => m.EditBillPageComponent),
    canActivate: [canActivate]
  },
  {
    path: 'submissions',
    loadComponent: () => import('./front/pages/submissions/submissions-list-page/submissions-list-page.component')
      .then(m => m.SubmissionsListPageComponent),
    canActivate: [canActivate]
  },
  {
    path: 'submissions/:submissionId',
    loadComponent: () => import('./front/pages/submissions/submission-detail-page/submission-detail-page.component')
      .then(m => m.SubmissionDetailPageComponent),
    canActivate: [canActivate]
  },
  {
    path: 'submissions/:submissionId/edit',
    loadComponent: () => import('./front/pages/submissions/edit-submission-page/edit-submission-page.component')
      .then(m => m.EditSubmissionPageComponent),
    canActivate: [canActivate]
  },
  {
    path: 'settings',
    loadComponent: () => import('./front/pages/settings/settings-page/settings-page.component')
      .then(m => m.SettingsPageComponent),
    canActivate: [canActivate]
  },
  {
    path: 'stats',
    loadComponent: () => import('./front/pages/stats/stats-page/stats-page.component')
      .then(m => m.StatsPageComponent),
    canActivate: [canActivate]
  },
  {
    path: 'profile',
    loadComponent: () => import('./front/pages/users/user-profile-page/user-profile-page.component')
      .then(m => m.UserProfilePageComponent),
    canActivate: [canActivate]
  },
  {
    path: 'invitation/family/:invitationId',
    loadComponent: () => import('./front/pages/invitations/accept-family-invitation-page/accept-family-invitation-page.component')
      .then(m => m.AcceptFamilyInvitationPageComponent),
    canActivate: [canActivate]
  },
  {path: '**', redirectTo: ''}
];
