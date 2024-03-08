import { Injectable } from '@angular/core';
import {Airport, FlightScheduleRouteDto} from "../dto/airport";
import {DataService} from "./data.service";
import {BehaviorSubject} from "rxjs";
import {FilterService} from "./filter.service";

@Injectable({
  providedIn: 'root'
})
export class DataStoreService {

  // 'raw' data
  allFlightScheduleRouteDtos: FlightScheduleRouteDto[] = [];
  allAirports: Airport[] = [];

  // displayed data
  private _currentlyDisplayedRoutes: BehaviorSubject<FlightScheduleRouteDto[]> = new BehaviorSubject<FlightScheduleRouteDto[]>([]);
  currentlyDisplayedRoutes: FlightScheduleRouteDto[] = [];

  private _currentlyDisplayedAirports: BehaviorSubject<Airport[]> = new BehaviorSubject<Airport[]>([]);
  currentlyDisplayedAirports: Airport[] = [];

  // selected data
  private _selectedAirport: BehaviorSubject<Airport> = new BehaviorSubject<Airport>(new Airport());
  selectedAirport = this._selectedAirport.asObservable();

  private _selectedRoute: BehaviorSubject<FlightScheduleRouteDto> = new BehaviorSubject<FlightScheduleRouteDto>(new FlightScheduleRouteDto());
  selectedRoute = this._selectedRoute.asObservable();

  // state
  private _selectedAirportRoutesOutgoing: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  selectedAirportRoutesOutgoing = this._selectedAirportRoutesOutgoing.asObservable();

  private _selectedAirportRoutesIncoming: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  selectedAirportRoutesIncoming = this._selectedAirportRoutesIncoming.asObservable();


  constructor(private dataService: DataService, private filterService: FilterService) {
    this.getAirports();
    this.getFlightScheduleLegRoutes();
  }

  // GETTERS
  getSelectedAirport(): Airport {
    return this._selectedAirport.getValue();
  }

  getSelectedRoute(): FlightScheduleRouteDto {
    return this._selectedRoute.getValue();
  }

  getCurrentlyDisplayedRoutes(): FlightScheduleRouteDto[] {
    return this._currentlyDisplayedRoutes.getValue();
  }

  getCurrentlyDisplayedAirports(): Airport[] {
    return this._currentlyDisplayedAirports.getValue();
  }

  getFlightScheduleRoutesForSelectedAirport(): FlightScheduleRouteDto[] {
    return this.filterService.getFlightScheduleRouteDtosByAirport(this.allFlightScheduleRouteDtos, this._selectedAirport.getValue(), this._selectedAirportRoutesIncoming.getValue(), this._selectedAirportRoutesOutgoing.getValue());
  }

  getSelectedAirportRoutesOutgoing(): boolean {
    return this._selectedAirportRoutesOutgoing.getValue();
  }

  getSelectedAirportRoutesIncoming(): boolean {
    return this._selectedAirportRoutesIncoming.getValue();
  }



  // SETTERS

  setSelectedAirport(airport: Airport): void {
    this._selectedAirport.next(airport);
  }

  setSelectedRoute(route: FlightScheduleRouteDto): void {
    this._selectedRoute.next(route);
  }

  setCurrentlyDisplayedRoutes(routes: FlightScheduleRouteDto[]): void {
    this._currentlyDisplayedRoutes.next(routes);
  }

  setCurrentlyDisplayedAirports(airports: Airport[]): void {
    this._currentlyDisplayedAirports.next(airports);
  }

  setSelectedAirportRoutesOutgoing(outgoing: boolean): void {
    this._selectedAirportRoutesOutgoing.next(outgoing);
  }

  setSelectedAirportRoutesIncoming(incoming: boolean): void {
    this._selectedAirportRoutesIncoming.next(incoming);
  }

  // FETCHING DATA
  getAirports(): void {
    this.dataService.getAirports().subscribe(airports => {
      this.allAirports = airports.filter(airport => airport.locationType === "Airport");
      this.setCurrentlyDisplayedAirports(this.allAirports);
    });
  }

  getFlightScheduleLegRoutes(): void {
    this.dataService.getFlightScheduleLegRoutes().subscribe(flightScheduleLegs => {
      this.allFlightScheduleRouteDtos = flightScheduleLegs;
      this.setCurrentlyDisplayedRoutes(this.allFlightScheduleRouteDtos);
    });
  }
}
