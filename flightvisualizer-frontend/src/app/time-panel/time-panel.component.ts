import {Component, OnDestroy, OnInit} from '@angular/core';
import {FilterService} from "../core/service/filter.service";
import {DataStoreService} from "../core/service/data-store.service";
import {DateRange, DefaultTimeFilter, TimeFilter, TimeRange} from "../core/dto/airport";
import {state, style, trigger} from "@angular/animations";
import {Subscription} from "rxjs";
import {AircraftTimeFilterType, RouteDisplayType} from "../core/enum";

@Component({
  selector: 'app-time-panel',
  animations: [
    trigger('openClose', [
      // ...
      state('open', style({
        transition: 'transform 0.5s',
        transform: 'rotate(0deg)'
      })),
      state('closed', style({
        transition: 'transform 0.5s',
        transform: 'rotate(180deg)'
      })),
    ]),
  ],
  templateUrl: './time-panel.component.html',
  styleUrl: './time-panel.component.css'
})
export class TimePanelComponent implements OnInit, OnDestroy {

  // Constants
  maxTime = 1439;
  minTime = 0;

  // Subscriptions
  flightDateFrequenciesSubscription!: Subscription;
  timeFilterSubscription!: Subscription;

  //UI Data
  flightDateFrequencies: Set<string> = new Set();
  timeFilter: TimeFilter = DefaultTimeFilter;
  aircraftTimeFilterTypes = Object.values(AircraftTimeFilterType);
  aircraftTimeFilterType: AircraftTimeFilterType = AircraftTimeFilterType.ARRIVALANDDEPARTURE;


  // UI State
  panelOpenState = false;

  // Callbacks
  dateFilter = this.getIsDateAvailableInputFilter();

  constructor(private filterService: FilterService, private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {

    this.flightDateFrequenciesSubscription = this.dataStoreService.allFlightDateFrequencies.subscribe(frequencies => {
      const dates = frequencies.map(frequency => {
        const date = new Date(frequency.startDateUtc);
        return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
      });
      this.flightDateFrequencies = new Set(dates);
      this.dateFilter = this.getIsDateAvailableInputFilter();
    });

    this.timeFilterSubscription = this.dataStoreService.timeFilter.subscribe(timeFilter => {
      this.timeFilter = timeFilter;
    });
  }

  getIsDateAvailableInputFilter() {
    return (date: Date | null): boolean => {
      if (!date || !this.flightDateFrequencies) {
        return false;
      }
      const dateString = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
      return this.flightDateFrequencies.has(dateString);
    };
  }

  onDateRangeChange(): void {
    this.dataStoreService.setTimeFilter(this.timeFilter);
  }

  onAircraftTimeFilterTypeChange(): void {
    this.timeFilter.aircraftDepOrArrInTimeRange = this.aircraftTimeFilterType;
    this.dataStoreService.setTimeFilter(this.timeFilter);
  }
  onTimeRangeInvertChange(): void {
    this.timeFilter.includeDifferentDayDepartures = true;
    this.timeFilter.includeDifferentDayArrivals = true;
    this.dataStoreService.setTimeFilter(this.timeFilter);
  }

  onIncludeMultiDayFlightChange(): void {
    this.dataStoreService.setTimeFilter(this.timeFilter);
  }

  resetFilters(): void {
    this.timeFilter = DefaultTimeFilter;
    this.dataStoreService.setTimeFilter(this.timeFilter);

  }

  onTimeRangeChange(): void  {
    this.dataStoreService.setTimeFilter(this.timeFilter);
  }

  ngOnDestroy(): void {
    this.flightDateFrequenciesSubscription.unsubscribe();
  }
}
