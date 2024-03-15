import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {DataStoreService} from "../core/service/data-store.service";
import {FlightSchedule, FlightScheduleRouteDto} from "../core/dto/airport";

@Component({
  selector: 'app-route-info',
  templateUrl: './route-info.component.html',
  styleUrl: './route-info.component.css'
})
export class RouteInfoComponent implements OnInit, OnDestroy{
  selectedRouteSubscription!: Subscription;

  selectedRoute: FlightScheduleRouteDto = new FlightScheduleRouteDto();

  flightSchedule: FlightSchedule = new FlightSchedule();
  allRoutesInFLightSchedule: FlightScheduleRouteDto[] = [];

  //TODO show the route its part of
  //TODO replace convertIntToTimeOfDay with a pipe
  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.selectedRouteSubscription = this.dataStoreService.selectedRoute.subscribe(route => {
      this.selectedRoute = route;
      this.flightSchedule = this.dataStoreService.getSpecificFlightSchedule(route.flightScheduleId);
      this.allRoutesInFLightSchedule = this.dataStoreService.getAllRoutesForFlightSchedule(route.flightScheduleId);
    });
  }

  protected convertIntToTimeOfDay(value: number | null): string {
    if(value == null) return '';
    let hours = Math.floor((value)/60);
    let minutes = Math.floor((value)%60);

    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
  }

  ngOnDestroy(): void {
    this.selectedRouteSubscription.unsubscribe();
  }
}
