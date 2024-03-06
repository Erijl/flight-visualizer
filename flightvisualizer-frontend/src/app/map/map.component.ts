import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
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

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit, AfterViewInit{

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

  specificAirportRoutesOutgoing: boolean = true;
  specificAirportRoutesIncoming: boolean = true;

  // UI state
  specificAirportRoutesContainerVisible: boolean = false;
  histogramData: number[] = [];
  selectedAirport: Airport = new Airport();
  selectedAirportRoutes: FlightScheduleRouteDto[] = [];


  lowerValue = 10;
  upperValue = 90;
  minValue = 0;
  maxValue = 100;
  displayedColumns: string[] = ['origin', 'destination', 'distance'];
  dataSource = new MatTableDataSource<FlightScheduleRouteDto>(this.selectedAirportRoutes);

  // @ts-ignore
  @ViewChild(MatPaginator) paginator: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

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

      this.map.on('click', 'airportLayer', (e) => {
        // @ts-ignore
        const clickedAirport = e.features[0];
        // @ts-ignore
        console.log(this.allAirports.find(airport => airport.iataAirportCode === clickedAirport?.properties?.iataAirportCode));

        // @ts-ignore
        this.selectedAirport = this.allAirports.find(airport => airport.iataAirportCode === clickedAirport.properties.iataAirportCode);
        if(this.selectedAirport.iataAirportCode != "") {
          this.selectedAirportRoutes = this.filterSerice.getFlightScheduleRouteDtosByAirport(this.allFlightScheduleRouteDtos, this.selectedAirport, true, true);
          this.dataSource = new MatTableDataSource<FlightScheduleRouteDto>(this.selectedAirportRoutes);
          this.dataSource.paginator = this.paginator;
          if(this.routeDisplayType === RouteDisplayType.SPECIFICAIRPORT) this.onSpecificAirportChange();
        }
      });

      this.map.on('mouseenter', 'airportLayer', (e) => {
        this.map.getCanvas().style.cursor = 'pointer';
      });

      this.map.on('mouseleave', 'airportLayer', () => {
        this.map.getCanvas().style.cursor = '';
      });

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

      this.flightScheduleRouteDtosToDisplay = this.filterSerice.getFlightScheduleRouteDtosByAirport(this.allFlightScheduleRouteDtos, this.selectedAirport, this.specificAirportRoutesIncoming, this.specificAirportRoutesOutgoing);
      this.geoService.addFeatureCollectionSourceToMap(this.map, 'routeSource', this.geoService.convertFlightScheduleRouteDtosToGeoJson(this.flightScheduleRouteDtosToDisplay));
      this.geoService.addLayerTypeLineToMap(this.map, 'routeLayer', 'routeSource', {}, {'line-color': '#ffffff', 'line-width': 2})

      if(this.airportDisplayType != AirportDisplayType.ALL) this.renderAirports();
    }
  }

  onRouteDisplayTypeChange(): void {
    this.renderRoutes();
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

  onSpecificAirportChange(): void {
    this.renderRoutes();
  }

  getAirports(): void {
    this.dataService.getAirports().subscribe(airports => {
      this.allAirports = airports.filter(airport => airport.locationType === "Airport");
    });
  }

  getFlightScheduleLegRoutes(): void {
    this.dataService.getFlightScheduleLegRoutes().subscribe(flightScheduleLegs => {
      this.allFlightScheduleRouteDtos = flightScheduleLegs;
      this.flightScheduleRouteDtosToDisplay = flightScheduleLegs;
      this.histogramData = this.geoService.generateRouteDistanceArray(this.allFlightScheduleRouteDtos);
      console.log(this.histogramData);
    });
  }

  protected readonly AirportDisplayType = AirportDisplayType;
}
