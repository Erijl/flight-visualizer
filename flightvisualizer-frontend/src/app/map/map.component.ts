import {Component, OnInit} from '@angular/core';
import mapboxgl from "mapbox-gl";
import {Airport} from "../core/airport";
import {DataService} from "../core/data.service";
import 'mapbox-gl/dist/mapbox-gl.css';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit {
  airports: Airport[] = [];
  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.getAirports();

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
      for(let i = 0; i < 1000; i++) {
        let randomOrigin = this.airports[Math.floor(Math.random() * this.airports.length)];
        let randomDesination = this.airports[Math.floor(Math.random() * this.airports.length)];

        let normalDistance = Math.abs(randomOrigin.longitude) + Math.abs(randomDesination.longitude);
        let distanceAcross = Math.abs(normalDistance - 360);

        let shouldCross180thMedian = distanceAcross < normalDistance;
        console.log(shouldCross180thMedian);

        if(shouldCross180thMedian) {
          if(randomDesination.longitude - randomOrigin.longitude >= 180) {
            randomDesination.longitude -= 360;
          } else if(randomDesination.longitude - randomOrigin.longitude < 180) {
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
}
