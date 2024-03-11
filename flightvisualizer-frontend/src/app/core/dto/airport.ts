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

export class FlightDateFrequencyDto {
  startDateUtc: Date;
  count: number;

  constructor(startDateUtc?: Date, frequency?: number) {
    this.startDateUtc = startDateUtc || new Date();
    this.count = frequency || 0;
  }
}

export class SelectedDateRange {
  start: Date | null;
  end: Date | null;

  constructor(start: Date | null, end: Date | null) {
    this.start = start;
    this.end = end;
  }
}

export class SelectedTimeRange {
  start: number | null;
  end: number | null;

  constructor(start: number | null, end: number | null) {
    this.start = start;
    this.end = end;
  }
}
