import {AircraftTimeFilterType, AirportDisplayType, RouteDisplayType} from "../enum";
import {RouteFilter} from "../../protos/filters";
import {RouteFilterType} from "../../protos/enums";

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

export class FlightSchedule {
  flightScheduleId: number;
  airline: string;
  operationPeriod: string;
  flightNumber: number;
  suffix: string;

  constructor(flightScheduleId?: number, airline?: string, operationPeriod?: string, flightNumber?: number, suffix?: string) {
    this.flightScheduleId = flightScheduleId || -1;
    this.airline = airline || '';
    this.operationPeriod = operationPeriod || '';
    this.flightNumber = flightNumber || -1;
    this.suffix = suffix || '';
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

export class DateRange {
  start: Date | null;
  end: Date | null;

  constructor(start: Date | null, end: Date | null) {
    this.start = start;
    this.end = end;
  }
}

export class TimeRange {
  start: number;
  end: number;
  inverted: boolean = false;

  constructor(start: number, end: number, inverted: boolean = false) {
    this.start = start;
    this.end = end;
    this.inverted = inverted;
  }
}

export class TimeFilter {
  dateRange: DateRange;
  timeRange: TimeRange;
  aircraftDepOrArrInTimeRange: AircraftTimeFilterType;
  includeDifferentDayDepartures: boolean;
  includeDifferentDayArrivals: boolean;

  constructor(dateRange: DateRange, timeRange: TimeRange, includeDifferentDayDepartures = false, includeDifferentDayArrivals = false, aircraftTimeFilter: AircraftTimeFilterType = AircraftTimeFilterType.ARRIVALANDDEPARTURE) {
    this.dateRange = dateRange;
    this.timeRange = timeRange;
    this.aircraftDepOrArrInTimeRange = aircraftTimeFilter;
    this.includeDifferentDayDepartures = includeDifferentDayDepartures;
    this.includeDifferentDayArrivals = includeDifferentDayArrivals;
  }
}

export class GeneralFilter {
  airportDisplayType: AirportDisplayType;
  routeDisplayType: RouteDisplayType;
  //TODO add custom routeFilterType object with value
    constructor(airportDisplayType: AirportDisplayType, routeDisplayType: RouteDisplayType) {
        this.airportDisplayType = airportDisplayType;
        this.routeDisplayType = routeDisplayType;
    }
}

//export class RouteFilter {
//  routeFilterType: RouteFilterType;
//  start: number;
//  end: number;
//
//  constructor(routeFilterType: RouteFilterType, start: number, end: number) {
//    this.routeFilterType = routeFilterType;
//    this.start = start;
//    this.end = end;
//  }
//}

export const DefaultTimeFilter = new TimeFilter(new DateRange(new Date(), null), new TimeRange(0, 1439), true, true, AircraftTimeFilterType.ARRIVALANDDEPARTURE);

export const DefaultGeneralFilter = new GeneralFilter(AirportDisplayType.ALL, RouteDisplayType.ALL);

export const DefaultRouteFilter = RouteFilter.create({start: 0, end: 100, routeFilterType: RouteFilterType.DISTANCE});
