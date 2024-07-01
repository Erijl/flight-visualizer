import {Injectable} from '@angular/core';
import {
  DefaultGeneralFilter, DefaultRouteFilter, DefaultSelectedAirportFilter, DefaultTimeFilter
} from "../dto/airport";
import {DataService} from "./data.service";
import {BehaviorSubject} from "rxjs";
import {DetailSelectionType} from "../enum";
import {GeneralFilter, RouteFilter, SelectedAirportFilter, TimeFilter} from "../../protos/filters";
import {
  AirportDetails,
  AirportRender,
  DetailedLegInformation,
  FlightDateFrequency,
  LegRender
} from "../../protos/objects";
import {RouteDisplayType} from "../../protos/enums";

@Injectable({
  providedIn: 'root'
})
export class DataStoreService {

  // 'raw' data
  allLegRenders: LegRender[] = [];
  allAirports: AirportRender[] = [];
  isInitialized: boolean = false;

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
  private _timeFilter: BehaviorSubject<TimeFilter> = new BehaviorSubject<TimeFilter>(TimeFilter.create(DefaultTimeFilter));
  timeFilter = this._timeFilter.asObservable();

  private _generalFilter: BehaviorSubject<GeneralFilter> = new BehaviorSubject<GeneralFilter>(GeneralFilter.create(DefaultGeneralFilter));
  generalFilter = this._generalFilter.asObservable();

  private _routeFilter: BehaviorSubject<RouteFilter> = new BehaviorSubject<RouteFilter>(RouteFilter.create(DefaultRouteFilter));
  routeFilter = this._routeFilter.asObservable();

  private _selectedAirportFilter: BehaviorSubject<SelectedAirportFilter> = new BehaviorSubject<SelectedAirportFilter>(SelectedAirportFilter.create(DefaultSelectedAirportFilter));
  selectedAirportFilter = this._selectedAirportFilter.asObservable();

  private _detailSelectionType: BehaviorSubject<DetailSelectionType> = new BehaviorSubject<DetailSelectionType>(DetailSelectionType.AIRPORT);
  detailSelectionType = this._detailSelectionType.asObservable();


  // selected data
  private _selectedRoute: BehaviorSubject<LegRender> = new BehaviorSubject<LegRender>(LegRender.create());
  selectedRoute = this._selectedRoute.asObservable();

  // details
  private _airportDetails: BehaviorSubject<AirportDetails> = new BehaviorSubject<AirportDetails>(AirportDetails.create());
  airportDetails = this._airportDetails.asObservable();

  private _routeDetails: BehaviorSubject<DetailedLegInformation[]> = new BehaviorSubject<DetailedLegInformation[]>([]);
  routeDetails = this._routeDetails.asObservable();

  // UI state
  private _showLoadingScreen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  showLoadingScreen = this._showLoadingScreen.asObservable();


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

  getSelectedRoute(): LegRender {
    return this._selectedRoute.getValue();
  }

  getAllLegRenders() {
    return this.allLegRenders;
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
    return this.furthestFLightLeg;
  }

  getLongestFlightLeg(): LegRender {
    return this.longestFlightLeg;
  }

  getAirportRenderByIataCode(iataCode: string): AirportRender | undefined {
    return this.allAirports.find(airport => airport.iataCode == iataCode);
  }

  getLegRendersForSelectedAirport(): LegRender[] {
    const filter = this.getSelectedAirportFilter();
    return this.allLegRenders.filter(leg => (filter.includingDepartures && leg.originAirportIataCode == filter.iataCode) || (filter.includingArrivals && leg.destinationAirportIataCode == filter.iataCode));
  }

  getLegRendersForSelectedRoute(): LegRender[] {
    const selectedLeg = this.getSelectedRoute();
    return this.allLegRenders.filter(leg => leg.originAirportIataCode == selectedLeg.originAirportIataCode && leg.destinationAirportIataCode == selectedLeg.destinationAirportIataCode);
  }

  // SETTERS

  setSelectedAirportFilter(selectedAirportFilter: SelectedAirportFilter): void {
    this._selectedAirportFilter.next(selectedAirportFilter);

    const render = this.getAirportRenderByIataCode(selectedAirportFilter.iataCode);
    if (render) {
      this.getAirportDetails(render);
    }

    if (this.getGeneralFilter().routeDisplayType == RouteDisplayType.ROUTEDISPLAYTYPE_SPECIFICAIRPORT) {
      this.updateRenderedRoutes();
    }
  }

  setSelectedRoute(route: LegRender): void {
    this._selectedRoute.next(route);

    const originRender = this.getAirportRenderByIataCode(route.originAirportIataCode);
    const destinationRender = this.getAirportRenderByIataCode(route.destinationAirportIataCode);
    if (originRender && destinationRender) {
      this.getAllLegsForSpecificRoute(route);
    }
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

  setAirportDetails(airportDetails: AirportDetails): void {
    this._airportDetails.next(airportDetails);
  }

  setRouteDetails(routeDetails: DetailedLegInformation[]): void {
    this._routeDetails.next(routeDetails);
  }

  setShowLoadingScreen(show: boolean): void {
    this._showLoadingScreen.next(show);
  }

  setIsInitialized(isInitialized: boolean): void {
    this.isInitialized = isInitialized;
  }

  // FETCHING DATA
  private getDistinctFlightScheduleLegsForRendering(): void {
    if(!this.isInitialized) return;
    this.setShowLoadingScreen(true);
    this.dataService.getDistinctFlightScheduleLegsForRendering(this.getTimeFilter(), this.getGeneralFilter(), this.getRouteFilter(), this.getSelectedAirportFilter()).subscribe(sandboxModeResponseObject => {
      this.furthestFLightLeg = sandboxModeResponseObject.furthestFlightLeg ?? LegRender.create()
      this.longestFlightLeg = sandboxModeResponseObject.longestFlightLeg ?? LegRender.create();

      this.allAirports = sandboxModeResponseObject.airportRenders;
      this.allLegRenders = sandboxModeResponseObject.legRenders;

      this._renderedRoutes.next(sandboxModeResponseObject.legRenders);
      this._currentlyDisplayedAirports.next(sandboxModeResponseObject.airportRenders); //TODO if selected airport isn't in this list, set it to null
      this.setShowLoadingScreen(false);
    });
  }

  private getAirportDetails(airportRender: AirportRender): void {
    this.dataService.getAirportDetails(airportRender).subscribe(airportDetails => {
      this.setAirportDetails(airportDetails);
    });
  }

  private getAllLegsForSpecificRoute(leg: LegRender): void {
    this.dataService.getAllLegsForSpecificRoute(leg, this.getTimeFilter()).subscribe(routeDetails => {
      this.setRouteDetails(routeDetails);
    });
  }

  private getFlightDateFrequencies(): void {
    this.dataService.getFlightDateFrequencies().subscribe(flightDateFrequencies => {
      this._allFlightDateFrequencies.next(flightDateFrequencies);
      this.initTimeFilter();
    });
  }

  // RENDERING

  reRenderRoutes() {
    this.updateRenderedRoutes();
  }

  private updateRenderedRoutes() {
    this.getDistinctFlightScheduleLegsForRendering();

    if (this.getDetailSelectionType() == DetailSelectionType.ROUTE) { //TODO overhaul && !routesToBeDisplayed.includes(this.getSelectedRoute())
      this.setSelectedRoute(LegRender.create());
    }
  }

  initTimeFilter() {
    const flightDateFrequencies = this._allFlightDateFrequencies.getValue();
    let timeFilter = TimeFilter.create(DefaultTimeFilter);

    if(!timeFilter || !timeFilter.dateRange) return;

    if (!flightDateFrequencies || flightDateFrequencies.length == 0) {
      //TODO oh boy...
    }

    timeFilter.dateRange.start = flightDateFrequencies[0].date;
    if(timeFilter.dateRange.start) timeFilter.dateRange.start = new Date(timeFilter.dateRange.start);

    this.setTimeFilter(timeFilter);
  }
}
