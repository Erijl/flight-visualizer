import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {DataStoreService} from "../core/service/data-store.service";
import {FlightSchedule, FlightScheduleRouteDto} from "../core/dto/airport";
import {LegRender} from "../protos/objects";

@Component({
  selector: 'app-route-info',
  templateUrl: './route-info.component.html',
  styleUrl: './route-info.component.css'
})
export class RouteInfoComponent implements OnInit, OnDestroy{
  selectedRouteSubscription!: Subscription;

  selectedRoute: FlightScheduleRouteDto = new FlightScheduleRouteDto();

  flightSchedule: FlightSchedule = new FlightSchedule();
  allLegRenders: LegRender[] = [];

  //(TODO) (show the route its part of)
  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.selectedRouteSubscription = this.dataStoreService.selectedRoute.subscribe(route => {
      this.selectedRoute = route;
      this.flightSchedule = this.dataStoreService.getSpecificFlightSchedule(route.flightScheduleId);
      this.allLegRenders = this.dataStoreService.getAllLegsForSpecificRoute(route.flightScheduleId);
    });
  }

  ngOnDestroy(): void {
    this.selectedRouteSubscription.unsubscribe();
  }
}
