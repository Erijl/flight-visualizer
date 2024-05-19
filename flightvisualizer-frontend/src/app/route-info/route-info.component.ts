import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {DataStoreService} from "../core/service/data-store.service";
import {LegRender} from "../protos/objects";

@Component({
  selector: 'app-route-info',
  templateUrl: './route-info.component.html',
  styleUrl: './route-info.component.css'
})
export class RouteInfoComponent implements OnInit, OnDestroy{
  selectedRouteSubscription!: Subscription;

  selectedRoute: LegRender = LegRender.create();

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.selectedRouteSubscription = this.dataStoreService.selectedRoute.subscribe(route => {
      this.selectedRoute = route;
    });
  }

  ngOnDestroy(): void {
    this.selectedRouteSubscription.unsubscribe();
  }
}
