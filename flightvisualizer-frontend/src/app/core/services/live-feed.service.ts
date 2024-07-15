import {Injectable, OnDestroy, OnInit} from '@angular/core';
import {LiveFeedSpeedMultiplierString, ModeSelection} from "../enum";
import {BehaviorSubject, interval, Observable, Subscription, switchMap} from "rxjs";
import {DataStoreService} from "./data-store.service";

@Injectable({
  providedIn: 'root'
})
export class LiveFeedService {

  modeSelectionSubscription!: Subscription;
  intervalSubscription?: Subscription;

  private reversed = false;
  private baseDate = new Date();

  private timeMultiplier$ = new BehaviorSubject<number>(32);
  private currentDate$ = new BehaviorSubject<Date>(this.baseDate);

  constructor(private dataStoreService: DataStoreService) {
    this.modeSelectionSubscription = this.dataStoreService.modeSelection.subscribe(modeSelection => {
      if (modeSelection == ModeSelection.LIVE_FEED) {
        this.startInterval();
      } else {
        this.stopInterval();
      }
    });
  }

  startInterval(): void {
    this.intervalSubscription = this.timeMultiplier$.pipe(
      switchMap((modifier) =>
        interval(Math.max(1000 / modifier, 10))
      ),
    ).subscribe(() => {
      const newDate = new Date(this.currentDate$.value.getTime() + ((this.timeMultiplier$.value * 1000) * (this.reversed ? -1 : 1)));
      console.log(newDate);
      this.currentDate$.next(newDate);
    });
  }

  stopInterval(): void {
    if (this.intervalSubscription) {
      this.intervalSubscription.unsubscribe();
      this.intervalSubscription = undefined;
    }
  }

  getCurrentDate$(): Observable<Date> {
    return this.currentDate$.asObservable();
  }

  setTimeMultiplier(modifier: number) {
    this.timeMultiplier$.next(modifier);
  }

  setBaseDate(baseDate: Date) {
    this.baseDate = baseDate;
    this.currentDate$.next(baseDate);
  }

  setReversed(reversed: boolean) {
    this.reversed = reversed;
  }
}