import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataStoreService} from "../../../core/service/data-store.service";
import {Subscription} from "rxjs";
import {LegRender} from "../../../protos/objects";

@Component({
  selector: 'app-airplane-info',
  templateUrl: './airplane-info.component.html',
  styleUrl: './airplane-info.component.css'
})
export class AirplaneInfoComponent implements OnInit, OnDestroy {

  //Subscriptions
  selectedAirplaneSubscription!: Subscription;

  // UI Data
  selectedAirplane: LegRender = LegRender.create();

  constructor(private dataStoreService: DataStoreService) { }

  ngOnInit(): void {
    this.selectedAirplaneSubscription = this.dataStoreService.selectedAirplane.subscribe(airplane => {
      this.selectedAirplane = airplane;
    });
  }

  ngOnDestroy(): void {
    this.selectedAirplaneSubscription.unsubscribe();
  }
}
