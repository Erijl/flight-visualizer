import {Injectable} from '@angular/core';
import {
  DefaultGeneralFilter, DefaultRouteFilter, DefaultSelectedAirportFilter, DefaultTimeFilter,
  FlightSchedule,
  FlightScheduleRouteDto
} from "../dto/airport";
import {DataService} from "./data.service";
import {BehaviorSubject} from "rxjs";
import {DetailSelectionType} from "../enum";
import {GeneralFilter, RouteFilter, SelectedAirportFilter, TimeFilter} from "../../protos/filters";
import {AirportRender, FlightDateFrequency, LegRender} from "../../protos/objects";
import {RouteDisplayType} from "../../protos/enums";

@Injectable({
  providedIn: 'root'
})
export class DataStoreService {

  // 'raw' data
  allLegRenders: LegRender[] = [];
  allAirports: AirportRender[] = [];
  fetchedFlightSchedule: FlightSchedule = new FlightSchedule();

  furthestFLightLeg: LegRender = LegRender.create();
  longestFlightLeg: LegRender = LegRender.create();

  private _allFlightDateFrequencies: BehaviorSubject<FlightDateFrequency[]> = new BehaviorSubject<FlightDateFrequency[]>([]);
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


  constructor(private dataService: DataService) {
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
    return this.longestFlightLeg
  }

  getLongestFlightLeg(): LegRender {
    return this.longestFlightLeg;
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
    console.log("Selected airport filter: ", selectedAirportFilter);

    if (this.getGeneralFilter().routeDisplayType == RouteDisplayType.ROUTEDISPLAYTYPE_SPECIFICAIRPORT) {
      this.updateRenderedRoutes();
    }
  }

  setSelectedRoute(route: FlightScheduleRouteDto): void {
    this._selectedRoute.next(route);
  }

  setTimeFilter(timeFilter: TimeFilter): void {
    this._timeFilter.next(timeFilter);

    this.updateRenderedRoutes();
  }

  setGeneralFilter(generalFilter: GeneralFilter): void {
    this._generalFilter.next(generalFilter);

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
  private getDistinctFlightScheduleLegsForRendering(): void {
    this.dataService.getDistinctFlightScheduleLegsForRendering(this.getTimeFilter(), this.getGeneralFilter(), this.getRouteFilter(), this.getSelectedAirportFilter()).subscribe(sandboxModeResponseObject => {
      this.furthestFLightLeg = sandboxModeResponseObject.furthestFlightLeg ?? LegRender.create()
      this.longestFlightLeg = sandboxModeResponseObject.longestFlightLeg ?? LegRender.create();

      this.allAirports = sandboxModeResponseObject.airportRenders;
      this.allLegRenders = sandboxModeResponseObject.legRenders;

      this._renderedRoutes.next(sandboxModeResponseObject.legRenders);
      this._currentlyDisplayedAirports.next(sandboxModeResponseObject.airportRenders);
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

  reRenderRoutes() {
    this.updateRenderedRoutes();
  }

  private updateRenderedRoutes() {
    this.getDistinctFlightScheduleLegsForRendering();

    if (this.getDetailSelectionType() == DetailSelectionType.ROUTE) { //TODO overhaul && !routesToBeDisplayed.includes(this.getSelectedRoute())
      this.setSelectedRoute(new FlightScheduleRouteDto());
    }
  }

  initTimeFilter() {
    const flightDateFrequencies = this._allFlightDateFrequencies.getValue();
    let timeFilter = DefaultTimeFilter;

    if(!timeFilter || !timeFilter.dateRange) return;

    if (!flightDateFrequencies || flightDateFrequencies.length == 0) {
      //TODO oh boy...
    }

    timeFilter.dateRange.start = flightDateFrequencies[0].date;
    if(timeFilter.dateRange.start) timeFilter.dateRange.start = new Date(timeFilter.dateRange.start);

    this.setTimeFilter(timeFilter);
  }
}
