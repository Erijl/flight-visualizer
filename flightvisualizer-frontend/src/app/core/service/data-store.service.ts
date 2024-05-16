import {Injectable} from '@angular/core';
import {
  Airport,
  DefaultGeneralFilter, DefaultRouteFilter, DefaultTimeFilter,
  FlightDateFrequencyDto,
  FlightSchedule,
  FlightScheduleRouteDto
} from "../dto/airport";
import {DataService} from "./data.service";
import {BehaviorSubject} from "rxjs";
import {FilterService} from "./filter.service";
import {DetailSelectionType} from "../enum";
import {RouteFilter} from "../../protos/filters";
import {GeneralFilter, LegRender, TimeFilter} from "../../protos/objects";
import {AirportDisplayType, RouteDisplayType} from "../../protos/enums";

@Injectable({
  providedIn: 'root'
})
export class DataStoreService {

  // 'raw' data
  allFlightScheduleRouteDtos: FlightScheduleRouteDto[] = [];
  allAirports: Airport[] = [];
  fetchedFlightSchedule: FlightSchedule = new FlightSchedule();

  legRenders: LegRender[] = [];

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
  private _timeFilter: BehaviorSubject<TimeFilter> = new BehaviorSubject<TimeFilter>(DefaultTimeFilter);
  timeFilter = this._timeFilter.asObservable();

  private _generalFilter: BehaviorSubject<GeneralFilter> = new BehaviorSubject<GeneralFilter>(DefaultGeneralFilter);
  generalFilter = this._generalFilter.asObservable();

  private _routeFilter: BehaviorSubject<RouteFilter> = new BehaviorSubject<RouteFilter>(DefaultRouteFilter);
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
    this.getFlightDateFrequencies();
    this.getFlightScheduleLegRoutes();
    this.getDistinctFlightScheduleLegsForRendering();
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
    if(this.allFlightScheduleRouteDtos.length == 0) return new FlightScheduleRouteDto();
    return this.allFlightScheduleRouteDtos.reduce((a, b) => a.kilometerDistance > b.kilometerDistance ? a : b);
  }

  getLongestFlightRoute(): FlightScheduleRouteDto {
    if(this.allFlightScheduleRouteDtos.length == 0) return new FlightScheduleRouteDto();
    return this.allFlightScheduleRouteDtos.reduce((a, b) => this.filterService.calculateFlightDurationInMinutes(a) > this.filterService.calculateFlightDurationInMinutes(b) ? a : b);
  }

  getCurrentlyDisplayedRoutesForSelectedAirport(): FlightScheduleRouteDto[] {
    const selectedAirportIataCode = this.getSelectedAirport().iataAirportCode;
    return this.getCurrentlyDisplayedRoutes()
      .filter(route =>
        (route.originAirport.iataAirportCode == selectedAirportIataCode || route.destinationAirport.iataAirportCode == selectedAirportIataCode)
        && ((route.originAirport.iataAirportCode == selectedAirportIataCode && this.getSelectedAirportRoutesOutgoing()) || (route.destinationAirport.iataAirportCode == selectedAirportIataCode && this.getSelectedAirportRoutesIncoming()))
      );
  }

  getLegRenders(): LegRender[] {
    return this.legRenders;
  }

  // SETTERS

  setSelectedAirport(airport: Airport): void {
    this._selectedAirport.next(airport);

    if (this.getGeneralFilter().routeDisplayType == RouteDisplayType.ROUTEDISPLAYTYPE_SPECIFICAIRPORT) {
      this.updateRenderedRoutes();
    }
  }

  setSelectedRoute(route: FlightScheduleRouteDto): void {
    this._selectedRoute.next(route);
  }

  private setCurrentlyDisplayedRoutes(routes: FlightScheduleRouteDto[]): void {
    this._currentlyDisplayedRoutes.next(routes);
    this._renderedRoutes.next(this.filterService.getCleanedFlightScheduleRouteDtos(routes));
  }

  private setCurrentlyDisplayedAirports(airports: Airport[]): void {
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

    this.getDistinctFlightScheduleLegsForRendering();
  }

  setGeneralFilter(generalFilter: GeneralFilter): void {
    this._generalFilter.next(generalFilter);

    this.updateRenderedAirports();
    this.updateRenderedRoutes();
  }

  setDetailSelectionType(detailSelectionType: DetailSelectionType): void {
    this._detailSelectionType.next(detailSelectionType);
  }

  setRouteFilter(routeFilter: RouteFilter): void {
    this._routeFilter.next(routeFilter);
    this.reRenderRoutes();
  }

  // FETCHING DATA
  private getAirports(): void {
    this.dataService.getAirports().subscribe(airports => {
      this.allAirports = airports.filter(airport => airport.locationType === "Airport");
      this.reRenderAirports();
    });
  }

  private getFlightScheduleLegRoutes(): void {
    // @ts-ignore
    this.dataService.getFlightScheduleLegRoutes(this.getTimeFilter().dateRange).subscribe(flightScheduleLegs => {
      this.allFlightScheduleRouteDtos = flightScheduleLegs;
      this.reRenderRoutes();
    });
  }

  private getDistinctFlightScheduleLegsForRendering(): void {
    // @ts-ignore
    this.dataService.getDistinctFlightScheduleLegsForRendering(this.getTimeFilter().dateRange).subscribe(legRenders => {
      this.legRenders = legRenders;
    });
  }

  private getFlightDateFrequencies(): void {
    this.dataService.getFlightDateFrequencies().subscribe(flightDateFrequencies => {
      this._allFlightDateFrequencies.next(flightDateFrequencies);
      this.initTimeFilter();
    });
  }

  private getFlightScheduleById(id: number): void {
    this.dataService.getFlightScheduleById(id).subscribe(flightSchedule => {
      this.fetchedFlightSchedule = flightSchedule;
    });
  }

  // RENDERING

  reRenderAirports() {
    this.updateRenderedAirports();
  }

  reRenderRoutes() {
    this.updateRenderedRoutes();
  }

  private updateRenderedAirports() {
    switch (this.getGeneralFilter().airportDisplayType) {
      default:
      case AirportDisplayType.AIRPORTDISPLAYTYPE_ALL:
        this.setCurrentlyDisplayedAirports(this.getAllAirports());
        break;
      case AirportDisplayType.AIRPORTDISPLAYTYPE_WITHROUTES:
        this.setCurrentlyDisplayedAirports(this.filterService.getAllAirportsPresentInFlightScheduleRouteDtos(this.getRenderedRoutes()));
        break;
      case AirportDisplayType.AIRPORTDISPLAYTYPE_NONE:
        this.setCurrentlyDisplayedAirports([]);
        break;
    }
  }

  private updateRenderedRoutes() {
    let routesToBeDisplayed: FlightScheduleRouteDto[] = this.getAllFlightScheduleRouteDtos();
    switch (this.getGeneralFilter().routeDisplayType) {
      default:
      case RouteDisplayType.ROUTEDISPLAYTYPE_ALL:
        routesToBeDisplayed = this.getAllFlightScheduleRouteDtos();
        break;
      case RouteDisplayType.ROUTEDISPLAYTYPE_SPECIFICAIRPORT:
        routesToBeDisplayed = this.getFlightScheduleRoutesForSelectedAirport();
        break;
      case RouteDisplayType.ROUTEDISPLAYTYPE_ONLYWITHINSAMECOUNTRY:
        routesToBeDisplayed = this.filterService.getFLightScheduleRouteDtosWithinSameCountry(this.getAllFlightScheduleRouteDtos());
        break;
      case RouteDisplayType.ROUTEDISPLAYTYPE_WITHINSAMEREGION:
        routesToBeDisplayed = this.filterService.getFLightScheduleRouteDtosWithinSameRegion((this.getAllFlightScheduleRouteDtos()));
        break;
      case RouteDisplayType.ROUTEDISPLAYTYPE_WITHINSAMETIMEZONE:
        routesToBeDisplayed = this.filterService.getFLightScheduleRouteDtosWithinSameTimezone(this.getAllFlightScheduleRouteDtos());
        break;
    }

    routesToBeDisplayed = this.filterService.getFlightRoutesInTimeFrame(routesToBeDisplayed, this.getTimeFilter());

    routesToBeDisplayed = this.filterService.getFlightScheduleRouteDtosByRouteFilter(routesToBeDisplayed, this.getRouteFilter());

    this.setCurrentlyDisplayedRoutes(routesToBeDisplayed);

    if (this.getDetailSelectionType() == DetailSelectionType.ROUTE && !routesToBeDisplayed.includes(this.getSelectedRoute())) {
      this.setSelectedRoute(new FlightScheduleRouteDto());
    }

    if (this.getGeneralFilter().airportDisplayType == AirportDisplayType.AIRPORTDISPLAYTYPE_WITHROUTES) {
      this.reRenderAirports();
    }
  }

  initTimeFilter() {
    const flightDateFrequencies = this._allFlightDateFrequencies.getValue();
    let timeFilter = DefaultTimeFilter;

    if(!timeFilter || !timeFilter.dateRange) return;

    if (!flightDateFrequencies || flightDateFrequencies.length == 0) {
      //TODO oh boy...
    }

    timeFilter.dateRange.start = flightDateFrequencies.reverse().find(flightDateFrequency => flightDateFrequency.count > 0 && flightDateFrequency.startDateUtc)?.startDateUtc ?? undefined;
    if(timeFilter.dateRange.start) timeFilter.dateRange.start = new Date(timeFilter.dateRange.start);

    this.setTimeFilter(timeFilter);
  }
}
