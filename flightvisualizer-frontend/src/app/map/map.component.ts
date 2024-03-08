import {AfterViewInit, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import mapboxgl from "mapbox-gl";
import {Airport, FlightScheduleRouteDto} from "../core/dto/airport";
import {DataService} from "../core/service/data.service";
import 'mapbox-gl/dist/mapbox-gl.css';
import {GeoService} from "../core/service/geo.service";
import {AirportDisplayType, RouteDisplayType, RouteFilterType} from "../core/enum";
import {FilterService} from "../core/service/filter.service";
import * as d3 from 'd3';
import {MatPaginator} from "@angular/material/paginator";
import {MatTableDataSource} from "@angular/material/table";
import {MatSort} from "@angular/material/sort";
import {AirportInfoComponent} from "../airport-info/airport-info.component";
import {DataStoreService} from "../core/service/data-store.service";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit{

  // @ts-ignore
  map: mapboxgl.Map;

  // UI data
  airportDisplayTypes = Object.values(AirportDisplayType);
  airportDisplayType: AirportDisplayType = AirportDisplayType.ALL;
  routeDisplayTypes = Object.values(RouteDisplayType);
  routeDisplayType: RouteDisplayType = RouteDisplayType.ALL;
  routeFilterTypes = Object.values(RouteFilterType);
  routeFilterType: RouteFilterType = RouteFilterType.DISTANCE;

  // UI state
  histogramData: number[] = [];
  selectedAirport: Airport = new Airport();
  selectionType: string = 'airport';

  lowerValue = 10;
  upperValue = 90;
  minValue = 0;
  maxValue = 100;

  // Callback Functions
  // @ts-ignore
  airportLayerClickHandler = (e) => {
    // @ts-ignore
    const clickedAirport = e.features[0];
    // @ts-ignore
    this.selectedAirport = this.allAirports.find(airport => airport.iataAirportCode === clickedAirport.properties.iataAirportCode);
    if(this.selectedAirport.iataAirportCode != "") {
      this.dataStoreService.setSelectedAirport(this.selectedAirport);
      if(this.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) this.onSpecificAirportChange();
    }
  };

  // ViewChild's

  constructor(private dataService: DataService, private geoService: GeoService, private filterSerice: FilterService, private dataStoreService: DataStoreService) { }

  ngOnInit(): void {
    mapboxgl.accessToken = 'pk.eyJ1IjoiZXJpamwiLCJhIjoiY2xza2JpemdmMDIzejJyczBvZGk2aG44eiJ9.eJkFfrXg1dGFasDJRkmnIg';
    this.map = new mapboxgl.Map({
      container: 'map',
      style: 'mapbox://styles/mapbox/dark-v11',
      center: [-74.5, 40],
      zoom: 0
    });

    this.map.on('load', () => {
      this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSource', this.geoService.convertFlightScheduleRouteDtosToGeoJson(this.dataStoreService.getCurrentlyDisplayedRoutes()));
      this.geoService.addFeatureCollectionSourceToMap(this.map, 'airportSource', this.geoService.convertAirportsToGeoJson(this.dataStoreService.getCurrentlyDisplayedAirports()));

      this.geoService.addLayerTypeLineToMap(this.map, 'routeLayer', 'routeSource', {}, {'line-color': '#ffffff', 'line-width': 2})
      this.geoService.addLayerTypeCircleToMap(this.map, 'airportLayer', 'airportSource', 8, '#eea719');

      this.map.on('click', 'airportLayer', this.airportLayerClickHandler);
      this.map.on('mouseenter', 'airportLayer', this.airportLayerMouseEnterHandler);
      this.map.on('mouseleave', 'airportLayer', this.airportLayerMouseLeaveHandler);

      this.map.resize();
    });
  }

  onSelectionTypeChange(): void {
    if(this.selectionType === 'airport') {
      this.map.on('click', 'airportLayer', this.airportLayerClickHandler);
      this.map.on('mouseenter', 'airportLayer', this.airportLayerMouseEnterHandler);
      this.map.on('mouseleave', 'airportLayer', this.airportLayerMouseLeaveHandler);
    } else if(this.selectionType === 'route') {
      this.map.off('click', 'airportLayer', this.airportLayerClickHandler);
      this.map.off('mouseenter', 'airportLayer', this.airportLayerMouseEnterHandler);
      this.map.off('mouseleave', 'airportLayer', this.airportLayerMouseLeaveHandler);
    }
  }

  airportLayerMouseEnterHandler = () => {
    this.map.getCanvas().style.cursor = 'pointer';
  }

  airportLayerMouseLeaveHandler = () => {
    this.map.getCanvas().style.cursor = '';
  }

  onAirportDisplayTypeChange(): void {
    this.renderAirports();
  }

  renderAirports(): void {
    //TODO optimize, check state before

    if(this.airportDisplayType === AirportDisplayType.ALL) {
      this.geoService.removeLayerFromMap(this.map, 'airportLayer');
      this.geoService.removeSourceFromMap(this.map, 'airportSource');

      this.geoService.addFeatureCollectionSourceToMap(this.map, 'airportSource', this.geoService.convertAirportsToGeoJson(this.dataStoreService.getCurrentlyDisplayedAirports()));
      this.geoService.addLayerTypeCircleToMap(this.map, 'airportLayer', 'airportSource', 8, '#eea719');
    } else if(this.airportDisplayType === AirportDisplayType.WITHROUTES) {
      this.geoService.removeLayerFromMap(this.map, 'airportLayer');
      this.geoService.removeSourceFromMap(this.map, 'airportSource');

      this.geoService.addFeatureCollectionSourceToMap(this.map, 'airportSource', this.geoService.convertAirportsToGeoJson(this.filterSerice.getAllAirportsPresentInFlightScheduleRouteDtos(this.dataStoreService.getCurrentlyDisplayedRoutes())));
      this.geoService.addLayerTypeCircleToMap(this.map, 'airportLayer', 'airportSource', 8, '#eea719');
    } else if(this.airportDisplayType === AirportDisplayType.NONE) {
      this.geoService.removeLayerFromMap(this.map, 'airportLayer');
      this.geoService.removeSourceFromMap(this.map, 'airportSource');
    }

  }

  renderRoutes(): void {
    //TODO optimize, check state before
    if(this.routeDisplayType === RouteDisplayType.ALL) {
      this.geoService.removeLayerFromMap(this.map, 'routeLayer');
      this.geoService.removeSourceFromMap(this.map, 'routeSource');

      this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSource', this.geoService.convertFlightScheduleRouteDtosToGeoJson(this.dataStoreService.getCurrentlyDisplayedRoutes()));
      this.geoService.addLayerTypeLineToMap(this.map, 'routeLayer', 'routeSource', {}, {'line-color': '#ffffff', 'line-width': 2})

      this.renderAirports();
    } else if(this.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) {
      this.geoService.removeLayerFromMap(this.map, 'routeLayer');
      this.geoService.removeSourceFromMap(this.map, 'routeSource');

      this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSource', this.geoService.convertFlightScheduleRouteDtosToGeoJson(this.dataStoreService.getCurrentlyDisplayedRoutes()));
      this.geoService.addLayerTypeLineToMap(this.map, 'routeLayer', 'routeSource', {}, {'line-color': '#ffffff', 'line-width': 2})

      if(this.airportDisplayType != AirportDisplayType.ALL) this.renderAirports();
    }
  }

  onRouteDisplayTypeChange(): void {
    this.renderRoutes();
  }

  /*
  onOutgoingChange(): void {
    if(this.selectedAirport.iataAirportCode != "") {
      this.selectedAirportRoutes = this.filterSerice.getFlightScheduleRouteDtosByAirport(this.allFlightScheduleRouteDtos, this.selectedAirport, this.specificAirportRoutesIncoming, this.specificAirportRoutesOutgoing);
      this.airportInfoComponent.updateTable();
      if(this.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) this.onSpecificAirportChange();
    }
  }

  onIncomingChange(): void {
    if(this.selectedAirport.iataAirportCode != "") {
      this.selectedAirportRoutes = this.filterSerice.getFlightScheduleRouteDtosByAirport(this.allFlightScheduleRouteDtos, this.selectedAirport, this.specificAirportRoutesIncoming, this.specificAirportRoutesOutgoing);
      this.airportInfoComponent.updateTable();
      if(this.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) this.onSpecificAirportChange();
    }
  }
   */

  onRouteFilterTypeChange(): void {

  }

  onSliderChange(event: any): void {
    // Handle slider change
  }

  onSpecificAirportChange(): void {
    this.renderRoutes();
  }

  //this.histogramData = this.geoService.generateRouteDistanceArray(this.allFlightScheduleRouteDtos);

  protected readonly AirportDisplayType = AirportDisplayType;
}
