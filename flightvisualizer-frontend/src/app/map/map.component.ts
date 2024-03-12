import {Component, OnDestroy, OnInit,} from '@angular/core';
import mapboxgl from "mapbox-gl";
import {Airport, FlightScheduleRouteDto, SelectedDateRange, SelectedTimeRange} from "../core/dto/airport";
import 'mapbox-gl/dist/mapbox-gl.css';
import {GeoService} from "../core/service/geo.service";
import {AirportDisplayType, RouteDisplayType, RouteFilterType} from "../core/enum";
import {FilterService} from "../core/service/filter.service";
import {DataStoreService} from "../core/service/data-store.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit, OnDestroy {

  // Constants
  maxTime = 1439;
  minTime = 0;

  // @ts-ignore
  map: mapboxgl.Map;

  // Subscriptions
  currentlyRenderedAirportsSubscription!: Subscription;
  currentlyRenderedRoutesSubscription!: Subscription;
  flightDateFrequenciesSubscription!: Subscription;
  selectedRouteSubscription!: Subscription;
  selectedAirportSubscription!: Subscription;

  // UI data
  airportDisplayTypes = Object.values(AirportDisplayType);
  airportDisplayType: AirportDisplayType = AirportDisplayType.ALL;
  routeDisplayTypes = Object.values(RouteDisplayType);
  routeDisplayType: RouteDisplayType = RouteDisplayType.ALL;
  routeFilterTypes = Object.values(RouteFilterType);
  routeFilterType: RouteFilterType = RouteFilterType.DISTANCE;
  flightDateFrequencies: Set<string> = new Set();
  selectedTimeRange: SelectedTimeRange = new SelectedTimeRange(this.minTime, this.maxTime);

  // UI state
  histogramData: number[] = [];
  selectedAirport: Airport = new Airport();
  selectedRoute: FlightScheduleRouteDto = new FlightScheduleRouteDto();
  selectionType: string = 'airport';
  selectedDateRange: SelectedDateRange = new SelectedDateRange(new Date(), new Date());

  lowerValue = 10;
  upperValue = 90;

  dateFilter = this.getIsDateAvailableInputFilter();

  constructor(private geoService: GeoService, private filterSerice: FilterService, private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.selectedDateRange = this.dataStoreService.getSelectedDateRange();

    this.currentlyRenderedAirportsSubscription = this.dataStoreService.currentlyDisplayedAirports.subscribe(airports => {
      this.replaceCurrentlyRenderedAirports(airports);

      if(!airports.includes(this.selectedAirport)) {
        this.selectedAirport = new Airport();
        this.dataStoreService.setSelectedAirport(this.selectedAirport);
      }
    });

    this.currentlyRenderedRoutesSubscription = this.dataStoreService.renderedRoutes.subscribe(routes => {
      this.replaceCurrentlyRenderedRoutes(routes);

      if(!routes.includes(this.selectedRoute)) {
        this.selectedRoute = new FlightScheduleRouteDto();
        this.dataStoreService.setSelectedRoute(this.selectedRoute);
      }
    });

    this.selectedRouteSubscription = this.dataStoreService.selectedRoute.subscribe(route => {
      this.selectedRoute = route;

      this.highlightSelectedRoute();
    });

    this.selectedAirportSubscription = this.dataStoreService.selectedAirport.subscribe(airport => {
      this.selectedAirport = airport;

      this.highlightSelectedAirport();
    });

    this.flightDateFrequenciesSubscription = this.dataStoreService.allFlightDateFrequencies.subscribe(frequencies => {
      const dates = frequencies.map(frequency => {
        const date = new Date(frequency.startDateUtc);
        return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
      });
      this.flightDateFrequencies = new Set(dates);
      this.dateFilter = this.getIsDateAvailableInputFilter();
    });

    mapboxgl.accessToken = 'pk.eyJ1IjoiZXJpamwiLCJhIjoiY2xza2JpemdmMDIzejJyczBvZGk2aG44eiJ9.eJkFfrXg1dGFasDJRkmnIg';
    this.map = new mapboxgl.Map({
      container: 'map',
      style: 'mapbox://styles/mapbox/dark-v11',
      center: [-74.5, 40],
      zoom: 0
    });

    this.map.on('load', () => {
      this.dataStoreService.setCurrentlyDisplayedRoutes(this.dataStoreService.getAllFlightScheduleRouteDtos());
      this.dataStoreService.setCurrentlyDisplayedAirports(this.dataStoreService.getAllAirports());

      this.map.resize();
    });

    this.map.on('click', (e) => {
      const features = this.map.queryRenderedFeatures(e.point, { layers: ['airportLayer', 'routeLayer'] });

      if (!features.length) {
        if (this.selectionType === 'airport') {
          this.selectedAirport = new Airport();
          this.dataStoreService.setSelectedAirport(this.selectedAirport);
        } else if (this.selectionType === 'route') {
          this.selectedRoute = new FlightScheduleRouteDto();
          this.dataStoreService.setSelectedRoute(this.selectedRoute);
        }
      }
    });
  }

  onSelectionTypeChange(): void {
    if (this.selectionType === 'airport') {
      this.selectedRoute = new FlightScheduleRouteDto();
      this.dataStoreService.setSelectedRoute(this.selectedRoute);

      this.enableAirportLayerSelection();
    } else if (this.selectionType === 'route') {
      this.selectedAirport = new Airport();
      this.dataStoreService.setSelectedAirport(this.selectedAirport);

      this.enableRouteLayerSelection();
    }
  }

  // @ts-ignore
  airportLayerClickHandler = (e) => {
    // @ts-ignore
    const clickedAirport = e.features[0];
    // @ts-ignore
    const selectedAirport = this.dataStoreService.getAllAirports().find(airport => airport.iataAirportCode === clickedAirport.properties.iataAirportCode);
    if (selectedAirport && selectedAirport.iataAirportCode != '' && selectedAirport.iataAirportCode != this.selectedAirport.iataAirportCode) {
      this.selectedAirport = selectedAirport;
      this.dataStoreService.setSelectedAirport(this.selectedAirport);
      if (this.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) this.onSpecificAirportChange();
    }
  };

  getIsDateAvailableInputFilter() {
    return (date: Date | null): boolean => {
      if (!date || !this.flightDateFrequencies) {
        return false;
      }
      const dateString = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
      return this.flightDateFrequencies.has(dateString);
    };
  }

  // @ts-ignore
  routeLayerClickHandler = (e) => {
    // @ts-ignore
    const clickedRoute = e.features[0];
    // @ts-ignore
    this.selectedRoute = this.dataStoreService.getAllFlightScheduleRouteDtos().find(route =>
      route.originAirport.iataAirportCode === clickedRoute.properties.originAirport &&
      route.destinationAirport.iataAirportCode === clickedRoute.properties.destinationAirport
    );
    this.dataStoreService.setSelectedRoute(this.selectedRoute);
  }

  layerMouseEnterHandler = () => {
    this.map.getCanvas().style.cursor = 'pointer';
  }

  layerMouseLeaveHandler = () => {
    this.map.getCanvas().style.cursor = '';
  }

  onAirportDisplayTypeChange(): void {
    this.renderAirports();
  }

  protected convertIntToTimeOfDay(value: number | null): string {
    if(value == null) return '';
    let hours = Math.floor((value)/60);
    let minutes = Math.floor((value)%60);

    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
  }

  onDateRangeChange(): void {
    this.dataStoreService.setSelectedDateRange(this.selectedDateRange);
    this.selectedTimeRange = new SelectedTimeRange(this.minTime, this.maxTime);
    this.routeDisplayType = RouteDisplayType.ALL;
    this.onTimeRangeChange();
  }

  onTimeRangeChange(): void  {
    this.dataStoreService.setSelectedTimeRange(this.selectedTimeRange);
  }

  renderAirports(): void {
    if (this.airportDisplayType === AirportDisplayType.ALL) {
      this.dataStoreService.setCurrentlyDisplayedAirports(this.dataStoreService.getAllAirports());
    } else if (this.airportDisplayType === AirportDisplayType.WITHROUTES) {
      this.dataStoreService.setCurrentlyDisplayedAirports(this.filterSerice.getAllAirportsPresentInFlightScheduleRouteDtos(this.dataStoreService.getRenderedRoutes()));
    } else if (this.airportDisplayType === AirportDisplayType.NONE) {
      this.dataStoreService.setCurrentlyDisplayedAirports([]);
    }
  }

  renderRoutes(): void {
    if (this.routeDisplayType === RouteDisplayType.ALL) {
      this.dataStoreService.setCurrentlyDisplayedRoutes(this.dataStoreService.getAllFlightScheduleRouteDtosWithTimeFilter());
    } else if (this.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) {
      this.dataStoreService.setCurrentlyDisplayedRoutes(this.dataStoreService.getFlightScheduleRoutesForSelectedAirportWithTimeFilter());
    }
  }

  highlightSelectedRoute(): void {
    this.geoService.removeLayerFromMap(this.map, 'routeHighlightLayer');
    this.geoService.removeSourceFromMap(this.map, 'routeHighlightSource');

    if (this.selectedRoute.originAirport.iataAirportCode != '' && this.selectedRoute.destinationAirport.iataAirportCode != '') {
      this.geoService.highlightRouteOnMap(this.map, 'routeHighlightSource', 'routeHighlightLayer', this.selectedRoute);
    }
  }

  highlightSelectedAirport(): void {
    this.geoService.removeLayerFromMap(this.map, 'airportHighlightLayer');
    this.geoService.removeSourceFromMap(this.map, 'airportHighlightSource');

    if (this.selectedAirport.iataAirportCode != '') {
      this.geoService.highlightAirportOnMap(this.map, 'airportHighlightSource', 'airportHighlightLayer', this.selectedAirport);
    }
  }

  onRouteDisplayTypeChange(): void {
    this.renderRoutes();
  }

  onRouteFilterTypeChange(): void {

  }

  onSpecificAirportChange(): void {
    this.renderRoutes();
    this.renderAirports();
  }

  replaceCurrentlyRenderedAirports(newAirports: Airport[]): void {
    let airportsGeoJson = this.geoService.convertAirportsToGeoJson(newAirports);

    this.geoService.addFeatureCollectionSourceToMap(this.map, 'airportSourceTEMP', airportsGeoJson);
    this.geoService.addLayerTypeCircleToMap(this.map, 'airportLayerTEMP', 'airportSourceTEMP', 8, '#eea719');

    this.geoService.removeLayerFromMap(this.map, 'airportLayer');
    this.geoService.removeSourceFromMap(this.map, 'airportSource');

    this.geoService.addFeatureCollectionSourceToMap(this.map, 'airportSource', airportsGeoJson);
    this.geoService.addLayerTypeCircleToMap(this.map, 'airportLayer', 'airportSource', 8, '#eea719');

    this.geoService.removeLayerFromMap(this.map, 'airportLayerTEMP');
    this.geoService.removeSourceFromMap(this.map, 'airportSourceTEMP');

    if (this.selectionType === 'airport') {
      this.enableAirportLayerSelection();
    }
  }

  replaceCurrentlyRenderedRoutes(newRoutes: FlightScheduleRouteDto[]): void {
    let routesGeoJson = this.geoService.convertFlightScheduleRouteDtosToGeoJson(newRoutes);

    this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSourceTEMP', routesGeoJson);
    this.geoService.addLayerTypeLineToMap(this.map, 'routeLayerTEMP', 'routeSourceTEMP', {}, {
      'line-color': '#ffffff',
      'line-width': 2
    })

    this.geoService.removeLayerFromMap(this.map, 'routeLayer');
    this.geoService.removeSourceFromMap(this.map, 'routeSource');

    this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSource', routesGeoJson);
    this.geoService.addLayerTypeLineToMap(this.map, 'routeLayer', 'routeSource', {}, {
      'line-color': '#ffffff',
      'line-width': 2
    })

    this.geoService.removeLayerFromMap(this.map, 'routeLayerTEMP');
    this.geoService.removeSourceFromMap(this.map, 'routeSourceTEMP');

    if (this.selectionType === 'routes') {
      this.enableRouteLayerSelection();
    }
  }

  enableRouteLayerSelection(): void {
    this.map.off('click', 'airportLayer', this.airportLayerClickHandler);
    this.map.off('mouseenter', 'airportLayer', this.layerMouseEnterHandler);
    this.map.off('mouseleave', 'airportLayer', this.layerMouseLeaveHandler);

    this.map.off('click', 'routeLayer', this.routeLayerClickHandler);
    this.map.off('mouseenter', 'routeLayer', this.layerMouseEnterHandler);
    this.map.off('mouseleave', 'routeLayer', this.layerMouseLeaveHandler);

    this.map.on('click', 'routeLayer', this.routeLayerClickHandler);
    this.map.on('mouseenter', 'routeLayer', this.layerMouseEnterHandler);
    this.map.on('mouseleave', 'routeLayer', this.layerMouseLeaveHandler);
  }

  enableAirportLayerSelection(): void {
    this.map.off('click', 'routeLayer', this.routeLayerClickHandler);
    this.map.off('mouseenter', 'routeLayer', this.layerMouseEnterHandler);
    this.map.off('mouseleave', 'routeLayer', this.layerMouseLeaveHandler);

    this.map.off('click', 'airportLayer', this.airportLayerClickHandler);
    this.map.off('mouseenter', 'airportLayer', this.layerMouseEnterHandler);
    this.map.off('mouseleave', 'airportLayer', this.layerMouseLeaveHandler);

    this.map.on('click', 'airportLayer', this.airportLayerClickHandler);
    this.map.on('mouseenter', 'airportLayer', this.layerMouseEnterHandler);
    this.map.on('mouseleave', 'airportLayer', this.layerMouseLeaveHandler);
  }

  //this.histogramData = this.geoService.generateRouteDistanceArray(this.allFlightScheduleRouteDtos);

  ngOnDestroy(): void {
    this.currentlyRenderedAirportsSubscription.unsubscribe();
    this.currentlyRenderedRoutesSubscription.unsubscribe();
    this.flightDateFrequenciesSubscription.unsubscribe();
    this.selectedRouteSubscription.unsubscribe();
    this.selectedAirportSubscription.unsubscribe();
  }

  protected readonly AirportDisplayType = AirportDisplayType;
}
