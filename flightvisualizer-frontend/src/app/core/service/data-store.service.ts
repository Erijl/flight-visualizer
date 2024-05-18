import {Injectable} from '@angular/core';
import {
  DefaultGeneralFilter, DefaultRouteFilter, DefaultSelectedAirportFilter, DefaultTimeFilter,
  FlightDateFrequencyDto,
  FlightSchedule,
  FlightScheduleRouteDto
} from "../dto/airport";
import {DataService} from "./data.service";
import {BehaviorSubject} from "rxjs";
import {FilterService} from "./filter.service";
import {DetailSelectionType} from "../enum";
import {GeneralFilter, RouteFilter, SelectedAirportFilter, TimeFilter} from "../../protos/filters";
import {AirportRender, LegRender} from "../../protos/objects";
import {AirportDisplayType, RouteDisplayType} from "../../protos/enums";

@Injectable({
  providedIn: 'root'
})
export class DataStoreService {

  // 'raw' data
  allLegRenders: LegRender[] = [];
  allAirports: AirportRender[] = [];
  fetchedFlightSchedule: FlightSchedule = new FlightSchedule();

  private _allFlightDateFrequencies: BehaviorSubject<FlightDateFrequencyDto[]> = new BehaviorSubject<FlightDateFrequencyDto[]>([]);
  allFlightDateFrequencies = this._allFlightDateFrequencies.asObservable();


  // displayed data
  private _currentlyDisplayedAirports: BehaviorSubject<AirportRender[]> = new BehaviorSubject<AirportRender[]>([]);
  currentlyDisplayedAirports = this._currentlyDisplayedAirports.asObservable();

  private _renderedRoutes: BehaviorSubject<LegRender[]> = new BehaviorSubject<LegRender[]>([]);
  renderedRoutes = this._renderedRoutes.asObservable();


  // filter
  private _timeFilter: BehaviorSubject<TimeFilter> = new BehaviorSubject<TimeFilter>(DefaultTimeFilter);
  timeFilter = this._timeFilter.asObservable();

  private _generalFilter: BehaviorSubject<GeneralFilter> = new BehaviorSubject<GeneralFilter>(DefaultGeneralFilter);
  generalFilter = this._generalFilter.asObservable();

  private _routeFilter: BehaviorSubject<RouteFilter> = new BehaviorSubject<RouteFilter>(DefaultRouteFilter);
  routeFilter = this._routeFilter.asObservable();

  private _selectedAirportFilter: BehaviorSubject<SelectedAirportFilter> = new BehaviorSubject<SelectedAirportFilter>(DefaultSelectedAirportFilter);
  selectedAirportFilter = this._selectedAirportFilter.asObservable();

  private _detailSelectionType: BehaviorSubject<DetailSelectionType> = new BehaviorSubject<DetailSelectionType>(DetailSelectionType.AIRPORT);
  detailSelectionType = this._detailSelectionType.asObservable();


  // selected data

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
  }

  // GETTERS
  getAllAirports(): AirportRender[] {
    return this.allAirports;
  }

  getSelectedAirportFilter(): SelectedAirportFilter {
    return this._selectedAirportFilter.getValue();
  }

  getSelectedRoute(): FlightScheduleRouteDto {
    return this._selectedRoute.getValue();
  }

  getSelectedAirportRoutesOutgoing(): boolean {
    return this._selectedAirportRoutesOutgoing.getValue();
  }

  getSelectedAirportRoutesIncoming(): boolean {
    return this._selectedAirportRoutesIncoming.getValue();
  }

  getAllLegRenders() {
    return this.allLegRenders;
  }

  getSpecificFlightSchedule(id: number): FlightSchedule {
    if (this.fetchedFlightSchedule?.flightScheduleId == id) return this.fetchedFlightSchedule;
    this.getFlightScheduleById(id);
    return this.fetchedFlightSchedule;
  }

  getAllLegsForSpecificRoute(id: number): LegRender[] {
    return this.allLegRenders; //TODO overhaul
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

  getFurthestFlightLeg(): LegRender {
    if(this.allLegRenders.length == 0) return LegRender.create();
    return this.allLegRenders.reduce((a, b) => a.distanceKilometers > b.distanceKilometers ? a : b);
  }

  getLongestFlightLeg(): LegRender {
    if(this.allLegRenders.length == 0) return LegRender.create();
    return this.allLegRenders.reduce((a, b) => a.durationMinutes > b.durationMinutes ? a : b);
  }

  getCurrentlyDisplayedRoutesForSelectedAirport(): LegRender[] {
    const selectedAirportIataCode = this.getSelectedAirportFilter().iataCode;
    return this.allLegRenders; //TODO overhaul
  }

  getAirportRenderByIataCode(iataCode: string): AirportRender | undefined {
    return this.allAirports.find(airport => airport.iataCode == iataCode);
  }

  // SETTERS

  setSelectedAirportFilter(selectedAirportFilter: SelectedAirportFilter): void {
    this._selectedAirportFilter.next(selectedAirportFilter);

    if (this.getGeneralFilter().routeDisplayType == RouteDisplayType.ROUTEDISPLAYTYPE_SPECIFICAIRPORT) {
      this.updateRenderedRoutes();
    }
  }

  setSelectedRoute(route: FlightScheduleRouteDto): void {
    this._selectedRoute.next(route);
  }

  private setCurrentlyDisplayedAirports(airports: AirportRender[]): void {
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

    this.updateRenderedRoutes();
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
    this.updateRenderedRoutes();
  }

  // FETCHING DATA
  private getAirports(): void {
    this.dataService.getAirports().subscribe(airports => {
      this.allAirports = airports;
      this.reRenderAirports();
    });
  }

  private getDistinctFlightScheduleLegsForRendering(): void {
    this.dataService.getDistinctFlightScheduleLegsForRendering(this.getTimeFilter(), this.getGeneralFilter(), this.getRouteFilter(), this.getSelectedAirportFilter()).subscribe(legRenders => {
      this.allLegRenders = legRenders;
      this._renderedRoutes.next(legRenders);
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
        this.setCurrentlyDisplayedAirports(this.filterService.getAllAirportsPresentInLegRenders(this.getAllLegRenders(), this.getAllAirports()));
        break;
      case AirportDisplayType.AIRPORTDISPLAYTYPE_NONE:
        this.setCurrentlyDisplayedAirports([]);
        break;
    }
  }

  private updateRenderedRoutes() {
    this.getDistinctFlightScheduleLegsForRendering();

    if (this.getDetailSelectionType() == DetailSelectionType.ROUTE) { //TODO overhaul && !routesToBeDisplayed.includes(this.getSelectedRoute())
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
