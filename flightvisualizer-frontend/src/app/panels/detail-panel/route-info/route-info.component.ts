import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Subscription} from "rxjs";
import {DataStoreService} from "../../../core/service/data-store.service";
import {DetailedLegInformation, LegRender} from "../../../protos/objects";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";

@Component({
  selector: 'app-route-info',
  templateUrl: './route-info.component.html',
  styleUrl: './route-info.component.css'
})
export class RouteInfoComponent implements OnInit, OnDestroy, AfterViewInit{
  selectedRouteSubscription!: Subscription;
  currentlyRenderedRoutesSubscription!: Subscription;
  routeDetailSubscription!: Subscription;

  // UI data
  displayedColumns: string[] = ['operationDate', 'departureTimeUtc', 'arrivalTimeUtc', 'aircraftArrivalTimeDateDiffUtc'];
  dataSource = new MatTableDataSource<DetailedLegInformation>([]);

  selectedRoute: LegRender = LegRender.create();
  routeDetails: DetailedLegInformation[] = [];

  // ViewChild's
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.selectedRouteSubscription = this.dataStoreService.selectedRoute.subscribe(route => {
      this.selectedRoute = route;

      this.updateTable();
    });

    this.currentlyRenderedRoutesSubscription = this.dataStoreService.renderedRoutes.subscribe(routes => {
      this.updateTable();
    });

    this.routeDetailSubscription = this.dataStoreService.routeDetails.subscribe(routeDetails => {
      this.routeDetails = routeDetails;
      this.updateTable();
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  updateTable(): void {
    this.dataSource = new MatTableDataSource<DetailedLegInformation>(this.routeDetails);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.selectedRouteSubscription.unsubscribe();
    this.currentlyRenderedRoutesSubscription.unsubscribe();
    this.routeDetailSubscription.unsubscribe();
  }
}
