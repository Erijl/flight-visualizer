import {Component, OnDestroy, OnInit} from '@angular/core';
import {LiveFeedSpeedModifier} from "../../core/enum";
import {Subscription} from "rxjs";
import {TimeFilter} from "../../protos/filters";
import {DefaultTimeFilter, TimeModifier} from "../../core/dto/airport";
import {DataStoreService} from "../../core/service/data-store.service";

@Component({
  selector: 'app-live-feed-time-panel',
  templateUrl: './live-feed-time-panel.component.html',
  styleUrl: './live-feed-time-panel.component.css'
})
export class LiveFeedTimePanelComponent implements OnInit, OnDestroy {

  timeFilterSubscription!: Subscription;
  timeModifierSubscription!: Subscription;

  selectionType: LiveFeedSpeedModifier = LiveFeedSpeedModifier.ONE_X;
  currentTIme = new Date();

  timeFilter: TimeFilter = TimeFilter.create(DefaultTimeFilter);
  timeModifier: TimeModifier = new TimeModifier(new Date(), LiveFeedSpeedModifier.ONE_X);

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    setInterval(() => {
      this.currentTIme = new Date();
    }, 1000);

    this.timeFilterSubscription = this.dataStoreService.timeFilter.subscribe(timeFilter => {
      this.timeFilter = timeFilter;
    });

    this.timeModifierSubscription = this.dataStoreService.timeModifier.subscribe(timeModifier => {
      this.timeModifier = timeModifier;
    });
  }

  onSpeedModifierChange() {
    this.dataStoreService.setTimeModifier(new TimeModifier(this.timeModifier.dateTime, this.selectionType));
  }

  ngOnDestroy(): void {
    this.timeFilterSubscription.unsubscribe();
    this.timeModifierSubscription.unsubscribe();
  }
}
