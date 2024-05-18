import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataStoreService} from "../core/service/data-store.service";
import {Subscription} from "rxjs";
import {DefaultGeneralFilter, DefaultRouteFilter} from "../core/dto/airport";
import {FilterService} from "../core/service/filter.service";
import {GeneralFilter, RouteFilter} from "../protos/filters";
import {AirportDisplayType, RouteDisplayType, RouteFilterType} from "../protos/enums";

@Component({
  selector: 'app-filter-panel',
  templateUrl: './filter-panel.component.html',
  styleUrl: './filter-panel.component.css'
})
export class FilterPanelComponent implements OnInit, OnDestroy {

  //static values
  MAX_DISTANCE = 20000;

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


  constructor(private dataStoreService: DataStoreService, private filterService: FilterService) {
  }

  ngOnInit(): void {
    this.generalFilterSubscription = this.dataStoreService.generalFilter.subscribe((generalFilter: GeneralFilter) => {
      this.generalFilter = generalFilter;
    });

    this.routeFilterSubscription = this.dataStoreService.routeFilter.subscribe((routeFilter: RouteFilter) => {
      this.routeFilter = routeFilter;
    });

    this.currentlyRenderedRoutesSubscription = this.dataStoreService.renderedRoutes.subscribe(routes => {
      console.log('filterpanel rendered Routes');
      console.log(this.dataStoreService.getFurthestFlightLeg());
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
      const furthestLeg = this.dataStoreService.getFurthestFlightLeg();
      const furthesDistance = furthestLeg.distanceKilometers != 0 ? furthestLeg.distanceKilometers : this.MAX_DISTANCE;
      if (this.maxSliderValue != furthesDistance) {
        this.minSliderValue = 0;
        this.maxSliderValue = furthesDistance;

        this.routeFilter.start = 0;
        this.routeFilter.end = furthesDistance;
      }
    } else if (this.routeFilter.routeFilterType == RouteFilterType.DURATION) {
      const maxDuration = this.dataStoreService.getLongestFlightLeg().durationMinutes;

      if (this.maxSliderValue != maxDuration) {

        this.minSliderValue = 0;
        this.maxSliderValue = maxDuration;

        this.routeFilter.start = 0;
        this.routeFilter.end = maxDuration;
      }
    }
  }

  onSliderRangeChange(): void {
    console.log(this.routeFilter);
    this.dataStoreService.setRouteFilter(this.routeFilter);
  }

  resetFilters(): void {
    this.generalFilter = DefaultGeneralFilter;
    const furthestFlightLeg = this.dataStoreService.getFurthestFlightLeg();
    if (furthestFlightLeg.distanceKilometers != 0) {
      this.routeFilter = RouteFilter.create({
        routeFilterType: RouteFilterType.DISTANCE,
        start: 0,
        end: furthestFlightLeg.distanceKilometers
      });
    } else {
      this.routeFilter = DefaultRouteFilter;
    }

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
