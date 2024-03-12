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
  flightScheduleId: number;
  originAirport: Airport;
  destinationAirport: Airport;
  aircraftDepartureTimeUtc: number;
  aircraftDepartureTimeDateDiffUtc: number;
  aircraftArrivalTimeUtc: number;
  aircraftArrivalTimeDateDiffUtc: number;
  kilometerDistance: number;

  constructor(
    legId?: number,
    flightScheduleId?: number,
    originAirport?: Airport,
    destinationAirport?: Airport,
    aircraftDepartureTimeUtc?: number,
    aircraftDepartureTimeDateDiffUtc?: number,
    aircraftArrivalTimeUtc?: number,
    aircraftArrivalTimeDateDiffUtc?: number,
    kilometerDistance?: number
  ) {
    this.legId = legId || -1;
    this.flightScheduleId = flightScheduleId || -1;
    this.originAirport = originAirport || new Airport();
    this.destinationAirport = destinationAirport || new Airport();
    this.aircraftDepartureTimeUtc = aircraftDepartureTimeUtc || 0;
    this.aircraftDepartureTimeDateDiffUtc = aircraftDepartureTimeDateDiffUtc || 0;
    this.aircraftArrivalTimeUtc = aircraftArrivalTimeUtc || 0;
    this.aircraftArrivalTimeDateDiffUtc = aircraftArrivalTimeDateDiffUtc || 0;
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
  start: number;
  end: number;

  constructor(start: number, end: number) {
    this.start = start;
    this.end = end;
  }
}
