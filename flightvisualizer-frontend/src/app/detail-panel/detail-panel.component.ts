import {Component, OnDestroy, OnInit} from '@angular/core';
import {DetailSelectionType} from "../core/enum";
import {Subscription} from "rxjs";
import {DataStoreService} from "../core/service/data-store.service";
import {Airport, FlightScheduleRouteDto} from "../core/dto/airport";

@Component({
  selector: 'app-detail-panel',
  templateUrl: './detail-panel.component.html',
  styleUrl: './detail-panel.component.css'
})
export class DetailPanelComponent implements OnInit, OnDestroy{

  detailSelectionTypeSubscription!: Subscription;
  selectedAirpotSubscription!: Subscription;
  selectedRouteSubscription!: Subscription;

  selectionType: DetailSelectionType = DetailSelectionType.AIRPORT;

  expanded: boolean = false;

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.detailSelectionTypeSubscription = this.dataStoreService.detailSelectionType.subscribe((type: DetailSelectionType) => {
      this.selectionType = type;
    });

    this.selectedAirpotSubscription = this.dataStoreService.selectedAirport.subscribe((airport: Airport) => {
      if (airport.iataAirportCode != '') {
        this.expanded = true;
      }
    });

    this.selectedRouteSubscription =  this.dataStoreService.selectedRoute.subscribe(route => {
      if (route.legId != -1) {
        this.expanded = true;
      }
    });
  }

  onSelectionTypeChange(): void {
    this.dataStoreService.setDetailSelectionType(this.selectionType);
  }

  ngOnDestroy(): void {
    this.detailSelectionTypeSubscription.unsubscribe();
  }
}
