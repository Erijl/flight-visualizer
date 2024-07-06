import {AircraftTimeFilterType, AirportDisplayType, RouteDisplayType, RouteFilterType} from "../../protos/enums";
import {DateRange, TimeRange} from "../../protos/objects";

//TODO temp, move to proto file
export interface Node {
  id: number;
  label: string;
}

export interface Edge {
  id: number;
  from: number;
  to: number;
}


export const DefaultTimeFilter = {dateRange: DateRange.create({start: new Date(), end: undefined}), timeRange: TimeRange.create({start: 0, end: 1439, inverted: false}), aircraftDepOrArrInTimeRange: AircraftTimeFilterType.ARRIVALANDDEPARTURE, includeDifferentDayDepartures: true, includeDifferentDayArrivals: true};

export const DefaultGeneralFilter = {airportDisplayType: AirportDisplayType.AIRPORTDISPLAYTYPE_ALL, routeDisplayType: RouteDisplayType.ROUTEDISPLAYTYPE_ALL};

export const DefaultRouteFilter = {start: 0, end: 20000, routeFilterType: RouteFilterType.DISTANCE};

export const DefaultSelectedAirportFilter = {iataCode: '', includingArrivals: true, includingDepartures: true};
