import {AircraftTimeFilterType, AirportDisplayType, RouteDisplayType, RouteFilterType} from "../../protos/enums";
import {DateRange, TimeRange} from "../../protos/objects";

//export class DateRange {
//  start: Date | null;
//  end: Date | null;
//
//  constructor(start: Date | null, end: Date | null) {
//    this.start = start;
//    this.end = end;
//  }
//}

//export class TimeRange {
//  start: number;
//  end: number;
//  inverted: boolean = false;
//
//  constructor(start: number, end: number, inverted: boolean = false) {
//    this.start = start;
//    this.end = end;
//    this.inverted = inverted;
//  }
//}
//
//export class TimeFilter {
//  dateRange: DateRange;
//  timeRange: TimeRange;
//  aircraftDepOrArrInTimeRange: AircraftTimeFilterType;
//  includeDifferentDayDepartures: boolean;
//  includeDifferentDayArrivals: boolean;
//
//  constructor(dateRange: DateRange, timeRange: TimeRange, includeDifferentDayDepartures = false, includeDifferentDayArrivals = false, aircraftTimeFilter: AircraftTimeFilterType = AircraftTimeFilterType.ARRIVALANDDEPARTURE) {
//    this.dateRange = dateRange;
//    this.timeRange = timeRange;
//    this.aircraftDepOrArrInTimeRange = aircraftTimeFilter;
//    this.includeDifferentDayDepartures = includeDifferentDayDepartures;
//    this.includeDifferentDayArrivals = includeDifferentDayArrivals;
//  }
//}

//export class GeneralFilter {
//  airportDisplayType: AirportDisplayType;
//  routeDisplayType: RouteDisplayType;
//  //TODO add custom routeFilterType object with value
//    constructor(airportDisplayType: AirportDisplayType, routeDisplayType: RouteDisplayType) {
//        this.airportDisplayType = airportDisplayType;
//        this.routeDisplayType = routeDisplayType;
//    }
//}

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

export const DefaultTimeFilter = {dateRange: DateRange.create({start: new Date(), end: undefined}), timeRange: TimeRange.create({start: 0, end: 1439, inverted: false}), aircraftDepOrArrInTimeRange: AircraftTimeFilterType.ARRIVALANDDEPARTURE, includeDifferentDayDepartures: true, includeDifferentDayArrivals: true};

export const DefaultGeneralFilter = {airportDisplayType: AirportDisplayType.AIRPORTDISPLAYTYPE_ALL, routeDisplayType: RouteDisplayType.ROUTEDISPLAYTYPE_ALL};

export const DefaultRouteFilter = {start: 0, end: 20000, routeFilterType: RouteFilterType.DISTANCE};

export const DefaultSelectedAirportFilter = {iataCode: '', includingArrivals: true, includingDepartures: true};
