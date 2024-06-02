import {Injectable} from '@angular/core';
import mapboxgl from 'mapbox-gl';
import {AirportRender, LegRender} from "../../protos/objects";

@Injectable({
  providedIn: 'root'
})
export class GeoService {

  constructor() {
  }

  highlightRouteOnMap(map: mapboxgl.Map, sourceId: string, layerId: string, leg: LegRender): void {
    this.addFeatureCollectionSourceToMap(map, sourceId, this.convertLegRendersToGeoJson([leg]));
    this.addLayerTypeLineToMap(map, layerId, sourceId, {
      'line-cap': 'round',
      'line-join': 'round'
    }, {
      'line-width': 5,
      'line-color': '#ff0000'
    });
  }

  highlightAirportOnMap(map: mapboxgl.Map, sourceId: string, layerId: string, airport: AirportRender): void {
    this.addFeatureCollectionSourceToMap(map, sourceId, this.convertAirportRendersToGeoJson([airport]));
    this.addLayerTypeCircleToMap(map, layerId, sourceId, 10, '#ff0000');
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

  convertAirportRendersToGeoJson(airports: AirportRender[]): any[] {
    const airportGeoFeatures: any[] = [];
    airports.forEach(airport => {
      airportGeoFeatures.push({
        'type': 'Feature',
        'geometry': {
          'type': 'Point',
          'coordinates': [airport.coordinate?.longitude, airport.coordinate?.latitude]
        },
        'properties': {
          'iataAirportCode': airport.iataCode,
          'airportName': 'ThisIsAnEnormousNameToCheckWhetherTheMaxWidthIsWorking',
        }
      });
    });
    return airportGeoFeatures;
  }

  convertLegRendersToGeoJson(legRenders: LegRender[]): any[] {
    const airportGeoRoutes: any[] = [];
    legRenders.forEach(legRender => {
      airportGeoRoutes.push({
        'type': 'Feature',
        'geometry': {
          'type': 'LineString',
          'coordinates': [
            [legRender.coordinates[0].longitude, legRender.coordinates[0].latitude],
            [legRender.coordinates[1].longitude, legRender.coordinates[1].latitude]
          ]
        },
        'properties': {
          'originAirport': legRender.originAirportIataCode,
          'destinationAirport': legRender.destinationAirportIataCode,
        }
      });
    });
    return airportGeoRoutes;
  }

  removeLayerFromMap(map: mapboxgl.Map, layerId: string): void {
    if(!map) return;
    if(map.getLayer(layerId)) {
      map.removeLayer(layerId);
    }
  }

  removeSourceFromMap(map: mapboxgl.Map, sourceId: string): void {
    if(!map) return;
    if(map.getSource(sourceId)) {
      map.removeSource(sourceId);
    }
  }

  updateMapSourceData(map: mapboxgl.Map, sourceId: string, features: any): void {
    // @ts-ignore
    map.getSource(sourceId).setData({
      'type': 'FeatureCollection',
      'features': features
    });
  }
}
