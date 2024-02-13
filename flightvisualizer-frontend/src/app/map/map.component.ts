import { Component, OnInit } from '@angular/core';
import mapboxgl from 'mapbox-gl';

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [

  ],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css',
})
export class MapComponent implements OnInit {
  constructor() { }

  ngOnInit(): void {
    mapboxgl.accessToken = '';
    const map = new mapboxgl.Map({
      container: 'map', // container id
      style: 'mapbox://styles/mapbox/dark-v11', // style URL
      center: [-74.5, 40], // starting position [lng, lat]
      zoom: 0 // starting zoom
    });

    const airports = [
      {"name": "John F. Kennedy International Airport", "lat": 40.6413, "long": -73.7781},
      {"name": "Los Angeles International Airport", "lat": 33.9416, "long": -118.4085},
      {"name": "Chicago O'Hare International Airport", "lat": 41.9742, "long": -87.9073},
      {"name": "London Heathrow Airport", "lat": 51.4700, "long": -0.4543},
      {"name": "Tokyo Haneda Airport", "lat": 35.5494, "long": 139.7798},
      {"name": "Paris Charles de Gaulle Airport", "lat": 49.0097, "long": 2.5479},
      {"name": "Beijing Capital International Airport", "lat": 40.0799, "long": 116.6031},
      {"name": "Dubai International Airport", "lat": 25.2528, "long": 55.3644},
      {"name": "Frankfurt Airport", "lat": 50.0344, "long": 8.5608},
      {"name": "Denver International Airport", "lat": 39.8561, "long": -104.6737},
      {"name": "Hong Kong International Airport", "lat": 22.3080, "long": 113.9185},
      {"name": "Singapore Changi Airport", "lat": 1.3644, "long": 103.9915},
      {"name": "Amsterdam Airport Schiphol", "lat": 52.3086, "long": 4.7639},
      {"name": "Shanghai Pudong International Airport", "lat": 31.1443, "long": 121.8083},
      {"name": "Sydney Kingsford Smith Airport", "lat": -33.9461, "long": 151.1772},
      {"name": "San Francisco International Airport", "lat": 37.6213, "long": -122.3790},
      {"name": "Incheon International Airport", "lat": 37.4602, "long": 126.4407},
      {"name": "Madrid Barajas Airport", "lat": 40.4720, "long": -3.5608},
      {"name": "Dallas/Fort Worth International Airport", "lat": 32.8998, "long": -97.0403},
      {"name": "Toronto Pearson International Airport", "lat": 43.6777, "long": -79.6248},
      {"name": "Munich Airport", "lat": 48.3537, "long": 11.7861},
      {"name": "Rome Leonardo da Vinci–Fiumicino Airport", "lat": 41.8003, "long": 12.2389},
      {"name": "Zurich Airport", "lat": 47.4647, "long": 8.5492},
      {"name": "Seoul Gimpo International Airport", "lat": 37.5583, "long": 126.7914},
      {"name": "Taiwan Taoyuan International Airport", "lat": 25.0777, "long": 121.2329},
      {"name": "Vienna International Airport", "lat": 48.1102, "long": 16.5697},
      {"name": "Barcelona–El Prat Airport", "lat": 41.2975, "long": 2.0808},
      {"name": "Oslo Gardermoen Airport", "lat": 60.1976, "long": 11.1004},
      {"name": "Copenhagen Airport", "lat": 55.6290, "long": 12.6475},
      {"name": "Brussels Airport", "lat": 50.9014, "long": 4.4844},
      {"name": "Melbourne Airport", "lat": -37.6690, "long": 144.8410},
      {"name": "Manchester Airport", "lat": 53.3650, "long": -2.2722},
      {"name": "Brisbane Airport", "lat": -27.3842, "long": 153.1176},
      {"name": "Perth Airport", "lat": -31.9406, "long": 115.9671},
      {"name": "Athens International Airport", "lat": 37.9364, "long": 23.9445},
      {"name": "São Paulo/Guarulhos–Governador André Franco Montoro International Airport", "lat": -23.4321, "long": -46.4702},
      {"name": "Moscow Sheremetyevo International Airport", "lat": 55.9736, "long": 37.4147},
      {"name": "Dublin Airport", "lat": 53.4277, "long": -6.2446},
      {"name": "Seattle–Tacoma International Airport", "lat": 47.4489, "long": -122.3094},
      {"name": "Las Vegas McCarran International Airport", "lat": 36.0851, "long": -115.1522},
      {"name": "Atlanta Hartsfield–Jackson International Airport", "lat": 33.6407, "long": -84.4277},
      {"name": "Miami International Airport", "lat": 25.7933, "long": -80.2906},
      {"name": "Orlando International Airport", "lat": 28.4294, "long": -81.3089},
      {"name": "Phoenix Sky Harbor International Airport", "lat": 33.4343, "long": -112.0116},
      {"name": "Boston Logan International Airport", "lat": 42.3656, "long": -71.0096},
      {"name": "Detroit Metropolitan Airport", "lat": 42.2123, "long": -83.3534},
      {"name": "Minneapolis–Saint Paul International Airport", "lat": 44.8820, "long": -93.2223},
      {"name": "Salt Lake City International Airport", "lat": 40.7899, "long": -111.9791},
      {"name": "Fort Lauderdale-Hollywood International Airport", "lat": 26.0725, "long": -80.1528},
      {"name": "Washington Dulles International Airport", "lat": 38.9531, "long": -77.4565},
      {"name": "Philadelphia International Airport", "lat": 39.8721, "long": -75.2437},
      {"name": "San Diego International Airport", "lat": 32.7336, "long": -117.1904},
      {"name": "Honolulu Daniel K. Inouye International Airport", "lat": 21.3187, "long": -157.9211},
      {"name": "Portland International Airport", "lat": 45.5898, "long": -122.5951},
      {"name": "Tampa International Airport", "lat": 27.9755, "long": -82.5333},
      {"name": "Newark Liberty International Airport", "lat": 40.6895, "long": -74.1745},
      {"name": "Houston George Bush Intercontinental Airport", "lat": 29.9902, "long": -95.3368},
      {"name": "Charlotte Douglas International Airport", "lat": 35.2144, "long": -80.9473},
      {"name": "Dubai World Central - Al Maktoum International Airport", "lat": 24.8978, "long": 55.1622},
      {"name": "Helsinki-Vantaa Airport", "lat": 60.3172, "long": 24.9633},
      {"name": "Stockholm Arlanda Airport", "lat": 59.6498, "long": 17.9239},
      {"name": "Doha Hamad International Airport", "lat": 25.2611, "long": 51.6131}
    ];

    // @ts-ignore
    map.on('load', function() {
      let airportGeoFeatures = [];
      for(let i = 0; i < airports.length; i++) {
        airportGeoFeatures.push({
          'type': 'Feature',
          'geometry': {
            'type': 'Point',
            'coordinates': [airports[i].long, airports[i].lat]
          }
        });
      }

      let airportGeoRoutes = [];
      for(let i = 0; i < 100; i++) {
        let randomOrigin = airports[Math.floor(Math.random() * airports.length)];
        let randomDesination = airports[Math.floor(Math.random() * airports.length)];

        airportGeoFeatures.push({
          'type': 'Feature',
          'geometry': {
            'type': 'LineString',
            'coordinates': [
              [randomOrigin.long, randomOrigin.lat],
              [randomDesination.long, randomDesination.lat]
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
}
