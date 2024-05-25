import {Component, HostListener} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Flightvisualizer';
  showModal = true;
  renderMap = false;
  showModeSelect = true;
  isLoading = false;

  closeModal() {
    this.showModal = false;
    this.renderMap = true;
  }

  selectMode(mode: number) {
    this.showModeSelect = false;
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.key === 'Escape' && !this.showModeSelect) {
      this.showModeSelect = true;
    }
  }
}
