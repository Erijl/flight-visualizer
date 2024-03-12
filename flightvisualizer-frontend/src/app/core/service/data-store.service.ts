import {Injectable} from '@angular/core';
import {
  Airport,
  FlightDateFrequencyDto,
  FlightScheduleRouteDto,
  SelectedDateRange,
  SelectedTimeRange
} from "../dto/airport";
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

  private _allFlightDateFrequencies: BehaviorSubject<FlightDateFrequencyDto[]> = new BehaviorSubject<FlightDateFrequencyDto[]>([]);
  allFlightDateFrequencies = this._allFlightDateFrequencies.asObservable();

  // displayed data
  private _currentlyDisplayedRoutes: BehaviorSubject<FlightScheduleRouteDto[]> = new BehaviorSubject<FlightScheduleRouteDto[]>([]);
  currentlyDisplayedRoutes: FlightScheduleRouteDto[] = [];

  private _currentlyDisplayedAirports: BehaviorSubject<Airport[]> = new BehaviorSubject<Airport[]>([]);
  currentlyDisplayedAirports = this._currentlyDisplayedAirports.asObservable();

  private _renderedRoutes: BehaviorSubject<FlightScheduleRouteDto[]> = new BehaviorSubject<FlightScheduleRouteDto[]>([]);
  renderedRoutes = this._renderedRoutes.asObservable();

  // selected data
  private _selectedAirport: BehaviorSubject<Airport> = new BehaviorSubject<Airport>(new Airport());
  selectedAirport = this._selectedAirport.asObservable();

  private _selectedRoute: BehaviorSubject<FlightScheduleRouteDto> = new BehaviorSubject<FlightScheduleRouteDto>(new FlightScheduleRouteDto());
  selectedRoute = this._selectedRoute.asObservable();

  private _selectedDateRange: BehaviorSubject<SelectedDateRange> = new BehaviorSubject<SelectedDateRange>(new SelectedDateRange(new Date(), null));
  selectedDateRange = this._selectedDateRange.asObservable();

  private _selectedTimeRange: BehaviorSubject<SelectedTimeRange> = new BehaviorSubject<SelectedTimeRange>(new SelectedTimeRange(0, 1439));
  selectedTimeRange = this._selectedTimeRange.asObservable();

  // state
  private _selectedAirportRoutesOutgoing: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  selectedAirportRoutesOutgoing = this._selectedAirportRoutesOutgoing.asObservable();

  private _selectedAirportRoutesIncoming: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  selectedAirportRoutesIncoming = this._selectedAirportRoutesIncoming.asObservable();


  constructor(private dataService: DataService, private filterService: FilterService) {
    this.getAirports();
    this.getFlightScheduleLegRoutes();
    this.getFlightDateFrequencies();
  }

  // GETTERS
  getAllAirports(): Airport[] {
    return this.allAirports;
  }

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

  getRenderedRoutes(): FlightScheduleRouteDto[] {
    return this._renderedRoutes.getValue();
  }

  getFlightScheduleRoutesForSelectedAirport(): FlightScheduleRouteDto[] {
    return this.filterService.getFlightScheduleRouteDtosByAirport(this.allFlightScheduleRouteDtos, this.getSelectedAirport(), this.getSelectedAirportRoutesIncoming(), this.getSelectedAirportRoutesOutgoing());
  }

  getFlightScheduleRoutesForSelectedAirportWithTimeFilter(): FlightScheduleRouteDto[] {
    return this.filterService.getFlightRoutesInTimeFrame(this.filterService.getFlightScheduleRouteDtosByAirport(this.allFlightScheduleRouteDtos, this.getSelectedAirport(), this.getSelectedAirportRoutesIncoming(), this.getSelectedAirportRoutesOutgoing()), this.getSelectedTimeRange());
  }

  getSelectedAirportRoutesOutgoing(): boolean {
    return this._selectedAirportRoutesOutgoing.getValue();
  }

  getSelectedAirportRoutesIncoming(): boolean {
    return this._selectedAirportRoutesIncoming.getValue();
  }

  getAllFlightScheduleRouteDtos() {
    return this.allFlightScheduleRouteDtos;
  }

  getSelectedDateRange(): SelectedDateRange {
    return this._selectedDateRange.getValue();
  }

  getSelectedTimeRange(): SelectedTimeRange {
    return this._selectedTimeRange.getValue();
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
    this._renderedRoutes.next(this.filterService.getCleanedFlightScheduleRouteDtos(routes));
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

  setSelectedDateRange(selectedDateRange: SelectedDateRange): void {
    this._selectedDateRange.next(selectedDateRange);

    this.getFlightScheduleLegRoutes();
  }

  setSelectedTimeRange(selectedTimeRange: SelectedTimeRange): void {
    this._selectedTimeRange.next(selectedTimeRange);

    this.setCurrentlyDisplayedRoutes(this.filterService.getFlightRoutesInTimeFrame(this.allFlightScheduleRouteDtos, this.getSelectedTimeRange()));
  }

  // FETCHING DATA
  private getAirports(): void {
    this.dataService.getAirports().subscribe(airports => {
      this.allAirports = airports.filter(airport => airport.locationType === "Airport");
      this.setCurrentlyDisplayedAirports(this.allAirports);
    });
  }

  private getFlightScheduleLegRoutes(): void {
    this.dataService.getFlightScheduleLegRoutes(this.getSelectedDateRange()).subscribe(flightScheduleLegs => {
      this.allFlightScheduleRouteDtos = flightScheduleLegs;
      this.setCurrentlyDisplayedRoutes(this.allFlightScheduleRouteDtos);
    });
  }

  private getFlightDateFrequencies(): void {
    this.dataService.getFlightDateFrequencies().subscribe(flightDateFrequencies => {
      this._allFlightDateFrequencies.next(flightDateFrequencies);
    });
  }
}
