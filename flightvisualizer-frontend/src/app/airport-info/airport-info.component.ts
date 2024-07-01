import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {DataStoreService} from "../core/service/data-store.service";
import {Subscription} from "rxjs";
import {AirportDetails, AirportRender, LegRender} from "../protos/objects";
import {SelectedAirportFilter} from "../protos/filters";

@Component({
  selector: 'app-airport-info',
  templateUrl: './airport-info.component.html',
  styleUrl: './airport-info.component.css'
})
export class AirportInfoComponent implements AfterViewInit, OnInit, OnDestroy {
  // Subscriptions
  selectedAirportSubscription!: Subscription;
  currentlyRenderedRoutesSubscription!: Subscription;
  airportDetailsSubscription!: Subscription;
  currentlyDisplayedAirportsSubscription!: Subscription;

  // UI data
  displayedColumns: string[] = ['originAirportIataCode', 'destinationAirportIataCode', 'distanceKilometers', 'durationMinutes'];
  dataSource = new MatTableDataSource<LegRender>([]);
  selectedAirportFilter: SelectedAirportFilter = SelectedAirportFilter.create();
  airportDetails: AirportDetails = AirportDetails.create();
  currentlyDisplayedAirports: AirportRender[] = [];

  // UI state
  specificAirportRoutesOutgoing: boolean = true;
  specificAirportRoutesIncoming: boolean = true;

  // ViewChild's
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.selectedAirportSubscription = this.dataStoreService.selectedAirportFilter.subscribe(selectedAirportFilter => {
      this.selectedAirportFilter = selectedAirportFilter;

      this.updateTable();
    });

    this.airportDetailsSubscription = this.dataStoreService.airportDetails.subscribe(airportDetails => {
      this.airportDetails = airportDetails;
    });

    this.currentlyRenderedRoutesSubscription = this.dataStoreService.renderedRoutes.subscribe(routes => {
      this.updateTable();
    });

    this.currentlyDisplayedAirportsSubscription = this.dataStoreService.currentlyDisplayedAirports.subscribe(airports => {
      this.currentlyDisplayedAirports = airports;
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  onOutgoingChange(event: any): void {
    this.selectedAirportFilter.includingDepartures = event.checked;
    this.dataStoreService.setSelectedAirportFilter(this.selectedAirportFilter);
    this.updateTable();
  }

  onIncomingChange(event: any): void {
    this.selectedAirportFilter.includingArrivals = event.checked;
    this.dataStoreService.setSelectedAirportFilter(this.selectedAirportFilter);
    this.updateTable();
  }

  updateTable(): void {
    this.dataSource = new MatTableDataSource<LegRender>(this.dataStoreService.getLegRendersForSelectedAirport());
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.selectedAirportSubscription.unsubscribe();
    this.currentlyRenderedRoutesSubscription.unsubscribe();
    this.airportDetailsSubscription.unsubscribe();
    this.currentlyDisplayedAirportsSubscription.unsubscribe();
  }
}
