import { Component, Input, Output, EventEmitter } from '@angular/core';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css'],
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-in', style({ opacity: 1 })),
      ]),
      transition(':leave', [
        animate('300ms ease-out', style({ opacity: 0 })),
      ]),
    ]),
  ],
})
export class ToastComponent {
  @Input() message!: string;
  @Input() type: 'success' | 'error' | 'info' | 'warning' = 'info';
  @Input() showCloseButton = true;
  @Output() closed = new EventEmitter<void>();

  close() {
    this.closed.emit();
  }

  get icon(): string {
    switch (this.type) {
      case 'success': return 'check_circle';
      case 'error': return 'error';
      case 'info': return 'info';
      case 'warning': return 'warning';
      default: return '';
    }
  }
}
