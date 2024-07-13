import { Injectable } from '@angular/core';
import {LiveFeedSpeedModifier} from "../enum";
import {BehaviorSubject, interval, Observable, switchMap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LiveFeedService {

  private reversed = false;
  private baseDate = new Date();


  private timeMultiplier$ = new BehaviorSubject<LiveFeedSpeedModifier>(LiveFeedSpeedModifier.THIRTYTWO_X);
  private currentDate$ = new BehaviorSubject<Date>(this.baseDate);

  constructor() {
    this.timeMultiplier$.pipe(
      switchMap((modifier) =>
        interval(Math.max(1000 / modifier, 10))
      ),
    ).subscribe(() => {
      const newDate = new Date(this.currentDate$.value.getTime() + ((this.timeMultiplier$.value * 1000) * (this.reversed ? -1 : 1)));
      this.currentDate$.next(newDate);
    });
  }

  getCurrentDate$(): Observable<Date> {
    return this.currentDate$.asObservable();
  }

  setTimeModifier(modifier: number) {
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
