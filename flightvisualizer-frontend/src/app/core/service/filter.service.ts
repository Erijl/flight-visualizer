import {Injectable} from '@angular/core';
import {Airport, FlightScheduleRouteDto, TimeFilter} from "../dto/airport";
import {AircraftTimeFilterType} from "../enum";
import {RouteFilter} from "../../protos/filters";
import {RouteFilterType} from "../../protos/enums";

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  constructor() {
  }

  getAllAirportsPresentInFlightScheduleRouteDtos(allFlightScheduleRouteDtos: FlightScheduleRouteDto[]): Airport[] {
    const allAirports: any = [];
    allFlightScheduleRouteDtos.forEach((flightScheduleRouteDto: FlightScheduleRouteDto) => {
      allAirports.push(flightScheduleRouteDto.originAirport);
      allAirports.push(flightScheduleRouteDto.destinationAirport);
    });
    return allAirports;
  }

  getFlightScheduleRouteDtosByAirport(allFlightScheduleRouteDtos: FlightScheduleRouteDto[], airport: Airport, incomingRoutes: boolean, outgoingRoutes: boolean): FlightScheduleRouteDto[] {
    const allFlightScheduleRouteDtosByAirport: any = [];
    allFlightScheduleRouteDtos.forEach((flightScheduleRouteDto: FlightScheduleRouteDto) => {

      if (incomingRoutes && flightScheduleRouteDto.destinationAirport.iataAirportCode == airport.iataAirportCode) {
        allFlightScheduleRouteDtosByAirport.push(flightScheduleRouteDto);
      } else if (outgoingRoutes && flightScheduleRouteDto.originAirport.iataAirportCode == airport.iataAirportCode) {
        allFlightScheduleRouteDtosByAirport.push(flightScheduleRouteDto);
      }
    });
    return allFlightScheduleRouteDtosByAirport;
  }

  getCleanedFlightScheduleRouteDtos(flightScheduleRouteDtos: FlightScheduleRouteDto[]): FlightScheduleRouteDto[] {
    let cleanedRoutes: FlightScheduleRouteDto[] = [];

    flightScheduleRouteDtos.forEach(route => {
      if (!cleanedRoutes.find(cleanedRoute => this.compareFlightScheduleRouteDtos(route, cleanedRoute))) {
        cleanedRoutes.push(route);
      }
    })
    return cleanedRoutes;
  }

  getFlightRoutesInTimeFrame(flightScheduleRouteDtos: FlightScheduleRouteDto[], timeFilter: TimeFilter) {
    const minTime = 0;
    const maxTime = 1439;

    return flightScheduleRouteDtos.filter(route => {
      const isDifferentDayDeparture = timeFilter.includeDifferentDayDepartures && route.aircraftDepartureTimeDateDiffUtc >= 0;
      const isSameDayDeparture = !timeFilter.includeDifferentDayDepartures && route.aircraftDepartureTimeDateDiffUtc == 0;
      const isDifferentDayArrival = timeFilter.includeDifferentDayArrivals && route.aircraftArrivalTimeDateDiffUtc >= 0;
      const isSameDayArrival = !timeFilter.includeDifferentDayArrivals && route.aircraftArrivalTimeDateDiffUtc == 0;

      if ((isDifferentDayDeparture || isSameDayDeparture) && (isDifferentDayArrival || isSameDayArrival)) {
        switch (timeFilter.aircraftDepOrArrInTimeRange) {
          case AircraftTimeFilterType.ARRIVALANDDEPARTURE:
            return this.isRouteInTimeRange(route, timeFilter, minTime, maxTime);
          case AircraftTimeFilterType.ARRIVAL:
            return this.isArrivalInTimeRange(route, timeFilter, minTime);
          case AircraftTimeFilterType.DEPARTURE:
            return this.isDepartureInTimeRange(route, timeFilter, maxTime);
        }
      }
      return false;
    });
  }

  isRouteInTimeRange(route: FlightScheduleRouteDto, timeFilter: TimeFilter, minTime: number, maxTime: number): boolean {
    if (timeFilter.timeRange.inverted) {
      return minTime <= route.aircraftArrivalTimeUtc && route.aircraftArrivalTimeUtc <= timeFilter.timeRange.start &&
        timeFilter.timeRange.end <= route.aircraftDepartureTimeUtc && route.aircraftDepartureTimeUtc <= maxTime;
    } else {
      return timeFilter.timeRange.start <= route.aircraftArrivalTimeUtc && route.aircraftArrivalTimeUtc <= timeFilter.timeRange.end &&
        timeFilter.timeRange.start <= route.aircraftDepartureTimeUtc && route.aircraftDepartureTimeUtc <= timeFilter.timeRange.end;
    }
  }

  isArrivalInTimeRange(route: FlightScheduleRouteDto, timeFilter: TimeFilter, minTime: number): boolean {
    if (timeFilter.timeRange.inverted) {
      return minTime <= route.aircraftArrivalTimeUtc && route.aircraftArrivalTimeUtc <= timeFilter.timeRange.start;
    } else {
      return timeFilter.timeRange.start <= route.aircraftArrivalTimeUtc && route.aircraftArrivalTimeUtc <= timeFilter.timeRange.end;
    }
  }

  isDepartureInTimeRange(route: FlightScheduleRouteDto, timeFilter: TimeFilter, maxTime: number): boolean {
    if (timeFilter.timeRange.inverted) {
      return timeFilter.timeRange.end <= route.aircraftDepartureTimeUtc && route.aircraftDepartureTimeUtc <= maxTime;
    } else {
      return timeFilter.timeRange.start <= route.aircraftDepartureTimeUtc && route.aircraftDepartureTimeUtc <= timeFilter.timeRange.end;
    }
  }

  compareFlightScheduleRouteDtos(originalFlightRoute: FlightScheduleRouteDto, alteredFlightRoute: FlightScheduleRouteDto): boolean {
    return (
        originalFlightRoute.originAirport.iataAirportCode == alteredFlightRoute.originAirport.iataAirportCode &&
        originalFlightRoute.destinationAirport.iataAirportCode == alteredFlightRoute.destinationAirport.iataAirportCode
      )
      ||
      (
        originalFlightRoute.originAirport.iataAirportCode == alteredFlightRoute.destinationAirport.iataAirportCode &&
        originalFlightRoute.destinationAirport.iataAirportCode == alteredFlightRoute.originAirport.iataAirportCode
      );
  }

  getFLightScheduleRouteDtosWithinSameCountry(flightScheduleRouteDtos: FlightScheduleRouteDto[]): FlightScheduleRouteDto[] {
    return flightScheduleRouteDtos.filter(route => route.originAirport.isoCountryCode == route.destinationAirport.isoCountryCode);
  }

  getFLightScheduleRouteDtosWithinSameRegion(flightScheduleRouteDtos: FlightScheduleRouteDto[]): FlightScheduleRouteDto[] {
    return flightScheduleRouteDtos.filter(route => route.originAirport.timezoneId?.split('/')[0] == route.destinationAirport.timezoneId?.split('/')[0]);
  }

  getFLightScheduleRouteDtosWithinSameTimezone(flightScheduleRouteDtos: FlightScheduleRouteDto[]): FlightScheduleRouteDto[] {
    return flightScheduleRouteDtos.filter(route => route.originAirport.offsetUtc == route.destinationAirport.offsetUtc);
  }

  getFlightScheduleRouteDtosByRouteFilter(flightScheduleRouteDtos: FlightScheduleRouteDto[], routeFilter: RouteFilter): FlightScheduleRouteDto[] {
    return flightScheduleRouteDtos.filter(route => {
      switch (routeFilter.routeFilterType) {
        case RouteFilterType.DISTANCE:
          return route.kilometerDistance >= routeFilter.start && route.kilometerDistance <= routeFilter.end;
        case RouteFilterType.DURATION:
          const flightDurationMinutes = this.calculateFlightDurationInMinutes(route);
          return flightDurationMinutes >= routeFilter.start && flightDurationMinutes <= routeFilter.end;
      }
      return false;
    });
  }

  calculateFlightDurationInMinutes(flightScheduleRouteDto: FlightScheduleRouteDto): number {
    if(flightScheduleRouteDto.aircraftArrivalTimeDateDiffUtc >= 1) {

      //TODO check MBA -> ZNZ connection again
      if(flightScheduleRouteDto.originAirport.iataAirportCode == 'MBA' && flightScheduleRouteDto.destinationAirport.iataAirportCode == 'ZNZ') {
        return 50;
      }
      return (Math.floor(1439 - flightScheduleRouteDto.aircraftDepartureTimeUtc) + flightScheduleRouteDto.aircraftArrivalTimeUtc);
    } else {
      return Math.floor(flightScheduleRouteDto.aircraftArrivalTimeUtc - flightScheduleRouteDto.aircraftDepartureTimeUtc);
    }
  }
}
