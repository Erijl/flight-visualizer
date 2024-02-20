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

  constructor(iataAirportCode: string, airportName: string, longitude: number, latitude: number, iataCityCode: string, isoCountryCode: string, locationType: string, offsetUtc: string, timezoneId: string) {
    this.iataAirportCode = iataAirportCode;
    this.airportName = airportName;
    this.longitude = longitude;
    this.latitude = latitude;
    this.iataCityCode = iataCityCode;
    this.isoCountryCode = isoCountryCode;
    this.locationType = locationType;
    this.offsetUtc = offsetUtc;
    this.timezoneId = timezoneId;
  }
}


export class FlightScheduleLeg {
  legId: number;
  originAirport: Airport;
  destinationAirport: Airport;

  constructor(legId: number, originAirport: Airport, destinationAirport: Airport) {
    this.legId = legId;
    this.originAirport = originAirport;
    this.destinationAirport = destinationAirport;
  }
}
