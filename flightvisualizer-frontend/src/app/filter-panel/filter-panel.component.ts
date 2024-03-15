import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataStoreService} from "../core/service/data-store.service";
import {AirportDisplayType, RouteDisplayType, RouteFilterType} from "../core/enum";
import {Subscription} from "rxjs";
import {GeneralFilter} from "../core/dto/airport";

@Component({
  selector: 'app-filter-panel',
  templateUrl: './filter-panel.component.html',
  styleUrl: './filter-panel.component.css'
})
export class FilterPanelComponent implements OnInit, OnDestroy {

  //Subscriptions
  generalFilterSubscription!: Subscription;

  // UI data
  airportDisplayTypes = Object.values(AirportDisplayType);
  routeDisplayTypes = Object.values(RouteDisplayType);
  routeFilterTypes = Object.values(RouteFilterType);
  routeFilterType: RouteFilterType = RouteFilterType.DISTANCE;

  //Filter
  generalFilter: GeneralFilter = new GeneralFilter(AirportDisplayType.ALL, RouteDisplayType.ALL);

  //UI state
  histogramData: number[] = [];

  lowerValue = 10;
  upperValue = 90;
  constructor(private dataStoreService: DataStoreService) { }

  ngOnInit(): void {
    this.generalFilterSubscription = this.dataStoreService.generalFilter.subscribe((generalFilter: GeneralFilter) => {
      this.generalFilter = generalFilter;
    });
  }

  onAirportDisplayTypeChange(): void {
    this.dataStoreService.setGeneralFilter(this.generalFilter);
  }

  onRouteDisplayTypeChange(): void {
    this.dataStoreService.setGeneralFilter(this.generalFilter);
  }

  onRouteFilterTypeChange(): void {
    this.dataStoreService.setGeneralFilter(this.generalFilter);
  }

  ngOnDestroy(): void {
    this.generalFilterSubscription.unsubscribe();
  }
}
