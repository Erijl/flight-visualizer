import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {DataStoreService} from "./core/service/data-store.service";
import {ToastService} from "./core/service/toast.service";
import {environment} from "../environments/environment";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Flightvisualizer';
  showModal = true;
  renderMap = false;
  showModeSelect = true;
  isLoading = false;

  showLoadingScreenSubscription!: Subscription;

  constructor(private dataStoreService: DataStoreService, public toastService: ToastService) {
    if(!environment.production) {
      console.log('Development mode');
    }
  }

  ngOnInit(): void {
    this.showLoadingScreenSubscription = this.dataStoreService.showLoadingScreen.subscribe((show: boolean) => {
      this.isLoading = show;
    });
  }

  closeModal() {
    this.showModal = false;
    this.renderMap = true;
    this.dataStoreService.setIsInitialized(true);
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

  ngOnDestroy(): void {
    this.showLoadingScreenSubscription.unsubscribe();
  }
}
