import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IPosts } from '../posts.model';

@Component({
  standalone: true,
  selector: 'jhi-posts-detail',
  templateUrl: './posts-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PostsDetailComponent {
  posts = input<IPosts | null>(null);

  previousState(): void {
    window.history.back();
  }
}
