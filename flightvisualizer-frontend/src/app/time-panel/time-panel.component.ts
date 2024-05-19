import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {DataStoreService} from "../core/service/data-store.service";
import {DefaultTimeFilter} from "../core/dto/airport";
import {state, style, trigger} from "@angular/animations";
import {Subscription} from "rxjs";
import {AircraftTimeFilterType} from "../protos/enums";
import {TimeFilter} from "../protos/filters";
import {AircraftTimeFilterTypeLabels} from "../core/enum";

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
  timeFilter: TimeFilter = TimeFilter.create(DefaultTimeFilter);
  aircraftTimeFilterTypes = [
    AircraftTimeFilterType.ARRIVALANDDEPARTURE,
    AircraftTimeFilterType.DEPARTURE,
    AircraftTimeFilterType.ARRIVAL
  ];
  aircraftTimeFilterType: AircraftTimeFilterType = AircraftTimeFilterType.ARRIVALANDDEPARTURE;
  allowedDates: Date[] = [];

  // UI State
  panelOpenState = false;
  expanded: boolean = true;

  // Callbacks
  dateFilter = this.getIsDateAvailableInputFilter();

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {

    this.flightDateFrequenciesSubscription = this.dataStoreService.allFlightDateFrequencies.subscribe(frequencies => {
      this.allowedDates = frequencies.map(frequency => frequency!.date!);
    });

    this.timeFilterSubscription = this.dataStoreService.timeFilter.subscribe(timeFilter => {
      this.timeFilter = timeFilter;
    });
  }

  getIsDateAvailableInputFilter() {
    return (d: Date | null): boolean => {
      if (!d) return false;
      return this.allowedDates.some(allowedDate => allowedDate.toDateString() == d.toDateString());
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
    this.dataStoreService.initTimeFilter();
  }

  onTimeRangeChange(): void  {
    this.dataStoreService.setTimeFilter(this.timeFilter);
  }

  ngOnDestroy(): void {
    this.flightDateFrequenciesSubscription.unsubscribe();
  }

  protected readonly AircraftTimeFilterTypeLabels = AircraftTimeFilterTypeLabels;
}
