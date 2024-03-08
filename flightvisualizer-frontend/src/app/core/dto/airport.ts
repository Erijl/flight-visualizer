export class Airport {
  iataAirportCode: string;
  airportName: string;
  longitude: number;
  latitude: number;
  iataCityCode: string;
  isoCountryCode: string;
  locationType: string;
  offsetUtc: string;
  timezoneId: string;

  constructor() {
    this.iataAirportCode = '';
    this.airportName = '';
    this.longitude = 0;
    this.latitude = 0;
    this.iataCityCode = '';
    this.isoCountryCode = '';
    this.locationType = '';
    this.offsetUtc = '';
    this.timezoneId = '';
  }
}


export class FlightScheduleRouteDto {
  legId: number;
  originAirport: Airport;
  destinationAirport: Airport;
  kilometerDistance: number;

  constructor(legId?: number, originAirport?: Airport, destinationAirport?: Airport, kilometerDistance?: number) {
    this.legId = legId || -1;
    this.originAirport = originAirport || new Airport();
    this.destinationAirport = destinationAirport || new Airport();
    this.kilometerDistance = kilometerDistance || 0;
  }
}
