import {Component, OnDestroy, OnInit} from '@angular/core';
import {LiveFeedSpeedModifier} from "../../core/enum";
import {Observable, Subscription} from "rxjs";
import {TimeFilter} from "../../protos/filters";
import {DefaultTimeFilter, TimeModifier} from "../../core/dto/airport";
import {DataStoreService} from "../../core/service/data-store.service";
import {LiveFeedService} from "../../core/service/live-feed.service";

@Component({
  selector: 'app-live-feed-time-panel',
  templateUrl: './live-feed-time-panel.component.html',
  styleUrl: './live-feed-time-panel.component.css'
})
export class LiveFeedTimePanelComponent implements OnInit, OnDestroy {

  timeFilterSubscription!: Subscription;
  timeModifierSubscription!: Subscription;

  selectionType: LiveFeedSpeedModifier = LiveFeedSpeedModifier.ONE_X;
  currentTime: Date = new Date();

  currentDate$!: Observable<Date>;


  timeFilter: TimeFilter = TimeFilter.create(DefaultTimeFilter);
  timeModifier: TimeModifier = new TimeModifier(new Date(), LiveFeedSpeedModifier.ONE_X);

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

    this.timeModifierSubscription = this.dataStoreService.timeModifier.subscribe(timeModifier => {
      this.timeModifier = timeModifier;
    });
  }

  onSpeedModifierChange() {
    //this.dataStoreService.setTimeModifier(new TimeModifier(this.timeModifier.dateTime, this.selectionType));
    this.liveFeedService.setTimeModifier(this.selectionType);
  }

  ngOnDestroy(): void {
    this.timeFilterSubscription.unsubscribe();
    this.timeModifierSubscription.unsubscribe();
  }
}
