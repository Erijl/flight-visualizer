import { Component, OnDestroy, OnInit } from '@angular/core';
import mapboxgl from "mapbox-gl";
import {Airport, FlightScheduleRouteDto, GeneralFilter} from "../core/dto/airport";
import 'mapbox-gl/dist/mapbox-gl.css';
import {GeoService} from "../core/service/geo.service";
import {AirportDisplayType, DetailSelectionType, RouteDisplayType} from "../core/enum";
import {FilterService} from "../core/service/filter.service";
import {DataStoreService} from "../core/service/data-store.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit, OnDestroy {

  // @ts-ignore
  map: mapboxgl.Map;

  // Subscriptions
  currentlyRenderedAirportsSubscription!: Subscription;
  currentlyRenderedRoutesSubscription!: Subscription;
  selectedRouteSubscription!: Subscription;
  selectedAirportSubscription!: Subscription;
  generalFilterSubscription!: Subscription;
  detailSelectionTypeSubscription!: Subscription;

  // UI data
  generalFilter: GeneralFilter = new GeneralFilter(AirportDisplayType.ALL, RouteDisplayType.ALL);

  // UI state
  selectedAirport: Airport = new Airport();
  selectedRoute: FlightScheduleRouteDto = new FlightScheduleRouteDto();
  selectionType: DetailSelectionType = DetailSelectionType.AIRPORT;

  constructor(private geoService: GeoService, private filterService: FilterService, private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {

    this.currentlyRenderedAirportsSubscription = this.dataStoreService.currentlyDisplayedAirports.subscribe(airports => {
      this.replaceCurrentlyRenderedAirports(airports);

      if (!airports.includes(this.selectedAirport)) {
        this.selectedAirport = new Airport();
        this.dataStoreService.setSelectedAirport(this.selectedAirport);
      }
    });

    this.currentlyRenderedRoutesSubscription = this.dataStoreService.renderedRoutes.subscribe(routes => {
      this.replaceCurrentlyRenderedRoutes(routes);

      if (!routes.includes(this.selectedRoute)) {
        this.selectedRoute = new FlightScheduleRouteDto();
        this.dataStoreService.setSelectedRoute(this.selectedRoute);
      }

      if (this.generalFilter.airportDisplayType === AirportDisplayType.WITHROUTES) {
        this.renderAirports();
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

    this.generalFilterSubscription = this.dataStoreService.generalFilter.subscribe(generalFilter => {
      this.generalFilter = generalFilter;

      this.renderAirports();
      this.renderRoutes();
    });

    this.detailSelectionTypeSubscription = this.dataStoreService.detailSelectionType.subscribe((type: DetailSelectionType) => {
      this.selectionType = type;
      this.onSelectionTypeChange();
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

      const temp = this.map.getStyle().layers;
      console.log(temp);
      console.log('LAYER LAYER LAYER');

      const tempSource = this.map.getSource('airportSource');
      console.log(tempSource);
      console.log('SOURCE SOURCE SOURCE');

      this.map.resize();
    });

    this.map.on('click', (e) => {
      const features = this.map.queryRenderedFeatures(e.point, {layers: ['airportLayer', 'routeLayer']});

      if (!features.length) {
        if (this.selectionType === DetailSelectionType.AIRPORT) {
          this.selectedAirport = new Airport();
          this.dataStoreService.setSelectedAirport(this.selectedAirport);
        } else if (this.selectionType === DetailSelectionType.ROUTE) {
          this.selectedRoute = new FlightScheduleRouteDto();
          this.dataStoreService.setSelectedRoute(this.selectedRoute);
        }
      }
    });
  }

  onSelectionTypeChange(): void {
    if (this.selectionType === DetailSelectionType.AIRPORT) {
      this.selectedRoute = new FlightScheduleRouteDto();
      this.dataStoreService.setSelectedRoute(this.selectedRoute);

      this.enableAirportLayerSelection();
    } else if (this.selectionType === DetailSelectionType.ROUTE) {
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
      if (this.generalFilter.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) this.onSpecificAirportChange();
    }
  };

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

  //TODO move handling to dataStoreService
  renderAirports(): void {
    if (this.generalFilter.airportDisplayType === AirportDisplayType.ALL) {
      this.dataStoreService.setCurrentlyDisplayedAirports(this.dataStoreService.getAllAirports());
    } else if (this.generalFilter.airportDisplayType === AirportDisplayType.WITHROUTES) {
      this.dataStoreService.setCurrentlyDisplayedAirports(this.filterService.getAllAirportsPresentInFlightScheduleRouteDtos(this.dataStoreService.getRenderedRoutes()));
    } else if (this.generalFilter.airportDisplayType === AirportDisplayType.NONE) {
      this.dataStoreService.setCurrentlyDisplayedAirports([]);
    }
  }

  //TODO move handling to dataStoreService
  renderRoutes(): void {
    if (this.generalFilter.routeDisplayType === RouteDisplayType.ALL) {
      this.dataStoreService.setCurrentlyDisplayedRoutes(this.dataStoreService.getAllFlightScheduleRouteDtosWithTimeFilter());
    } else if (this.generalFilter.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) {
      this.dataStoreService.setCurrentlyDisplayedRoutes(this.dataStoreService.getFlightScheduleRoutesForSelectedAirportWithTimeFilter());
    } else if (this.generalFilter.routeDisplayType === RouteDisplayType.ONLYWITHINSAMECOUNTRY) {
      this.dataStoreService.setCurrentlyDisplayedRoutes(this.dataStoreService.getFlightScheduleRoutesWithinSameCountryWithTimeFilter());
    } else if (this.generalFilter.routeDisplayType === RouteDisplayType.WITHINSAMEREGION) {
      this.dataStoreService.setCurrentlyDisplayedRoutes(this.dataStoreService.getFlightScheduleRoutesWithinSameRegionWithTimeFilter());
    } else if (this.generalFilter.routeDisplayType === RouteDisplayType.WITHINSAMETIMEZONE) {
      this.dataStoreService.setCurrentlyDisplayedRoutes(this.dataStoreService.getFlightScheduleRoutesWithinSameTimezoneWithTimeFilter());
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

  onSpecificAirportChange(): void {
    this.renderRoutes();
    this.renderAirports();
  }

  replaceCurrentlyRenderedAirports(newAirports: Airport[]): void {
    let airportsGeoJson = this.geoService.convertAirportsToGeoJson(newAirports);

    if(this.map.getSource('airportSource')) {
      // @ts-ignore
      this.map.getSource('airportSource').setData({
        'type': 'FeatureCollection',
        'features': airportsGeoJson
      });
    } else {
      this.geoService.addFeatureCollectionSourceToMap(this.map, 'airportSource', airportsGeoJson);
      this.geoService.addLayerTypeCircleToMap(this.map, 'airportLayer', 'airportSource', 8, '#eea719');
    }

    if (this.selectionType === DetailSelectionType.AIRPORT) {
      this.enableAirportLayerSelection();
    }
  }

  replaceCurrentlyRenderedRoutes(newRoutes: FlightScheduleRouteDto[]): void {
    let routesGeoJson = this.geoService.convertFlightScheduleRouteDtosToGeoJson(newRoutes);

    if(this.map.getSource('routeSource')) {
      // @ts-ignore
      this.map.getSource('routeSource').setData({
        'type': 'FeatureCollection',
        'features': routesGeoJson
      });
    } else {
      this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSource', routesGeoJson);
      this.geoService.addLayerTypeLineToMap(this.map, 'routeLayer', 'routeSource', {}, {
        'line-color': '#ffffff',
        'line-width': 2
      });
    }

    if (this.selectionType === DetailSelectionType.ROUTE) {
      this.enableRouteLayerSelection();
    }
  }

  enableRouteLayerSelection(): void {
    this.disableLayerSelection();

    this.map.on('click', 'routeLayer', this.routeLayerClickHandler);
    this.map.on('mouseenter', 'routeLayer', this.layerMouseEnterHandler);
    this.map.on('mouseleave', 'routeLayer', this.layerMouseLeaveHandler);
  }

  enableAirportLayerSelection(): void {
    this.disableLayerSelection();

    this.map.on('click', 'airportLayer', this.airportLayerClickHandler);
    this.map.on('mouseenter', 'airportLayer', this.layerMouseEnterHandler);
    this.map.on('mouseleave', 'airportLayer', this.layerMouseLeaveHandler);
  }

  disableLayerSelection(): void {
    this.map.off('click', 'routeLayer', this.routeLayerClickHandler);
    this.map.off('mouseenter', 'routeLayer', this.layerMouseEnterHandler);
    this.map.off('mouseleave', 'routeLayer', this.layerMouseLeaveHandler);

    this.map.off('click', 'airportLayer', this.airportLayerClickHandler);
    this.map.off('mouseenter', 'airportLayer', this.layerMouseEnterHandler);
    this.map.off('mouseleave', 'airportLayer', this.layerMouseLeaveHandler);
  }

  //this.histogramData = this.geoService.generateRouteDistanceArray(this.allFlightScheduleRouteDtos);

  ngOnDestroy(): void {
    this.currentlyRenderedAirportsSubscription.unsubscribe();
    this.currentlyRenderedRoutesSubscription.unsubscribe();
    this.selectedRouteSubscription.unsubscribe();
    this.selectedAirportSubscription.unsubscribe();
    this.generalFilterSubscription.unsubscribe();
    this.detailSelectionTypeSubscription.unsubscribe();
  }

  protected readonly AirportDisplayType = AirportDisplayType;
}
