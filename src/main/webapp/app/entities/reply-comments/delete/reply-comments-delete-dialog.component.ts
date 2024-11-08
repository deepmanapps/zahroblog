import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IReplyComments } from '../reply-comments.model';
import { ReplyCommentsService } from '../service/reply-comments.service';

@Component({
  standalone: true,
  templateUrl: './reply-comments-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ReplyCommentsDeleteDialogComponent {
  replyComments?: IReplyComments;

  protected replyCommentsService = inject(ReplyCommentsService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.replyCommentsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
