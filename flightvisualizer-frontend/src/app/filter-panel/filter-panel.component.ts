import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataStoreService} from "../core/service/data-store.service";
import {AirportDisplayType, RouteDisplayType, RouteFilterType} from "../core/enum";
import {Subscription} from "rxjs";
import {GeneralFilter, RouteFilter} from "../core/dto/airport";
import {FilterService} from "../core/service/filter.service";

@Component({
  selector: 'app-filter-panel',
  templateUrl: './filter-panel.component.html',
  styleUrl: './filter-panel.component.css'
})
export class FilterPanelComponent implements OnInit, OnDestroy {

  //Subscriptions
  generalFilterSubscription!: Subscription;
  routeFilterSubscription!: Subscription;

  // UI data
  airportDisplayTypes = Object.values(AirportDisplayType);
  routeDisplayTypes = Object.values(RouteDisplayType);
  routeFilterTypes = Object.values(RouteFilterType);

  //Filter
  generalFilter: GeneralFilter = new GeneralFilter(AirportDisplayType.ALL, RouteDisplayType.ALL);
  routeFilter: RouteFilter = new RouteFilter(RouteFilterType.DISTANCE, 0, 1000);

  //UI state
  minSliderValue = 0;
  maxSliderValue = 100;


  constructor(private dataStoreService: DataStoreService, private filterService: FilterService) { }

  ngOnInit(): void {
    this.generalFilterSubscription = this.dataStoreService.generalFilter.subscribe((generalFilter: GeneralFilter) => {
      this.generalFilter = generalFilter;
    });

    this.routeFilterSubscription = this.dataStoreService.routeFilter.subscribe((routeFilter: RouteFilter) => {
      this.routeFilter = routeFilter;
    });
  }

  onAirportDisplayTypeChange(): void {
    this.dataStoreService.setGeneralFilter(this.generalFilter);
  }

  onRouteDisplayTypeChange(): void {
    this.dataStoreService.setGeneralFilter(this.generalFilter);
  }

  onRouteFilterTypeChange(): void {
    if (this.routeFilter.routeFilterType == RouteFilterType.DISTANCE) {
      const maxKilometers = this.dataStoreService.getFurthestFlightRoute().kilometerDistance;
      this.minSliderValue = 0;
      this.maxSliderValue = maxKilometers;

      this.routeFilter.start = 0;
      this.routeFilter.end = maxKilometers;
    } else if (this.routeFilter.routeFilterType == RouteFilterType.DURATION) {
      const maxDuration = this.filterService.calculateFlightDurationInMinutes(this.dataStoreService.getLongestFlightRoute());

      this.minSliderValue = 0;
      this.maxSliderValue = maxDuration;

      this.routeFilter.start = 0;
      this.routeFilter.end = maxDuration;
    }

    this.dataStoreService.setRouteFilter(this.routeFilter);
  }

  onSliderRangeChange(): void {
    this.dataStoreService.setRouteFilter(this.routeFilter);
  }

  //TODO convert to pipe
  protected convertIntToTimeOfDay(value: number | null): string {
    if(value == null) return '';
    let hours = Math.floor((value)/60);
    let minutes = Math.floor((value)%60);

    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
  }

  ngOnDestroy(): void {
    this.generalFilterSubscription.unsubscribe();
    this.routeFilterSubscription.unsubscribe();
  }

  protected readonly RouteFilterType = RouteFilterType;
}
