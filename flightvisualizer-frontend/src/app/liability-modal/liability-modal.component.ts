import {Component, EventEmitter, Output} from '@angular/core';

enum ModalState {
  LIABILITY = 'LIABILITY',
  PRIVACY_POLICY = 'PRIVACY_POLICY',
  IMPRINT = 'IMPRINT'
}

@Component({
  selector: 'app-liability-modal',
  templateUrl: './liability-modal.component.html',
  styleUrl: './liability-modal.component.css'
})
export class LiabilityModalComponent {
  @Output() closeModalEvent = new EventEmitter();
  modalState = ModalState.LIABILITY;

  closeModal() {
    this.closeModalEvent.emit();
  }

  changeModal(state: ModalState) {
    this.modalState = state;
  }

  protected readonly ModalState = ModalState;
}
