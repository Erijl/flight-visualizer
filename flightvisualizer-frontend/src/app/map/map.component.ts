import {Component, OnDestroy, OnInit} from '@angular/core';
import mapboxgl from 'mapbox-gl';
import {DefaultGeneralFilter, DefaultSelectedAirportFilter} from "../core/dto/airport";
import 'mapbox-gl/dist/mapbox-gl.css';
import {GeoService} from "../core/service/geo.service";
import {CursorStyles, DetailSelectionType, LayerType, MapEventType, ModeSelection, SourceType} from "../core/enum";
import {DataStoreService} from "../core/service/data-store.service";
import {Subscription} from "rxjs";
import {environment} from "../../environments/environment";
import {GeneralFilter, SelectedAirportFilter} from "../protos/filters";
import {AirportRender, LegRender} from "../protos/objects";

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
  selectionModeSubscription!: Subscription;

  // UI data
  generalFilter: GeneralFilter = GeneralFilter.create(DefaultGeneralFilter);

  // UI state
  selectedAirportFilter: SelectedAirportFilter = SelectedAirportFilter.create();
  selectedRoute: LegRender = LegRender.create();
  selectionType: DetailSelectionType = DetailSelectionType.AIRPORT;

  intervalCount = 0;
  liveFeedInterval: any;

  //popup = new mapboxgl.Popup({
  //  closeButton: false,
  //  closeOnClick: false
  //});

  constructor(private geoService: GeoService, private dataStoreService: DataStoreService) {
  }

  ngOnInit(): void {
    this.currentlyRenderedAirportsSubscription = this.dataStoreService.currentlyDisplayedAirports.subscribe(airports => {
      this.replaceCurrentlyRenderedAirports(airports);
    });

    this.currentlyRenderedRoutesSubscription = this.dataStoreService.renderedRoutes.subscribe(routes => {
      this.replaceCurrentlyRenderedRoutes(routes);
    });

    this.selectedRouteSubscription = this.dataStoreService.selectedRoute.subscribe(route => {
      this.selectedRoute = route;

      this.highlightSelectedRoute();
    });

    this.selectedAirportSubscription = this.dataStoreService.selectedAirportFilter.subscribe(selectedAirportFilter => {
      this.selectedAirportFilter = selectedAirportFilter;

      this.highlightSelectedAirport();
    });

    this.generalFilterSubscription = this.dataStoreService.generalFilter.subscribe(generalFilter => {
      this.generalFilter = generalFilter;
    });

    this.detailSelectionTypeSubscription = this.dataStoreService.detailSelectionType.subscribe((type: DetailSelectionType) => {
      this.selectionType = type;
      this.onSelectionTypeChange();
    });

    //this.selectionModeSubscription = this.dataStoreService.modeSelection.subscribe(mode => {
    //  if(mode == ModeSelection.LIVE_FEED) {
    //    this.intervalCount = 0;
    //  } else {
    //    clearInterval(this.liveFeedInterval);
    //  }
    //});


    mapboxgl.accessToken = environment.mapboxAccessToken;
    this.map = new mapboxgl.Map({
      container: 'map',
      style: 'mapbox://styles/mapbox/dark-v11',
      center: [-74.5, 40],
      zoom: 3
    });

    this.map.dragRotate.disable();
    this.map.touchZoomRotate.disableRotation();

    this.map.on('load', () => {
      this.dataStoreService.reRenderRoutes();

      this.map.resize();
    });

    // @ts-ignore
    this.map.on(MapEventType.CLICK, (e) => {
      const features = this.map.queryRenderedFeatures(e.point, {layers: [LayerType.AIRPORTLAYER, LayerType.ROUTELAYER]});

      if (!features.length) {
        if (this.selectionType === DetailSelectionType.AIRPORT) {
          this.selectedAirportFilter = SelectedAirportFilter.create();
          this.dataStoreService.setSelectedAirportFilter(this.selectedAirportFilter);
        } else if (this.selectionType === DetailSelectionType.ROUTE) {
          this.selectedRoute = LegRender.create();
          this.dataStoreService.setSelectedRoute(this.selectedRoute);
        }
      }
    });
  }

  onSelectionTypeChange(): void {
    if (this.selectionType === DetailSelectionType.AIRPORT) {
      this.dataStoreService.setSelectedRoute(LegRender.create());

      this.enableAirportLayerSelection();
    } else if (this.selectionType === DetailSelectionType.ROUTE) {
      this.dataStoreService.setSelectedAirportFilter(SelectedAirportFilter.create());

      this.enableRouteLayerSelection();
    }
  }

  // @ts-ignore
  airportLayerClickHandler = (e) => {
    // @ts-ignore
    const clickedAirport = e.features[0];
    const selectedAirport = this.dataStoreService.getAllAirports().find(airport => airport.iataCode === clickedAirport.properties.iataAirportCode);
    if (selectedAirport && selectedAirport.iataCode != '' && selectedAirport.iataCode != this.selectedAirportFilter.iataCode) {
      this.selectedAirportFilter = SelectedAirportFilter.create(DefaultSelectedAirportFilter);
      this.selectedAirportFilter.iataCode = selectedAirport.iataCode;

      this.dataStoreService.setSelectedAirportFilter(this.selectedAirportFilter);
    }
  };

  // @ts-ignore
  routeLayerClickHandler = (e) => {
    const clickedRoute = e.features[0];
    const clickedLeg = this.dataStoreService.getAllLegRenders().find(leg =>
      leg.originAirportIataCode === clickedRoute.properties.originAirport &&
      leg.destinationAirportIataCode === clickedRoute.properties.destinationAirport
    );

    if(clickedLeg) {
      this.selectedRoute = clickedLeg;
      this.dataStoreService.setSelectedRoute(this.selectedRoute);
    }
  }

  // @ts-ignore
  layerMouseEnterHandler = (e) => {
    this.map.getCanvas().style.cursor = CursorStyles.POINTER;

    const coordinates = e.features[0].geometry.coordinates.slice();

    while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
      coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
    }

    const properties = e.features[0].properties;
    //this.popup.setLngLat(coordinates).setHTML(properties.iataAirportCode + ' - ' + properties.airportName).addTo(this.map);
  }

  // @ts-ignore
  layerMouseLeaveHandler = (e) => {
    this.map.getCanvas().style.cursor = CursorStyles.DEFAULT;
    //this.popup.remove();
  }

  highlightSelectedRoute(): void {
    this.geoService.removeLayerFromMap(this.map, LayerType.ROUTEHIGHLIGHTLAYER);
    this.geoService.removeSourceFromMap(this.map, SourceType.ROUTEHIGHLIGHTSOURCE);

    if (this.selectedRoute.originAirportIataCode != '' && this.selectedRoute.destinationAirportIataCode != '') {
      this.geoService.highlightRouteOnMap(this.map, SourceType.ROUTEHIGHLIGHTSOURCE, LayerType.ROUTEHIGHLIGHTLAYER, this.selectedRoute); //TODO overhaul
    }
  }

  highlightSelectedAirport(): void {
    this.geoService.removeLayerFromMap(this.map, LayerType.AIRPORTHIGHLIGHTLAYER);
    this.geoService.removeSourceFromMap(this.map, SourceType.AIRPORTHIGHLIGHTSOURCE);

    const airportRender = this.dataStoreService.getAirportRenderByIataCode(this.selectedAirportFilter.iataCode);
    if (airportRender && airportRender.iataCode != '') {
      this.geoService.highlightAirportOnMap(this.map, SourceType.AIRPORTHIGHLIGHTSOURCE, LayerType.AIRPORTHIGHLIGHTLAYER, airportRender);
    }
  }

  replaceCurrentlyRenderedAirports(newAirports: AirportRender[]): void {
    let airportsGeoJson = this.geoService.convertAirportRendersToGeoJson(newAirports);
    if(!this.map) return;

    if(this.map.getSource(SourceType.AIRPORTSOURCE)) {
      this.geoService.updateMapSourceData(this.map, SourceType.AIRPORTSOURCE, airportsGeoJson);
    } else {
      this.geoService.addFeatureCollectionSourceToMap(this.map, SourceType.AIRPORTSOURCE, airportsGeoJson);
      this.geoService.addLayerTypeCircleToMap(this.map, LayerType.AIRPORTLAYER, SourceType.AIRPORTSOURCE, this.dataStoreService.getModeSelection() == ModeSelection.SANDBOX ? 8 : 5, '#eea719'); //TODO replace shitty if with proper mode objects that store such things
    }

    if (this.selectionType === DetailSelectionType.AIRPORT) {
      this.enableAirportLayerSelection();
    }
  }

  replaceCurrentlyRenderedRoutes(newRoutes: LegRender[]): void {
    if(this.dataStoreService.getModeSelection() == ModeSelection.SANDBOX) { //TODO refactor, to complex method
      let routesGeoJson = this.geoService.convertLegRendersToGeoJson(newRoutes);
      if(!this.map) return;

      if(this.map.getSource(SourceType.ROUTESOURCE)) {
        this.geoService.updateMapSourceData(this.map, SourceType.ROUTESOURCE, routesGeoJson);
      } else {
        this.geoService.addFeatureCollectionSourceToMap(this.map, SourceType.ROUTESOURCE, routesGeoJson);
        this.geoService.addLayerTypeLineToMap(this.map, LayerType.ROUTELAYER, SourceType.ROUTESOURCE, {}, {
          'line-color': '#ffffff',
          'line-width': 2,
          'line-opacity': 0.9,
          //'line-dasharray': [2, 2]
        });
      }

      if (this.selectionType === DetailSelectionType.ROUTE) {
        this.enableRouteLayerSelection();
      }
    } else if(this.dataStoreService.getModeSelection() == ModeSelection.LIVE_FEED) { //TODO (possibly) base the speed (interval timeout & time multiplier) on the zoom level
      let date = new Date();
      this.liveFeedInterval = setInterval(() => {
        var newDateObj = new Date(date.getTime() + this.intervalCount*30000);
        let airplanesGeoJson = this.geoService.convertLegRendersToLiveFeedGeoJson(newRoutes, newDateObj);
        if(!this.map) return;

        if(this.map.getSource(SourceType.ROUTESOURCE)) {
          this.geoService.updateMapSourceData(this.map, SourceType.ROUTESOURCE, airplanesGeoJson);
        } else {
          this.geoService.addFeatureCollectionSourceToMap(this.map, SourceType.ROUTESOURCE, airplanesGeoJson);
          this.geoService.addLayerTypeAirplane(this.map, LayerType.ROUTELAYER, SourceType.ROUTESOURCE);
        }

        this.intervalCount++;
      }, 10)
    }
  }

  enableRouteLayerSelection(): void {
    this.disableLayerSelection();
    if(!this.map) return;
    this.map.on(MapEventType.CLICK, LayerType.ROUTELAYER, this.routeLayerClickHandler);
    this.map.on(MapEventType.MOUSEENTER, LayerType.ROUTELAYER, this.layerMouseEnterHandler);
    this.map.on(MapEventType.MOUSELEAVE, LayerType.ROUTELAYER, this.layerMouseLeaveHandler);
  }

  enableAirportLayerSelection(): void {
    this.disableLayerSelection();
    if(!this.map) return;
    this.map.on(MapEventType.CLICK, LayerType.AIRPORTLAYER, this.airportLayerClickHandler);
    this.map.on(MapEventType.MOUSEENTER, LayerType.AIRPORTLAYER, this.layerMouseEnterHandler);
    this.map.on(MapEventType.MOUSELEAVE, LayerType.AIRPORTLAYER, this.layerMouseLeaveHandler);
  }

  disableLayerSelection(): void {
    if(!this.map) return;
    this.map.off(MapEventType.CLICK, LayerType.ROUTELAYER, this.routeLayerClickHandler);
    this.map.off(MapEventType.MOUSEENTER, LayerType.ROUTELAYER, this.layerMouseEnterHandler);
    this.map.off(MapEventType.MOUSELEAVE, LayerType.ROUTELAYER, this.layerMouseLeaveHandler);

    this.map.off(MapEventType.CLICK, LayerType.AIRPORTLAYER, this.airportLayerClickHandler);
    this.map.off(MapEventType.MOUSEENTER, LayerType.AIRPORTLAYER, this.layerMouseEnterHandler);
    this.map.off(MapEventType.MOUSELEAVE, LayerType.AIRPORTLAYER, this.layerMouseLeaveHandler);
  }

  ngOnDestroy(): void {
    this.currentlyRenderedAirportsSubscription.unsubscribe();
    this.currentlyRenderedRoutesSubscription.unsubscribe();
    this.selectedRouteSubscription.unsubscribe();
    this.selectedAirportSubscription.unsubscribe();
    this.generalFilterSubscription.unsubscribe();
    this.detailSelectionTypeSubscription.unsubscribe();
    this.selectionModeSubscription.unsubscribe();
  }
}
