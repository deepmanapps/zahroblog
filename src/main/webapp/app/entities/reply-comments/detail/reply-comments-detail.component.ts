import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IReplyComments } from '../reply-comments.model';

@Component({
  standalone: true,
  selector: 'jhi-reply-comments-detail',
  templateUrl: './reply-comments-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ReplyCommentsDetailComponent {
  replyComments = input<IReplyComments | null>(null);

  previousState(): void {
    window.history.back();
  }
}
