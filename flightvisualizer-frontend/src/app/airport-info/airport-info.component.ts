import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Airport, FlightScheduleRouteDto} from "../core/dto/airport";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {DataStoreService} from "../core/service/data-store.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-airport-info',
  templateUrl: './airport-info.component.html',
  styleUrl: './airport-info.component.css'
})
export class AirportInfoComponent implements AfterViewInit, OnInit, OnDestroy {
  // Subscriptions
  selectedAirportSubscription!: Subscription;

  // UI data
  displayedColumns: string[] = ['origin', 'destination', 'kilometerDistance'];
  dataSource = new MatTableDataSource<FlightScheduleRouteDto>([]);
  selectedAirport: Airport = new Airport();

  // UI state
  specificAirportRoutesOutgoing: boolean = true;
  specificAirportRoutesIncoming: boolean = true;
  compactFlightRouteInformationTable: boolean = true;

  // ViewChild's
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.selectedAirportSubscription = this.dataStoreService.selectedAirport.subscribe(airport => {
      this.selectedAirport = airport;
      this.updateTable();
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  onOutgoingChange(): void {
    this.dataStoreService.setSelectedAirportRoutesOutgoing(this.specificAirportRoutesOutgoing);
    this.updateTable();
  }

  onIncomingChange(): void {
    this.dataStoreService.setSelectedAirportRoutesIncoming(this.specificAirportRoutesIncoming);
    this.updateTable();
  }

  updateTable(): void {
    this.dataSource = new MatTableDataSource<FlightScheduleRouteDto>(this.dataStoreService.getFlightScheduleRoutesForSelectedAirport());
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.selectedAirportSubscription.unsubscribe();
  }
}
