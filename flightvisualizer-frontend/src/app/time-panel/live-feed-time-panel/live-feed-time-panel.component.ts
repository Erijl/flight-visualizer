import {Component, OnDestroy, OnInit} from '@angular/core';
import {LiveFeedSpeedModifier} from "../../core/enum";
import {Subscription} from "rxjs";
import {TimeFilter} from "../../protos/filters";
import {DefaultTimeFilter} from "../../core/dto/airport";
import {DataStoreService} from "../../core/service/data-store.service";

@Component({
  selector: 'app-live-feed-time-panel',
  templateUrl: './live-feed-time-panel.component.html',
  styleUrl: './live-feed-time-panel.component.css'
})
export class LiveFeedTimePanelComponent implements OnInit, OnDestroy {

  timeFilterSubscription!: Subscription;

  selectionType: LiveFeedSpeedModifier = LiveFeedSpeedModifier.ONE_X;
  currentTIme = new Date();

  timeFilter: TimeFilter = TimeFilter.create(DefaultTimeFilter);

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    setInterval(() => {
      this.currentTIme = new Date();
    }, 1000);

    this.timeFilterSubscription = this.dataStoreService.timeFilter.subscribe(timeFilter => {
      this.timeFilter = timeFilter;
    });
  }

  onSpeedModifierChange() {

  }

  ngOnDestroy(): void {
    this.timeFilterSubscription.unsubscribe();
  }
}
