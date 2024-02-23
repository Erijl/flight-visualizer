import {Component, OnInit} from '@angular/core';
import mapboxgl from "mapbox-gl";
import {Airport, FlightScheduleRouteDto} from "../core/dto/airport";
import {DataService} from "../core/service/data.service";
import 'mapbox-gl/dist/mapbox-gl.css';
import {GeoService} from "../core/service/geo.service";
import {AirportDisplayType, RouteDisplayType, RouteFilterType} from "../core/enum";
import {FilterService} from "../core/service/filter.service";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit {
  // @ts-ignore
  map: mapboxgl.Map;

  // 'raw' data
  allAirports: Airport[] = [];
  allFlightScheduleRouteDtos: FlightScheduleRouteDto[] = [];//TODO use only Routes from Airports not Railway statsions etc.

  // UI data
  airportDisplayTypes = Object.values(AirportDisplayType);
  airportDisplayType: AirportDisplayType = AirportDisplayType.ALL;
  routeDisplayTypes = Object.values(RouteDisplayType);
  routeDisplayType: RouteDisplayType = RouteDisplayType.ALL;
  routeFilterTypes = Object.values(RouteFilterType);
  routeFilterType: RouteFilterType = RouteFilterType.DISTANCE;

  flightScheduleRouteDtosToDisplay: FlightScheduleRouteDto[] = [];

  selectedAirportSpecificRoutes: Airport = new Airport();
  selectedAirportSpecificRoutesString: string = "";
  specificAirportRoutesOutgoing: boolean = true;
  specificAirportRoutesIncoming: boolean = true;

  // UI state
  specificAirportRoutesContainerVisible: boolean = false;


  constructor(private dataService: DataService, private geoService: GeoService, private filterSerice: FilterService) { }

  ngOnInit(): void {
    this.getAirports();
    this.getFlightScheduleLegRoutes();

    mapboxgl.accessToken = 'pk.eyJ1IjoiZXJpamwiLCJhIjoiY2xza2JpemdmMDIzejJyczBvZGk2aG44eiJ9.eJkFfrXg1dGFasDJRkmnIg';
    this.map = new mapboxgl.Map({
      container: 'map',
      style: 'mapbox://styles/mapbox/dark-v11',
      center: [-74.5, 40],
      zoom: 0
    });

    this.map.on('load', () => {
      this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSource', this.geoService.convertFlightScheduleRouteDtosToGeoJson(this.flightScheduleRouteDtosToDisplay));
      this.geoService.addFeatureCollectionSourceToMap(this.map, 'airportSource', this.geoService.convertAirportsToGeoJson(this.allAirports));

      this.geoService.addLayerTypeLineToMap(this.map, 'routeLayer', 'routeSource', {}, {'line-color': '#ffffff', 'line-width': 2})
      this.geoService.addLayerTypeCircleToMap(this.map, 'airportLayer', 'airportSource', 8, '#eea719');

      this.map.resize();
    });
  }

  onAirportDisplayTypeChange(): void {
    this.renderAirports();
  }

  renderAirports(): void {
    //TODO optimize, check state before

    if(this.airportDisplayType === AirportDisplayType.ALL) {
      this.geoService.removeLayerFromMap(this.map, 'airportLayer');
      this.geoService.removeSourceFromMap(this.map, 'airportSource');

      this.geoService.addFeatureCollectionSourceToMap(this.map, 'airportSource', this.geoService.convertAirportsToGeoJson(this.allAirports));
      this.geoService.addLayerTypeCircleToMap(this.map, 'airportLayer', 'airportSource', 8, '#eea719');
    } else if(this.airportDisplayType === AirportDisplayType.WITHROUTES) {
      this.geoService.removeLayerFromMap(this.map, 'airportLayer');
      this.geoService.removeSourceFromMap(this.map, 'airportSource');

      this.geoService.addFeatureCollectionSourceToMap(this.map, 'airportSource', this.geoService.convertAirportsToGeoJson(this.filterSerice.getAllAirportsPresentInFlightScheduleRouteDtos(this.flightScheduleRouteDtosToDisplay)));
      this.geoService.addLayerTypeCircleToMap(this.map, 'airportLayer', 'airportSource', 8, '#eea719');
    } else if(this.airportDisplayType === AirportDisplayType.NONE) {
      this.geoService.removeLayerFromMap(this.map, 'airportLayer');
      this.geoService.removeSourceFromMap(this.map, 'airportSource');
    }
  }

  renderRoutes(): void {
    //TODO optimize, check state before
    if(this.routeDisplayType === RouteDisplayType.ALL) {
      this.specificAirportRoutesContainerVisible = false;
      this.geoService.removeLayerFromMap(this.map, 'routeLayer');
      this.geoService.removeSourceFromMap(this.map, 'routeSource');

      this.flightScheduleRouteDtosToDisplay = this.allFlightScheduleRouteDtos;
      this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSource', this.geoService.convertFlightScheduleRouteDtosToGeoJson(this.flightScheduleRouteDtosToDisplay));
      this.geoService.addLayerTypeLineToMap(this.map, 'routeLayer', 'routeSource', {}, {'line-color': '#ffffff', 'line-width': 2})

      this.renderAirports();
    } else if(this.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) {
      this.specificAirportRoutesContainerVisible = true;
      this.geoService.removeLayerFromMap(this.map, 'routeLayer');
      this.geoService.removeSourceFromMap(this.map, 'routeSource');

      this.flightScheduleRouteDtosToDisplay = this.filterSerice.getFlightScheduleRouteDtosByAirport(this.allFlightScheduleRouteDtos, this.selectedAirportSpecificRoutes, this.specificAirportRoutesIncoming, this.specificAirportRoutesOutgoing);
      this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSource', this.geoService.convertFlightScheduleRouteDtosToGeoJson(this.flightScheduleRouteDtosToDisplay));
      this.geoService.addLayerTypeLineToMap(this.map, 'routeLayer', 'routeSource', {}, {'line-color': '#ffffff', 'line-width': 2})

      this.renderAirports()
    }
  }

  onRouteDisplayTypeChange(): void {
    this.renderRoutes();
  }

  onSelectedAirportSpecificRoutesChange(): void {
    // @ts-ignore
    this.selectedAirportSpecificRoutes = this.allAirports.find(airport => airport.iataAirportCode === this.selectedAirportSpecificRoutesString);
    this.onRouteDisplayTypeChange();
  }

  onOutgoingChange(): void {
    this.renderRoutes();
    this.renderAirports();
  }

  onIncomingChange(): void {
    this.renderRoutes();
    this.renderAirports();
  }

  onRouteFilterTypeChange(): void {

  }

  onSliderChange(event: any): void {
    // Handle slider change
  }

  getAirports(): void {
    this.dataService.getAirports().subscribe(airports => {
      this.allAirports = airports;
    });
  }

  getFlightScheduleLegRoutes(): void {
    this.dataService.getFlightScheduleLegRoutes().subscribe(flightScheduleLegs => {
      this.allFlightScheduleRouteDtos = flightScheduleLegs;
      this.flightScheduleRouteDtosToDisplay = flightScheduleLegs;
    });
  }

  protected readonly AirportDisplayType = AirportDisplayType;
}
