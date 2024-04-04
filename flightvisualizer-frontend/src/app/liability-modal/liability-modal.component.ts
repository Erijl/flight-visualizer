import {Component, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'app-liability-modal',
  templateUrl: './liability-modal.component.html',
  styleUrl: './liability-modal.component.css'
})
export class LiabilityModalComponent {
  @Output() closeModalEvent = new EventEmitter();

  closeModal() {
    this.closeModalEvent.emit();
  }
}
