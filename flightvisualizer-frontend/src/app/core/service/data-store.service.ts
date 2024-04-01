import {Injectable} from '@angular/core';
import {
  Airport,
  DateRange,
  FlightDateFrequencyDto,
  FlightSchedule,
  FlightScheduleRouteDto,
  GeneralFilter,
  RouteFilter,
  TimeFilter,
  TimeRange
} from "../dto/airport";
import {DataService} from "./data.service";
import {BehaviorSubject} from "rxjs";
import {FilterService} from "./filter.service";
import {AirportDisplayType, DetailSelectionType, RouteDisplayType, RouteFilterType} from "../enum";

@Injectable({
  providedIn: 'root'
})
export class DataStoreService {

  // 'raw' data
  allFlightScheduleRouteDtos: FlightScheduleRouteDto[] = [];
  allAirports: Airport[] = [];
  fetchedFlightSchedule: FlightSchedule = new FlightSchedule();

  private _allFlightDateFrequencies: BehaviorSubject<FlightDateFrequencyDto[]> = new BehaviorSubject<FlightDateFrequencyDto[]>([]);
  allFlightDateFrequencies = this._allFlightDateFrequencies.asObservable();


  // displayed data
  private _currentlyDisplayedRoutes: BehaviorSubject<FlightScheduleRouteDto[]> = new BehaviorSubject<FlightScheduleRouteDto[]>([]);
  currentlyDisplayedRoutes: FlightScheduleRouteDto[] = [];

  private _currentlyDisplayedAirports: BehaviorSubject<Airport[]> = new BehaviorSubject<Airport[]>([]);
  currentlyDisplayedAirports = this._currentlyDisplayedAirports.asObservable();

  private _renderedRoutes: BehaviorSubject<FlightScheduleRouteDto[]> = new BehaviorSubject<FlightScheduleRouteDto[]>([]);
  renderedRoutes = this._renderedRoutes.asObservable();


  // filter
  private _timeFilter: BehaviorSubject<TimeFilter> = new BehaviorSubject<TimeFilter>(new TimeFilter(new DateRange(new Date(), null), new TimeRange(0, 1439)));
  timeFilter = this._timeFilter.asObservable();

  private _generalFilter: BehaviorSubject<GeneralFilter> = new BehaviorSubject<GeneralFilter>(new GeneralFilter(AirportDisplayType.ALL, RouteDisplayType.ALL));
  generalFilter = this._generalFilter.asObservable();

  private _routeFilter: BehaviorSubject<RouteFilter> = new BehaviorSubject<RouteFilter>(new RouteFilter(RouteFilterType.DISTANCE, 0, 1000));
  routeFilter = this._routeFilter.asObservable();

  private _detailSelectionType: BehaviorSubject<DetailSelectionType> = new BehaviorSubject<DetailSelectionType>(DetailSelectionType.AIRPORT);
  detailSelectionType = this._detailSelectionType.asObservable();


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
    return this.filterService.getFlightRoutesInTimeFrame(
      this.filterService.getFlightScheduleRouteDtosByAirport(
        this.allFlightScheduleRouteDtos,
        this.getSelectedAirport(),
        this.getSelectedAirportRoutesIncoming(),
        this.getSelectedAirportRoutesOutgoing()
      ),
      this.getTimeFilter()
    );
  }

  getFlightScheduleRoutesWithinSameCountryWithTimeFilter(): FlightScheduleRouteDto[] {
    return this.filterService.getFlightRoutesInTimeFrame(
      this.filterService.getFLightScheduleRouteDtosWithinSameCountry(
        this.allFlightScheduleRouteDtos
      ),
      this.getTimeFilter()
    );
  }

  getFlightScheduleRoutesWithinSameRegionWithTimeFilter(): FlightScheduleRouteDto[] {
    return this.filterService.getFlightRoutesInTimeFrame(
      this.filterService.getFLightScheduleRouteDtosWithinSameRegion(
        this.allFlightScheduleRouteDtos
      ),
      this.getTimeFilter()
    );
  }

  getFlightScheduleRoutesWithinSameTimezoneWithTimeFilter(): FlightScheduleRouteDto[] {
    return this.filterService.getFlightRoutesInTimeFrame(
      this.filterService.getFLightScheduleRouteDtosWithinSameTimezone(
        this.allFlightScheduleRouteDtos
      ),
      this.getTimeFilter()
    );
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

  getAllFlightScheduleRouteDtosWithTimeFilter() {
    return this.filterService.getFlightRoutesInTimeFrame(this.allFlightScheduleRouteDtos, this.getTimeFilter());
  }

  getSpecificFlightSchedule(id: number): FlightSchedule {
    if (this.fetchedFlightSchedule?.flightScheduleId == id) return this.fetchedFlightSchedule;
    this.getFlightScheduleById(id);
    return this.fetchedFlightSchedule;
  }

  getAllRoutesForFlightSchedule(id: number): FlightScheduleRouteDto[] {
    return this.allFlightScheduleRouteDtos.filter(route => route.flightScheduleId == id);
  }

  getTimeFilter(): TimeFilter {
    return this._timeFilter.getValue();
  }

  getGeneralFilter(): GeneralFilter {
    return this._generalFilter.getValue();
  }

  getRouteFilter(): RouteFilter {
    return this._routeFilter.getValue();
  }

  getDetailSelectionType(): DetailSelectionType {
    return this._detailSelectionType.getValue();
  }

  getFurthestFlightRoute(): FlightScheduleRouteDto {
    return this.allFlightScheduleRouteDtos.reduce((a, b) => a.kilometerDistance > b.kilometerDistance ? a : b);
  }

  getFlightScheduleRouteDtosWithRouteFilter(): FlightScheduleRouteDto[] {
    return this.filterService.getFlightScheduleRouteDtosByRouteFilter(this.allFlightScheduleRouteDtos, this.getRouteFilter());
  }

  getLongestFlightRoute(): FlightScheduleRouteDto {
    return this.allFlightScheduleRouteDtos.reduce((a, b) => this.filterService.calculateFlightDurationInMinutes(a) > this.filterService.calculateFlightDurationInMinutes(b) ? a : b);
  }


  // SETTERS

  setSelectedAirport(airport: Airport): void {
    this._selectedAirport.next(airport);
  }

  setSelectedRoute(route: FlightScheduleRouteDto): void {
    this._selectedRoute.next(route);
  }

  setCurrentlyDisplayedRoutes(routes: FlightScheduleRouteDto[]): void {
    console.log("Set AllFlighroutes: " + routes.length)
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

  setTimeFilter(timeFilter: TimeFilter): void {
    this._timeFilter.next(timeFilter);

    this.getFlightScheduleLegRoutes();
  }

  setGeneralFilter(generalFilter: GeneralFilter): void {
    this._generalFilter.next(generalFilter);
  }

  setDetailSelectionType(detailSelectionType: DetailSelectionType): void {
    this._detailSelectionType.next(detailSelectionType);
  }

  setRouteFilter(routeFilter: RouteFilter): void {
    this._routeFilter.next(routeFilter);
    this.setCurrentlyDisplayedRoutes(this.getFlightScheduleRouteDtosWithRouteFilter());
  }

  // FETCHING DATA
  private getAirports(): void {
    this.dataService.getAirports().subscribe(airports => {
      this.allAirports = airports.filter(airport => airport.locationType === "Airport");
      this.setCurrentlyDisplayedAirports(this.allAirports);
    });
  }

  private getFlightScheduleLegRoutes(): void {
    this.dataService.getFlightScheduleLegRoutes(this.getTimeFilter().dateRange).subscribe(flightScheduleLegs => {
      console.log('Fetched new Routes: ' + flightScheduleLegs.length)
      this.allFlightScheduleRouteDtos = flightScheduleLegs;
      this.setCurrentlyDisplayedRoutes(this.allFlightScheduleRouteDtos);
    });
  }

  private getFlightDateFrequencies(): void {
    this.dataService.getFlightDateFrequencies().subscribe(flightDateFrequencies => {
      this._allFlightDateFrequencies.next(flightDateFrequencies);
    });
  }

  private getFlightScheduleById(id: number): void {
    this.dataService.getFlightScheduleById(id).subscribe(flightSchedule => {
      this.fetchedFlightSchedule = flightSchedule;
    });
  }
}
