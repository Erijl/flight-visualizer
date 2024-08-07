import { Component, OnDestroy, OnInit } from '@angular/core';
import { AircraftTimeFilterTypeLabels } from "../../../core/enum";
import { AircraftTimeFilterType } from "../../../protos/enums";
import { TimeFilter } from "../../../protos/filters";
import { DefaultTimeFilter } from "../../../core/dto/default-filter";
import { Subscription } from "rxjs";
import { DataStoreService } from "../../../core/services/data-store.service";

@Component({
  selector: 'app-sandbox-time-panel',
  templateUrl: './sandbox-time-panel.component.html',
  styleUrl: './sandbox-time-panel.component.css'
})
export class SandboxTimePanelComponent implements OnInit, OnDestroy {

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

  // Callbacks
  dateFilter = this.getIsDateAvailableInputFilter();

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {

    this.flightDateFrequenciesSubscription = this.dataStoreService.allFlightDateFrequencies.subscribe(frequencies => {
      const offset = new Date().getTimezoneOffset();
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
    // Adjust for timezone because Angular returns a local date (?) (it works, just leave it)
    const offset = new Date().getTimezoneOffset();
    if (this.timeFilter.dateRange && this.timeFilter.dateRange.start) {
      this.timeFilter.dateRange.start = new Date(this.timeFilter.dateRange.start.getTime() + ((offset * 60000) * (-1)));
    }

    if (this.timeFilter.dateRange && this.timeFilter.dateRange.end) {
      this.timeFilter.dateRange.end = new Date(this.timeFilter.dateRange.end.getTime() + ((offset * 60000) * (-1)));
    }

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

  onTimeRangeChange(): void {
    this.dataStoreService.setTimeFilter(this.timeFilter);
  }

  ngOnDestroy(): void {
    this.flightDateFrequenciesSubscription.unsubscribe();
    this.timeFilterSubscription.unsubscribe();
  }

  protected readonly AircraftTimeFilterTypeLabels = AircraftTimeFilterTypeLabels;
}
