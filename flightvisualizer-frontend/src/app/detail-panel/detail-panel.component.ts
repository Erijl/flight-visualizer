import {Component, OnDestroy, OnInit} from '@angular/core';
import {DetailSelectionType} from "../core/enum";
import {Subscription} from "rxjs";
import {DataStoreService} from "../core/service/data-store.service";

@Component({
  selector: 'app-detail-panel',
  templateUrl: './detail-panel.component.html',
  styleUrl: './detail-panel.component.css'
})
export class DetailPanelComponent implements OnInit, OnDestroy{

  detailSelectionTypeSubscription!: Subscription;

  selectionType: DetailSelectionType = DetailSelectionType.AIRPORT;

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.detailSelectionTypeSubscription = this.dataStoreService.detailSelectionType.subscribe((type: DetailSelectionType) => {
      this.selectionType = type;
    });
  }

  onSelectionTypeChange(): void {
    this.dataStoreService.setDetailSelectionType(this.selectionType);
  }

  ngOnDestroy(): void {
    this.detailSelectionTypeSubscription.unsubscribe();
  }
}
