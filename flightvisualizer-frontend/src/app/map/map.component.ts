import {Component, OnInit} from '@angular/core';
import mapboxgl from "mapbox-gl";
import {Airport, FlightScheduleLeg} from "../core/dto/airport";
import {DataService} from "../core/service/data.service";
import 'mapbox-gl/dist/mapbox-gl.css';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit {
  airports: Airport[] = [];
  flightScheduleLegs: FlightScheduleLeg[] = [];
  constructor(private dataService: DataService) { }

  generateAirportGeoFeatures(): any[] {
    let airportGeoFeatures = [];
    for(let i = 0; i < this.airports.length; i++) {
      airportGeoFeatures.push({
        'type': 'Feature',
        'geometry': {
          'type': 'Point',
          'coordinates': [this.airports[i].longitude, this.airports[i].latitude]
        }
      });
    }
    return airportGeoFeatures;
  }

  generateAirportGeoRoutes(): any[] {
    let airportGeoRoutes = [];
    for(let i = 0; i < this.flightScheduleLegs.length; i++) {
      let randomOrigin = this.flightScheduleLegs[i].originAirport;
      let randomDesination = this.flightScheduleLegs[i].destinationAirport;

      let diffLongitude = randomDesination.longitude - randomOrigin.longitude;
      let shouldCross180thMedian = Math.abs(diffLongitude) > 180;

      if(shouldCross180thMedian) {
        if(diffLongitude > 0) {
          randomOrigin.longitude += 360;
        } else {
          randomDesination.longitude += 360;
        }
      }

      airportGeoRoutes.push({
        'type': 'Feature',
        'geometry': {
          'type': 'LineString',
          'coordinates': [
            [randomOrigin.longitude, randomOrigin.latitude],
            [randomDesination.longitude, randomDesination.latitude]
          ]
        }
      });
    }
    return airportGeoRoutes;
  }

  addLayersToMap(map: any): void {
    map.addSource('airports', {
      'type': 'geojson',
      'data': {
        'type': 'FeatureCollection',
        'features': this.generateAirportGeoFeatures()
      }
    });

    map.addSource('routes', {
      'type': 'geojson',
      'data': {
        'type': 'FeatureCollection',
        'features': this.generateAirportGeoRoutes()
      }
    });

    map.addLayer({
      'id': 'airports',
      'type': 'circle',
      'source': 'airports',
      'paint': {
        'circle-radius': 6,
        'circle-color': '#B42222'
      },
      'filter': ['==', '$type', 'Point']
    });

    map.addLayer({
      'id': 'routes',
      'type': 'line',
      'source': 'routes',
      'layout': {
        'line-join': 'round',
        'line-cap': 'round'
      },
      'paint': {
        'line-color': 'white',
        'line-width': 1
      },
    });
  }

  handleMapEvents(map: any): void {
    map.on('click', 'routes', (e: any) => {
      console.log(e);
    });

    map.on('mouseenter', 'routes', () => {
      map.getCanvas().style.cursor = 'pointer';
    });

    map.on('mouseleave', 'routes', () => {
      map.getCanvas().style.cursor = '';
    });

    map.addControl(new mapboxgl.NavigationControl());
  }

  ngOnInit(): void {
    this.getAirports();
    this.getFlightScheduleLegRoutes();

    mapboxgl.accessToken = '';
    const map = new mapboxgl.Map({
      container: 'map', // container id
      style: 'mapbox://styles/mapbox/dark-v11', // style URL
      center: [-74.5, 40], // starting position [lng, lat]
      zoom: 0 // starting zoom
    });

    map.on('load', () => {
      this.addLayersToMap(map);
      map.resize();
      this.handleMapEvents(map);
    });
  }

  getAirports(): void {
    this.dataService.getAirports().subscribe(airports => {

      this.airports = airports;
    });
  }

  getFlightScheduleLegRoutes(): void {
    this.dataService.getFlightScheduleLegRoutes().subscribe(flightScheduleLegs => {
      this.flightScheduleLegs = flightScheduleLegs;
    });
  }
}
