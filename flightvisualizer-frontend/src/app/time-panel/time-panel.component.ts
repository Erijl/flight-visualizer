import {Component, OnDestroy, OnInit} from '@angular/core';
import {FilterService} from "../core/service/filter.service";
import {DataStoreService} from "../core/service/data-store.service";
import {SelectedDateRange, SelectedTimeRange} from "../core/dto/airport";
import {state, style, trigger} from "@angular/animations";
import {Subscription} from "rxjs";
import {RouteDisplayType} from "../core/enum";

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

  //UI Data
  flightDateFrequencies: Set<string> = new Set();


  // UI State
  selectedTimeRange: SelectedTimeRange = new SelectedTimeRange(this.minTime, this.maxTime);
  selectedDateRange: SelectedDateRange = new SelectedDateRange(new Date(), new Date());

  panelOpenState = false;

  // Callbacks
  dateFilter = this.getIsDateAvailableInputFilter();

  constructor(private filterService: FilterService, private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.selectedDateRange = this.dataStoreService.getSelectedDateRange();

    this.flightDateFrequenciesSubscription = this.dataStoreService.allFlightDateFrequencies.subscribe(frequencies => {
      const dates = frequencies.map(frequency => {
        const date = new Date(frequency.startDateUtc);
        return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
      });
      this.flightDateFrequencies = new Set(dates);
      this.dateFilter = this.getIsDateAvailableInputFilter();
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
    this.dataStoreService.setSelectedDateRange(this.selectedDateRange);
    this.selectedTimeRange = new SelectedTimeRange(this.minTime, this.maxTime);
    //this.routeDisplayType = RouteDisplayType.ALL;
    this.onTimeRangeChange();
  }

  protected convertIntToTimeOfDay(value: number | null): string {
    if(value == null) return '';
    let hours = Math.floor((value)/60);
    let minutes = Math.floor((value)%60);

    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
  }

  onTimeRangeChange(): void  {
    this.dataStoreService.setSelectedTimeRange(this.selectedTimeRange);
  }

  ngOnDestroy(): void {
    this.flightDateFrequenciesSubscription.unsubscribe();
  }
}
