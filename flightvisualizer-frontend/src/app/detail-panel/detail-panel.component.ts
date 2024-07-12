import {Component, OnDestroy, OnInit} from '@angular/core';
import {DetailSelectionType, ModeSelection} from "../core/enum";
import {Subscription} from "rxjs";
import {DataStoreService} from "../core/service/data-store.service";

@Component({
  selector: 'app-detail-panel',
  templateUrl: './detail-panel.component.html',
  styleUrl: './detail-panel.component.css'
})
export class DetailPanelComponent implements OnInit, OnDestroy{

  detailSelectionTypeSubscription!: Subscription;
  selectedAirportSubscription!: Subscription;
  selectedRouteSubscription!: Subscription;
  modeSelectionSubscription!: Subscription;

  selectionType: DetailSelectionType = DetailSelectionType.AIRPORT;

  expanded: boolean = false;
  modeSelection: ModeSelection = ModeSelection.NONE;

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.detailSelectionTypeSubscription = this.dataStoreService.detailSelectionType.subscribe((type: DetailSelectionType) => {
      this.selectionType = type;
    });

    this.selectedAirportSubscription = this.dataStoreService.selectedAirportFilter.subscribe((selectedAirportFilter) => {
      if (selectedAirportFilter.iataCode != '') {
        this.expanded = true;
      }
    });

    this.selectedRouteSubscription =  this.dataStoreService.selectedRoute.subscribe(route => {
      if (route.originAirportIataCode != '') {
        this.expanded = true;
      }
    });

    this.modeSelectionSubscription = this.dataStoreService.modeSelection.subscribe(modeSelection => {
      this.modeSelection = modeSelection;

      if(this.modeSelection == ModeSelection.LIVE_FEED) {
        this.selectionType = DetailSelectionType.AIRPLANE;
      }
    });
  }

  onSelectionTypeChange(): void {
    this.dataStoreService.setDetailSelectionType(this.selectionType);
  }

  ngOnDestroy(): void {
    this.detailSelectionTypeSubscription.unsubscribe();
    this.selectedAirportSubscription.unsubscribe();
    this.selectedRouteSubscription.unsubscribe();
    this.modeSelectionSubscription.unsubscribe();
  }

  protected readonly ModeSelection = ModeSelection;
  protected readonly DetailSelectionType = DetailSelectionType;
}
