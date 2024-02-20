import {Component, OnInit} from '@angular/core';
import mapboxgl from "mapbox-gl";
import {Airport, FlightScheduleLeg} from "../core/airport";
import {DataService} from "../core/data.service";
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

  ngOnInit(): void {
    this.getAirports();
    this.getFlightScheduleLegRoutes();

    mapboxgl.accessToken = 'pk.eyJ1IjoiZXJpamwiLCJhIjoiY2xza2JpemdmMDIzejJyczBvZGk2aG44eiJ9.eJkFfrXg1dGFasDJRkmnIg';
    const map = new mapboxgl.Map({
      container: 'map', // container id
      style: 'mapbox://styles/mapbox/dark-v11', // style URL
      center: [-74.5, 40], // starting position [lng, lat]
      zoom: 0 // starting zoom
    });

    // @ts-ignore
    map.on('load', () => {
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

        airportGeoFeatures.push({
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

      console.log(JSON.stringify(airportGeoFeatures));
      // @ts-ignore
      map.addSource('route', {
        'type': 'geojson',
        'data': {
          'type': 'FeatureCollection',
          // @ts-ignore

          'features': airportGeoFeatures
        }
      });


      map.addLayer({
        'id': 'route',
        'type': 'line',
        'source': 'route',
        'layout': {
          'line-join': 'round',
          'line-cap': 'round'
        },
        'paint': {
          'line-color': 'white',
          'line-width': 1
        },
      });

      map.addLayer({
        'id': 'park-volcanoes',
        'type': 'circle',
        'source': 'route',
        'paint': {
          'circle-radius': 6,
          'circle-color': '#B42222'
        },
        'filter': ['==', '$type', 'Point']
      });

      map.resize();
    });

    map.on('click', 'route', (e) => {
      console.log(e);
    });

    map.on('mouseenter', 'route', () => {
      map.getCanvas().style.cursor = 'pointer';
    });

    map.on('mouseleave', 'route', () => {
      map.getCanvas().style.cursor = '';
    });

    // Add navigation control (optional)
    map.addControl(new mapboxgl.NavigationControl());
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
