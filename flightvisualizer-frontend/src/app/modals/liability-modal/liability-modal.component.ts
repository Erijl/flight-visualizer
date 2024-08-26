import { Component, EventEmitter, OnInit, Output } from '@angular/core';

enum ModalState {
  LIABILITY = 'LIABILITY',
  PRIVACY_POLICY = 'PRIVACY_POLICY',
  IMPRINT = 'IMPRINT',
  WELCOME = 'WELCOME',
  CREDITS = 'CREDITS'
}

@Component({
  selector: 'app-liability-modal',
  templateUrl: './liability-modal.component.html',
  styleUrl: './liability-modal.component.css'
})
export class LiabilityModalComponent implements OnInit{
  @Output() closeModalEvent = new EventEmitter();
  modalState = ModalState.WELCOME;

  innerScreenWidth = 1000;

  closeModal() {
    this.closeModalEvent.emit();
  }

  changeModal(state: ModalState) {
    this.modalState = state;
  }

  protected readonly ModalState = ModalState;

  ngOnInit(): void {
    this.innerScreenWidth = window.innerWidth;
  }
}
