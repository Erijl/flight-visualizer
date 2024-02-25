import {Injectable} from '@angular/core';
import mapboxgl from "mapbox-gl";
import {Airport} from "../dto/airport";

@Injectable({
  providedIn: 'root'
})
export class GeoService {

  constructor() {
  }

  addFeatureCollectionSourceToMap(map: mapboxgl.Map, sourceId: string, features: any): void {
    map.addSource(sourceId, {
      'type': 'geojson',
      'data': {
        'type': 'FeatureCollection',
        'features': features
      }
    });
  }

  addLayerTypeCircleToMap(map: mapboxgl.Map, layerId: string, sourceId: string, circleRadius: number, circleColor: string): void {
    map.addLayer({
      'id': layerId,
      'type': 'circle',
      'source': sourceId,
      'paint': {
        'circle-radius': circleRadius,
        'circle-color': circleColor
      }
    });
  }

  addLayerTypeLineToMap(map: mapboxgl.Map, layerId: string, sourceId: string, layout: mapboxgl.LineLayout, paint: mapboxgl.LinePaint): void {
    map.addLayer({
      'id': layerId,
      'type': 'line',
      'source': sourceId,
      'layout': layout,
      'paint': paint,
    });
  }

  convertAirportsToGeoJson(airports: Airport[]): any[] {
    const airportGeoFeatures: any[] = [];
    airports.forEach(airport => {
      airportGeoFeatures.push({
        'type': 'Feature',
        'geometry': {
          'type': 'Point',
          'coordinates': [airport.longitude, airport.latitude]
        },
        'properties': {
          'title': airport.airportName ? airport.airportName : airport.iataAirportCode,
          'icon': 'airport'
        }
      });
    });
    return airportGeoFeatures;
  }

  convertFlightScheduleRouteDtosToGeoJson(flightScheduleRouteDtos: any[]): any[] {
    const airportGeoRoutes: any[] = [];
    flightScheduleRouteDtos.forEach(flightScheduleRouteDto => {
      airportGeoRoutes.push({
        'type': 'Feature',
        'geometry': {
          'type': 'LineString',
          'coordinates': this.calculateOptimalLinePath(flightScheduleRouteDto.originAirport, flightScheduleRouteDto.destinationAirport)
        }
      });
    });
    return airportGeoRoutes;
  }

  calculateOptimalLinePath(origin: Airport, destination: Airport): number[][] {
    let originCopy: Airport = JSON.parse(JSON.stringify(origin));
    let destinationCopy: Airport = JSON.parse(JSON.stringify(destination));

    let diffLongitude = (destinationCopy.longitude - originCopy.longitude);
    if(Math.abs(diffLongitude) > 180) {
      if(diffLongitude > 0) {
        originCopy.longitude += 360;
      } else {
        destinationCopy.longitude += 360;
      }
    }

    return [
      [originCopy.longitude, originCopy.latitude],
      [destinationCopy.longitude, destinationCopy.latitude]
    ]
  }

  removeLayerFromMap(map: mapboxgl.Map, layerId: string): void {
    if(map.getLayer(layerId)) {
      map.removeLayer(layerId);
    }
  }

  removeSourceFromMap(map: mapboxgl.Map, sourceId: string): void {
    if(map.getSource(sourceId)) {
      map.removeSource(sourceId);
    }
  }

  generateRouteDistanceArray(flightScheduleRouteDtos: any[]): number[] {
    const routeDistances: number[] = [];
    flightScheduleRouteDtos.forEach(flightScheduleRouteDto => {
      let distance = this.calculateDistanceBetweenAirports(flightScheduleRouteDto.originAirport, flightScheduleRouteDto.destinationAirport);
      routeDistances.push(Number((distance / 1000).toFixed(0)));
      console.log(distance);
    });
    routeDistances.sort((a, b) => a - b);

    return routeDistances;
  }

  calculateDistanceBetweenAirports(origin: Airport, destination: Airport): number {
    const R = 6371e3; // metres
    const φ1 = origin.latitude * Math.PI/180; // φ, λ in radians
    const φ2 = destination.latitude * Math.PI/180;
    const Δφ = (destination.latitude-origin.latitude) * Math.PI/180;
    const Δλ = (destination.longitude-origin.longitude) * Math.PI/180;

    const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
      Math.cos(φ1) * Math.cos(φ2) *
      Math.sin(Δλ/2) * Math.sin(Δλ/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    return Math.floor(R * c); // in metres
  }
}
