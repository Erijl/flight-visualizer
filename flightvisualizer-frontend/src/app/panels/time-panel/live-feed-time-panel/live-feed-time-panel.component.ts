import { Component, OnDestroy, OnInit } from '@angular/core';
import { LiveFeedSpeedMultiplier, LiveFeedSpeedMultiplierString } from "../../../core/enum";
import { Observable, Subscription } from "rxjs";
import { TimeFilter } from "../../../protos/filters";
import { DefaultTimeFilter } from "../../../core/dto/default-filter";
import { DataStoreService } from "../../../core/services/data-store.service";
import { LiveFeedService } from "../../../core/services/live-feed.service";

@Component({
  selector: 'app-live-feed-time-panel',
  templateUrl: './live-feed-time-panel.component.html',
  styleUrl: './live-feed-time-panel.component.css'
})
export class LiveFeedTimePanelComponent implements OnInit, OnDestroy {

  timeFilterSubscription!: Subscription;

  selectionType: LiveFeedSpeedMultiplierString = LiveFeedSpeedMultiplierString.THIRTYTWO_X;
  currentTime: Date = new Date();
  reversed = false;

  currentDate$!: Observable<Date>;


  timeFilter: TimeFilter = TimeFilter.create(DefaultTimeFilter);

  constructor(private dataStoreService: DataStoreService, private liveFeedService: LiveFeedService) {
  }

  ngOnInit(): void {
    this.currentDate$ = this.liveFeedService.getCurrentDate$();

    this.currentDate$.subscribe((newDateObj) => {
      this.currentTime = newDateObj;
    });

    this.timeFilterSubscription = this.dataStoreService.timeFilter.subscribe(timeFilter => {
      this.timeFilter = timeFilter;
    });

  }

  onSpeedModifierChange() {
    this.liveFeedService.setTimeMultiplier(parseInt(LiveFeedSpeedMultiplier[this.selectionType]));
  }

  onReverseChange() {
    this.liveFeedService.setReversed(this.reversed);
  }

  onResetDate() {
    this.liveFeedService.setBaseDate(new Date());
  }

  ngOnDestroy(): void {
    this.timeFilterSubscription.unsubscribe();
  }

}
