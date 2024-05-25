import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {ToastComponent} from "../../toast/toast.component";

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toastSubject = new BehaviorSubject<ToastComponent | null>(null);
  toast$ = this.toastSubject.asObservable();

  showToast(message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info', icon?: string) {
    const toast = new ToastComponent();
    toast.message = message;
    toast.type = type;

    this.toastSubject.next(toast);

    setTimeout(() => this.toastSubject.next(null), 10000);
  }

  clear() {
    this.toastSubject.next(null);
  }
}
