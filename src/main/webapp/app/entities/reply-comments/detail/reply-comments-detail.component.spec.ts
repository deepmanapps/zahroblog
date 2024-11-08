import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReplyCommentsDetailComponent } from './reply-comments-detail.component';

describe('ReplyComments Management Detail Component', () => {
  let comp: ReplyCommentsDetailComponent;
  let fixture: ComponentFixture<ReplyCommentsDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReplyCommentsDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./reply-comments-detail.component').then(m => m.ReplyCommentsDetailComponent),
              resolve: { replyComments: () => of({ replyId: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ReplyCommentsDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReplyCommentsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load replyComments on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReplyCommentsDetailComponent);

      // THEN
      expect(instance.replyComments()).toEqual(expect.objectContaining({ replyId: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
