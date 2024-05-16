import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataStoreService} from "../core/service/data-store.service";
import {Subscription} from "rxjs";
import {Airport, DefaultGeneralFilter, DefaultRouteFilter} from "../core/dto/airport";
import {FilterService} from "../core/service/filter.service";
import {RouteFilter} from "../protos/filters";
import {AirportDisplayType, RouteDisplayType, RouteFilterType} from "../protos/enums";
import {GeneralFilter} from "../protos/objects";

@Component({
  selector: 'app-filter-panel',
  templateUrl: './filter-panel.component.html',
  styleUrl: './filter-panel.component.css'
})
export class FilterPanelComponent implements OnInit, OnDestroy {

  //Subscriptions
  generalFilterSubscription!: Subscription;
  routeFilterSubscription!: Subscription;
  currentlyRenderedRoutesSubscription!: Subscription;

  // UI data
  airportDisplayTypes = Object.values(AirportDisplayType);
  routeDisplayTypes = Object.values(RouteDisplayType);
  routeFilterTypes = Object.values(RouteFilterType);

  //Filter
  generalFilter: GeneralFilter = DefaultGeneralFilter;
  routeFilter: RouteFilter = DefaultRouteFilter;

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

    this.currentlyRenderedRoutesSubscription = this.dataStoreService.renderedRoutes.subscribe(routes => {
      this.updateSlider();
    });

  }

  onAirportDisplayTypeChange(): void {
    this.dataStoreService.setGeneralFilter(this.generalFilter);
  }

  onRouteDisplayTypeChange(): void {
    this.dataStoreService.setGeneralFilter(this.generalFilter);
  }

  onRouteFilterTypeChange(): void {
    this.updateSlider();

    this.dataStoreService.setRouteFilter(this.routeFilter);
  }

  updateSlider() {
    if (this.routeFilter.routeFilterType == RouteFilterType.DISTANCE) {
      const maxKilometers = this.dataStoreService.getFurthestFlightRoute().kilometerDistance;
      if(this.maxSliderValue != maxKilometers) {
        this.minSliderValue = 0;
        this.maxSliderValue = maxKilometers;

        this.routeFilter.start = 0;
        this.routeFilter.end = maxKilometers;
      }
    } else if (this.routeFilter.routeFilterType == RouteFilterType.DURATION) {
      const maxDuration = this.filterService.calculateFlightDurationInMinutes(this.dataStoreService.getLongestFlightRoute());

      if(this.maxSliderValue != maxDuration) {

        this.minSliderValue = 0;
        this.maxSliderValue = maxDuration;

        this.routeFilter.start = 0;
        this.routeFilter.end = maxDuration;
      }
    }
  }

  onSliderRangeChange(): void {
    this.dataStoreService.setRouteFilter(this.routeFilter);
  }


  resetFilters(): void {
    this.generalFilter = DefaultGeneralFilter;
    this.routeFilter = RouteFilter.create({routeFilterType: RouteFilterType.DISTANCE, start: 0, end: this.dataStoreService.getFurthestFlightRoute()?.kilometerDistance ?? 100000});

    this.dataStoreService.setGeneralFilter(this.generalFilter);
    this.dataStoreService.setRouteFilter(this.routeFilter);
  }

  ngOnDestroy(): void {
    this.generalFilterSubscription.unsubscribe();
    this.routeFilterSubscription.unsubscribe();
    this.currentlyRenderedRoutesSubscription.unsubscribe();
  }

  protected readonly RouteFilterType = RouteFilterType;
}
